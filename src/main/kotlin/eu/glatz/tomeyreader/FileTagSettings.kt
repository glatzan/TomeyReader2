package eu.glatz.tomeyreader

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "tomey", ignoreInvalidFields = false, ignoreUnknownFields = false)
open class FileTagSettings {

    var width: String = ""
    var height: String = ""
    var fileName: String = ""
    var fileName2: String = ""
    var imageCount: String = ""
    var bitsPerPixel: String = ""
    var xSizeInMM: String = ""
    var ySizeInMM: String = ""
    var zSizePerPixel: String = ""

    /**
     * Offset from fileName to image start
     */
    var imageOffset: Int = 0


    var patient: Patient = Patient()

    var targetImageFormat: String = ""

    class Patient {
        var id: String = ""
        var firstName: String = ""
        var lastName: String = ""
        var birthday: String = ""
        var eye: String = ""
        var commentary: String = ""
    }

    final val nullChar = '\u0000'
    final val dleChar = 16.toChar()
    final val soChar = 14.toChar()
    final val newLineChar = 10.toChar()
    final val returnChar = 13.toChar()
    final val lineBreak = String(charArrayOf(returnChar, newLineChar))
}
