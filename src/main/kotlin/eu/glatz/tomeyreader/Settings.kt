package eu.glatz.tomeyreader

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.system.ApplicationHome
import picocli.CommandLine
import java.io.File


@CommandLine.Command(name = "settings", mixinStandardHelpOptions = true, showAtFileInUsageHelp = true)
class Settings {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @CommandLine.Option(names = ["-sourceFolder", "-s"], description = ["Path containing .exam files"])
    var dataFolder: String = ""

    @CommandLine.Option(names = ["-fileExtension"], description = ["Extension of .exam files; Default: .exam"])
    var fileExtensions: String = ".exam"

    @CommandLine.Option(names = ["-targetFolder", "-t"], description = ["Target folder for extracted images"])
    var targetFolder: String = "export"

    @CommandLine.Option(names = ["-d"], description = ["Set to true if a new subdirectory within the target folder should be created for every exam file; Default: true"])
    var createNewDirForFile: Boolean = true

    @CommandLine.Option(names = ["-x"], description = ["Width of extracted images within the exam file; Default: Autodetect"])
    var xResolution = -1

    @CommandLine.Option(names = ["-y"], description = ["Height of extracted images within the exam file; Default: Autodetect"])
    var yResolution = -1

    @CommandLine.Option(names = ["-imageCount"], description = ["Total number of images within the exam file; Default: Autodetect"])
    var imageCount = -1

    @CommandLine.Option(names = ["-z"], description = ["Pixel depth of an image; Default: Autodetect"])
    var bytesPerPixel = -1

    @CommandLine.Option(names = ["-offset"], description = ["Starting position of the first image within the exam file; Default: Autodetect"])
    var startOffset = -1

    @CommandLine.Option(names = ["-macro"], description = ["Imagej macro for postprocessing of extracted images"])
    var postProcessMacro: String = ""

    @CommandLine.Option(names = ["-postPlugins"], description = ["Plugins for imagej postprocessing"])
    var postProcessPluginDir: String = ""

    @CommandLine.Option(names = ["-postDir"], description = ["Target folder for post processed images"])
    var postProcessTargetDir: String = ""

    @CommandLine.Option(names = ["-postCreatDir"], description = ["Set to true if a new subdirectory within the post processing folder should be created for every exam file; Default: true"])
    var createNewPostDirForFile: Boolean = true

    @CommandLine.Option(names = ["-additionData"], description = ["Saves extracted patient and image information to separate json file; Default: true"])
    var saveInfoFile: Boolean = true

    @CommandLine.Option(names = ["-onlyAdditionData"], description = ["Saves only additional extracted patient and image information, does not export images; Default: false"])
    var onlyAdditionalData: Boolean = false


    val freeMemory: Long
        get() = Runtime.getRuntime().freeMemory()

    var maxMemory: Long = 0
        get() = Runtime.getRuntime().maxMemory()

    val runDirectory: String
        get() {
            val home = ApplicationHome(Settings::class.java)
            return home.dir.absolutePath
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

    companion object {
        fun toMByte(bytes: Long): Long {
            return bytes / (1024 * 1024)
        }
    }

}
