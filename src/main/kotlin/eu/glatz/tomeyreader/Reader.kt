package eu.glatz.tomeyreader

import java.awt.Point
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.DataBufferInt
import java.awt.image.Raster
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import javax.imageio.ImageIO

class Reader(private val settings: CmdSettings, private val fileSettings: FileTagSettings) {


    fun getFiles(): Array<File> {
        val base = File(settings.dataFolder)
        val files = base.listFiles(FileFilter { x -> x.endsWith(settings.fileExtensions) })
        return files
    }

    fun validateInputOutpu(files: Array<File>) {
        require(!files.isNullOrEmpty()) { "No Files to process found!" }

        val target = File(settings.targetFolder)

        if (!target.exists())
            target.mkdirs()
        else if (!target.isDirectory)
            throw IllegalArgumentException("Target is no folder")
    }

    fun readFile(file: File) {

        val copyArray = ByteArray(settings.imageSize)
        val imageArray = IntArray(copyArray.size / settings.bytesPerPixel)
        val resultIMG = BufferedImage(settings.xResolution, settings.yResolution, 11)

        val bytes = read(file)
        val offset = if (settings.startOffset == -1)
            findStartOffset(bytes)
        else
            settings.startOffset

        var imageCount = 0
        var imageOffset = offset

        while ((imageOffset + settings.imageSize) < bytes.size && imageCount < settings.maxImageCount) {
            val imageBytes = readNextImage(imageOffset, settings.imageSize, bytes, copyArray)
            val resultImg = byteArrayToImage(imageBytes, imageArray, resultIMG, settings.bytesPerPixel)

            val targetFile = getTargetFile(imageCount, file, ".png")

            writeImage(targetFile, resultIMG)

            imageOffset += settings.imageSize
            imageCount++
        }


    }


    private fun read(file: File): ByteArray {
        return Files.readAllBytes(file.toPath())
    }

    private fun findStartOffset(arr: ByteArray): Int {

    }

    private fun findImageStart(byteContent: ByteArray){

    }

    private fun findTag(prefix: String, suffix: String, byteContent: ByteArray, startOffset: Int): Pair<Int, String> {
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
        var path = settings.targetFolder + if (settings.targetFolder.endsWith("/")) "" else "/"

        if (settings.createNewDirForFile)
            path += file.name.substringBeforeLast(".") + "/"

        val targetFolder = File(path)

        if (!targetFolder.exists())
            targetFolder.mkdirs()

        path += file.name.substringBeforeLast(".") + "-" + count.toString() + newExtension

        return File(path)
    }

    private fun readNextImage(offset: Int, size: Int, bytes: ByteArray, copyArray: ByteArray): ByteArray {
        System.arraycopy(bytes, offset, copyArray, 0, size)
        return bytes
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

}
