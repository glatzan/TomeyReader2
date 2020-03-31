package eu.glatz.tomeyreader

import picocli.CommandLine
import kotlin.math.ceil
import kotlin.math.log

@CommandLine.Command(name = "settings", mixinStandardHelpOptions = true)
class CmdSettings {

    @CommandLine.Option(names = arrayOf("-a"), description = arrayOf("Automode"))
    var autoMode: Boolean = true

    @CommandLine.Option(names = arrayOf("-sourceFolder"), description = arrayOf("Path containing .exame files"))
    var dataFolder: String = ""

    @CommandLine.Option(names = arrayOf("-fileExtension"), description = arrayOf("Extension of exame files"))
    var fileExtensions: String = ""

    @CommandLine.Option(names = arrayOf("-targetFolder"), description = arrayOf("TargetFolder"))
    var targetFolder: String = ""

    @CommandLine.Option(names = arrayOf("-d"), description = arrayOf("Create new dir for every exame file"))
    var createNewDirForFile: Boolean = true

    @CommandLine.Option(names = arrayOf("-x"), description = arrayOf("x Resolution"))
    var xResolution = 512

    @CommandLine.Option(names = arrayOf("-y"), description = arrayOf("Y Resolution"))
    var yResolution = 900

    @CommandLine.Option(names = arrayOf("-z"), description = arrayOf("Pixel Depth"))
    var zDepth = 1000

    @CommandLine.Option(names = arrayOf("-offset"), description = arrayOf("FileOffset"))
    var startOffset = -1

    @CommandLine.Option(names = arrayOf("-p"), description = arrayOf("Postprocess"))
    var postProcess = true

    @CommandLine.Option(names = arrayOf("-count"), description = arrayOf("Postprocess"))
    var maxImageCount = 255


    val imageSize: Int
        get() = (xResolution * yResolution * bytesPerPixel

    val bytesPerPixel: Int
        get() = (ceil((log(zDepth.toDouble(), 2.toDouble())) / 8)).toInt()
}
