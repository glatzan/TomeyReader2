package eu.glatz.tomeyreader

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.Point
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.DataBufferInt
import java.awt.image.Raster
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import javax.imageio.ImageIO
import kotlin.math.ceil

class FileReader(private val settings: Settings, private val fileSettings: FileTagSettings, private val postProcessor: PostProcessor? = null) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun run() {
        val files = getFiles(settings.getAbsoluteDataFolder, settings.fileExtensions)
        validateInputOutput(files)
        readFiles(files)
    }

    private fun readFiles(files: Array<File>) {
        files.forEach { readFile(it) }
    }

    private fun readFile(file: File) {

        val bytes = readByteArray(file)
        val imageSettings = getImageSettings(bytes)

        val copyArray = ByteArray(imageSettings.imageSize)
        val imageArray = IntArray(copyArray.size / imageSettings.bytesPerPixel)
        val resultIMG = BufferedImage(imageSettings.xResolution, imageSettings.yResolution, 11)

        var imageCount = 0
        var imageOffset = imageSettings.startOffset

        val resultImgs = mutableListOf<File>()

        while ((imageOffset + imageSettings.imageSize) < bytes.size && imageCount < imageSettings.imageCount) {

            logger.info("Reading img ${imageCount + 1} of ${imageSettings.imageCount} from file: ${file.path}")
//            val imageBytes = readNextImage(imageOffset, imageSettings.imageSize, bytes, copyArray)
//            val resultImg = byteArrayToImage(imageBytes, imageArray, resultIMG, imageSettings.bytesPerPixel)
//
            val targetFile = getTargetFile(imageCount, file, ".${fileSettings.targetImageFormat}")
            resultImgs.add(targetFile)
//
//            writeImage(targetFile, resultIMG)
//
//            imageOffset += imageSettings.imageSize
            imageCount++
        }

        postProcessor?.run(resultImgs.toTypedArray())
    }

    private fun getImageSettings(byteContent: ByteArray): ImageSettings {
        try {
            val result = ImageSettings()
            result.xResolution = if (settings.xResolution == -1) findTag(fileSettings.width, fileSettings.lineBreak, byteContent).second.toInt() else settings.xResolution
            result.yResolution = if (settings.yResolution == -1) findTag(fileSettings.height, fileSettings.lineBreak, byteContent).second.toInt() else settings.yResolution
            result.imageCount = if (settings.imageCount == -1) findTag(fileSettings.imageCount, fileSettings.lineBreak, byteContent).second.toInt() else settings.imageCount
            result.bytesPerPixel = if (settings.bytesPerPixel == -1) {
                val bitsPerPixel = findTag(fileSettings.bitsPerPixel, fileSettings.lineBreak, byteContent).second.toDouble()
                (ceil(bitsPerPixel / 8)).toInt()
            } else settings.bytesPerPixel
            result.startOffset = if (settings.startOffset == -1) findImageStart(byteContent) else settings.startOffset

            return result
        } catch (e: Exception) {
            throw java.lang.IllegalStateException("Could not read or initialize Image settings")
        }
    }

    private fun validateInputOutput(files: Array<File>) {
        require(!files.isNullOrEmpty()) { "No Files to process found!" }

        val target = settings.getAbsoluteTargetFolder

        if (!target.exists())
            target.mkdirs()
        else require(target.isDirectory) { "Target is no folder" }
    }

    private fun readByteArray(file: File): ByteArray {
        return Files.readAllBytes(file.toPath())
    }

    private fun findImageStart(byteContent: ByteArray): Int {
        val fileName = findTag(fileSettings.fileName, fileSettings.lineBreak, byteContent, 0)
        val imageOffset = findOffset(fileName.second + fileSettings.nullChar + fileSettings.nullChar, byteContent, fileName.first)
        if (imageOffset != -1)
            return imageOffset + fileSettings.imageOffset - 1
        else
            throw IllegalStateException("Image Offset not fount!")
    }

    private fun findTag(prefix: String, suffix: String, byteContent: ByteArray, startOffset: Int = 0): Pair<Int, String> {
        val tagIndex = findOffset(prefix, byteContent, startOffset)

        require(tagIndex != -1) { "Tag not Found: ${prefix}" }

        val tagContent = StringBuffer()
        var suffixCount = 0

        for (i in tagIndex + 1 until byteContent.size) {
            if (byteContent[i].toChar() == suffix[suffixCount]) {
                if (suffixCount == suffix.length - 1) {
                    return Pair(i, tagContent.toString())
                }

                suffixCount++
            } else {
                suffixCount = 0
                tagContent.append(byteContent[i].toChar())
            }
        }

        return Pair(-1, tagContent.toString())
    }

    private fun findOffset(searchTag: String, byteContent: ByteArray, startOffset: Int = 0): Int {
        var searchOffsetCount = 0

        for (i in startOffset until byteContent.size) {
            if (byteContent[i].toChar() == searchTag[searchOffsetCount]) {
                if (searchOffsetCount == searchTag.length - 1) {
                    return i
                }
                searchOffsetCount++
            } else {
                searchOffsetCount = 0
            }
        }

        return -1
    }

    private fun getTargetFile(count: Int, file: File, newExtension: String): File {
        var targetFolder = settings.getAbsoluteTargetFolder

        if (settings.createNewDirForFile)
            targetFolder = File(targetFolder, file.name.substringBeforeLast("."))

        if (!targetFolder.exists())
            targetFolder.mkdirs()

        return File(targetFolder, file.name.substringBeforeLast(".") + "-" + count.toString() + newExtension)
    }

    private fun readNextImage(offset: Int, size: Int, bytes: ByteArray, copyArray: ByteArray): ByteArray {
        System.arraycopy(bytes, offset, copyArray, 0, size)
        return copyArray
    }

    private fun byteArrayToImage(copyArray: ByteArray, targetArray: IntArray, img: BufferedImage, bytesPerPixel: Int): BufferedImage {

        var byteArrayCounter = 0
        var byteArrayEmpty = false

        for (i in 0 until targetArray.size) {
            targetArray[i] = 0

            if (!byteArrayEmpty)
                for (y in 0 until bytesPerPixel) {
                    if (byteArrayCounter >= copyArray.size) {
                        byteArrayEmpty = true
                        break
                    }

                    targetArray[i] = targetArray[i] or (copyArray[byteArrayCounter].toUByte().toInt() shl y * 7)
                    byteArrayCounter++
                }

        }

        img.data = Raster.createRaster(img.sampleModel, DataBufferInt(targetArray, targetArray.size) as DataBuffer, Point())

        return img
    }

    private fun writeImage(target: File, img: BufferedImage) {
        ImageIO.write(img, "png", target)
    }

    companion object {
        fun getFiles(baseFolder: File, fileExtension: String): Array<File> {
            val t = baseFolder.listFiles()
            return baseFolder.listFiles(FileFilter { x -> x.name.endsWith(fileExtension) }) ?: emptyArray()
        }
    }


    class ImageSettings() {
        var xResolution = 0

        var yResolution = 0

        var imageCount = 0

        var bytesPerPixel = 0

        var startOffset = 0

        val imageSize: Int
            get() = xResolution * yResolution * bytesPerPixel
    }
}
