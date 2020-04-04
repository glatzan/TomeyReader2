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
    var xResolution = -1

    @CommandLine.Option(names = arrayOf("-y"), description = arrayOf("Y Resolution"))
    var yResolution = -1

    @CommandLine.Option(names = arrayOf("-imageCount"), description = arrayOf("Image count"))
    var imageCount = -1

    @CommandLine.Option(names = arrayOf("-z"), description = arrayOf("Pixel Depth"))
    var bytesPerPixel = -1

    @CommandLine.Option(names = arrayOf("-offset"), description = arrayOf("FileOffset"))
    var startOffset = -1

    @CommandLine.Option(names = arrayOf("-macro"), description = arrayOf("Macro for postprocessing"))
    var postProcessMacro: String = ""

    @CommandLine.Option(names = arrayOf("-postPlugins"), description = arrayOf("Macro for postprocessing"))
    var postProcessPluginDir: String = ""

    val runDirectory: String
        get() {
            val path: String = Settings::class.java.protectionDomain.codeSource.location.file
            return URLDecoder.decode(path, "UTF-8")
        }


    val getAbsoluteDataFolder: File
        get() {
            return if (!File(dataFolder).isAbsolute)
                File(runDirectory, dataFolder)
            else
                File(dataFolder)
        }

    val getAbsoluteTargetFolder: File
        get() {
            return if (!File(targetFolder).isAbsolute)
                File(runDirectory, targetFolder)
            else
                File(targetFolder)
        }

    fun validate(): Boolean {

        require(getAbsoluteDataFolder.isDirectory) { "Source folder not valid! ${dataFolder}" }

        require(!(fileExtensions.isEmpty() || !fileExtensions.matches(Regex("\\..*")))) { "File-Extension not valid, must match .xxxx is ${fileExtensions} " }

        if (!getAbsoluteTargetFolder.isDirectory)
            require(getAbsoluteTargetFolder.mkdirs()) { "Target folder not valid! ${targetFolder}" }

        require(!(postProcessMacro.isNotEmpty() && !File(postProcessMacro).isFile)) { "Makro not found: $postProcessMacro" }

        require(!(postProcessMacro.isNotEmpty() && postProcessPluginDir.isNotEmpty() && !File(postProcessPluginDir).isDirectory)) { "ImageJ Plugin dir ist not valid: $postProcessPluginDir" }

        return true
    }

}
