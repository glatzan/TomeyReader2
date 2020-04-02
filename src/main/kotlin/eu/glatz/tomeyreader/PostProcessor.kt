package eu.glatz.tomeyreader

import ij.IJ
import java.io.File

class PostProcessor(private val settings: Settings, private val fileSettings: FileTagSettings) {


    fun run(baseDir : File){
        val imgs = FileReader.getFiles(File(settings.targetFolder),".${fileSettings.targetImageFormat}")

        for (img in imgs){
            IJ.open(img.absolutePath)
            IJ.runMacro(settings.postProcessMacro)
        }

    }

}