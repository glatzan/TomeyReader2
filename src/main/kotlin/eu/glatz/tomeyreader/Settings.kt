package eu.glatz.tomeyreader

import picocli.CommandLine
import java.io.File
import java.net.URLDecoder


@CommandLine.Command(name = "settings", mixinStandardHelpOptions = true)
class Settings {

    @CommandLine.Option(names = arrayOf("-sourceFolder"), description = arrayOf("Path containing .exame files"))
    var dataFolder: String = ""

    @CommandLine.Option(names = arrayOf("-fileExtension"), description = arrayOf("Extension of exame files"))
    var fileExtensions: String = ".exam"

    @CommandLine.Option(names = arrayOf("-targetFolder"), description = arrayOf("TargetFolder"))
    var targetFolder: String = "export"

    @CommandLine.Option(names = arrayOf("-d"), description = arrayOf("Create new dir for every exame file"))
    var createNewDirForFile: Boolean = true

    @CommandLine.Option(names = arrayOf("-x"), description = arrayOf("x Resolution"))
    var xResolution = 512

    @CommandLine.Option(names = arrayOf("-y"), description = arrayOf("Y Resolution"))
    var yResolution = 900

    @CommandLine.Option(names = arrayOf("-z"), description = arrayOf("Image count"))
    var imageCount = 256

    @CommandLine.Option(names = arrayOf("-z"), description = arrayOf("Pixel Depth"))
    var bytesPerPixel = 2

    @CommandLine.Option(names = arrayOf("-offset"), description = arrayOf("FileOffset"))
    var startOffset = -1

    @CommandLine.Option(names = arrayOf("-macro"), description = arrayOf("Macro for postprocessing"))
    var postProcessMacro: String = ""

    @CommandLine.Option(names = arrayOf("-imagejPlugins"), description = arrayOf("Macro for postprocessing"))
    var postProcessPluginDir: String = ""

    val imageSize: Int
        get() = xResolution * yResolution * bytesPerPixel

    val runDirectory: String
        get() {
            val path: String = Settings::class.java.protectionDomain.codeSource.location.file
            return URLDecoder.decode(path, "UTF-8")
        }

    fun validate(): Boolean {
        var dataSource = File(dataFolder)

        if (!dataSource.isAbsolute)
            dataSource = File(runDirectory, dataFolder)

        if (!dataSource.isDirectory)
            throw IllegalArgumentException("Source folder not valid! ${dataSource.path}")

        if (fileExtensions.isEmpty() || fileExtensions.matches(Regex("\\..*")))
            throw IllegalArgumentException("File-Extension not valid, must match .xxxx is ${fileExtensions} ")

        var target = File(targetFolder)

        if (!target.isAbsolute)
            target = File(runDirectory, targetFolder)

        if (!target.isDirectory)
            throw IllegalArgumentException("Source folder not valid! ${target.path}")

        if (postProcessMacro.isNotEmpty() && !File(postProcessMacro).isFile)
            throw IllegalArgumentException("Makro not found: $postProcessMacro")

        if(postProcessMacro.isNotEmpty() && postProcessPluginDir.isNotEmpty() && !File(postProcessPluginDir).isDirectory)
            throw IllegalArgumentException("ImageJ Plugin dir ist not valid: $postProcessPluginDir")

        return true
    }
}
