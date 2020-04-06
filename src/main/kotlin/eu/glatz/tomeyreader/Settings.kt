package eu.glatz.tomeyreader

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.system.ApplicationHome
import picocli.CommandLine
import java.io.File


@CommandLine.Command(name = "settings", mixinStandardHelpOptions = true)
class Settings {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

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

    @CommandLine.Option(names = arrayOf("-postDir"), description = arrayOf("Postprocessing target dir"))
    var postProcessTargetDir: String = ""

    @CommandLine.Option(names = arrayOf("-postCreatDir"), description = arrayOf("Create new subdir for image"))
    var createNewPostDirForFile: Boolean = true

    val runDirectory: String
        get() {
            val home = ApplicationHome(Settings::class.java)
            return home.dir.absolutePath;
        }

    val getAbsoluteDataFolder: File
        get() = getAbsoluteFile(dataFolder)

    val getAbsoluteTargetFolder: File
        get() = getAbsoluteFile(targetFolder)

    val getAbsolutePostProcessMacro: File
        get() = getAbsoluteFile(postProcessMacro)

    val getAbsolutePostProcessDir: File
        get() = getAbsoluteFile(postProcessTargetDir)

    private fun getAbsoluteFile(dir: String): File {
        return if (!File(dir).isAbsolute)
            File(runDirectory, dir)
        else
            File(dir)
    }

    fun validate(): Boolean {

        logger.info("Source folder: ${getAbsoluteDataFolder.path}")
        logger.info("Target folder: ${getAbsoluteTargetFolder.path}")
        logger.info("File extension: ${fileExtensions}")

        if (!getAbsoluteDataFolder.isDirectory) {
            logger.error("Source folder not valid! ${dataFolder}")
            return false
        }

        if (fileExtensions.isEmpty() || !fileExtensions.matches(Regex("\\..*"))) {
            logger.error("File-Extension not valid, must match .xxxx is ${fileExtensions}")
            return false
        }

        if (!getAbsoluteTargetFolder.isDirectory)
            if (!getAbsoluteTargetFolder.mkdirs()) {
                logger.error("Target folder not valid! ${targetFolder}")
                return false
            }


        if (postProcessMacro.isNotEmpty() && !File(postProcessMacro).isFile) {
            logger.error("Makro not found: $postProcessMacro")
            return false
        }

        if (postProcessMacro.isNotEmpty() && postProcessPluginDir.isNotEmpty() && !File(postProcessPluginDir).isDirectory) {
            logger.error("ImageJ Plugin dir ist not valid: $postProcessPluginDir")
            return false
        }
        return true
    }

}
