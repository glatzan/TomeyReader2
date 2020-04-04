package eu.glatz.tomeyreader

import ij.IJ
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File


class PostProcessor(private val settings: Settings, private val fileSettings: FileTagSettings) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    fun run(imgs: Array<File>) {

        val postSettings = getPostProcessorSettings()

        IJ.runMacro(settings.postProcessMacro)
    }


    fun getPostProcessorSettings(): PostProcessorSettings {
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
