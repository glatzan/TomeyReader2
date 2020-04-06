package eu.glatz.tomeyreader

import ij.IJ
import ij.io.Opener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


class PostProcessor(private val settings: Settings, private val fileSettings: FileTagSettings) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    lateinit var postSettings : PostProcessorSettings

    fun run(extractedImageFiles: Array<File>, sourceFile: File): Boolean {
        for (imgFile in extractedImageFiles) {
            logger.info("Running post for ${imgFile.absolutePath} with macro: ${settings.getAbsolutePostProcessMacro}")
            IJ.runMacroFile(settings.getAbsolutePostProcessMacro.absolutePath, "${imgFile.absolutePath} ${getTargetFile(imgFile, sourceFile)} png")
        }
        return true
    }

    private fun getTargetFile(extractedImageFile: File, sourceFile: File): File {
        if (settings.postProcessTargetDir.isEmpty()) {
            var targetFolder = settings.getAbsoluteTargetFolder

            if (settings.createNewPostDirForFile)
                targetFolder = File(targetFolder, "${sourceFile.name.substringBeforeLast(".")}/post")


            if (!targetFolder.exists())
                targetFolder.mkdirs()

            return File(targetFolder, extractedImageFile.name)
        } else {
            var pDir = settings.getAbsolutePostProcessDir

            if (settings.createNewPostDirForFile)
                pDir = File(pDir, sourceFile.name.substringBeforeLast("."))

            if (!pDir.exists())
                if (!pDir.mkdirs())
                    throw  IllegalStateException("Could not create postprocess output directory ${pDir.absolutePath}")

            return File(pDir, extractedImageFile.name)
        }
    }

    private fun getPostProcessorSettings(): PostProcessorSettings {
        val result = PostProcessorSettings()
        val macroFile = File(settings.postProcessMacro)

        if (!macroFile.isAbsolute)
            result.macroFile = File(settings.runDirectory, settings.postProcessMacro)
        else
            result.macroFile = macroFile

        check(result.macroFile.isFile) { "Macro File not found! (${result.macroFile.path})" }

        val pluginFolder = File(settings.postProcessPluginDir)

        if (!pluginFolder.isAbsolute)
            result.pluginDir = File(settings.runDirectory, settings.postProcessPluginDir)
        else
            result.pluginDir = pluginFolder

        check(!result.pluginDir.isFile) { "Plugin dir not set or does not exist! (${result.pluginDir.path})" }

        return result
    }


    class PostProcessorSettings {
        lateinit var macroFile: File
        lateinit var pluginDir: File
    }
}
