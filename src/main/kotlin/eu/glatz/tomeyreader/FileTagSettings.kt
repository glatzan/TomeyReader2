package eu.glatz.tomeyreader

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "tomey", ignoreInvalidFields = false, ignoreUnknownFields = false)
class FileTagSettings {

    var width: String = ""
    var height: String = ""
    var fileName: String = ""
    var fileName2: String = ""
    var imageCount: String = ""
    var bitsPerPixel: String = ""
    var xSizeInMM: String = ""
    var ySizeInMM: String = ""
    var zSizePerPixel: String = ""
    var examinationDate: String = ""
    var examinationTime: String = ""

    /**
     * Offset from fileName to image start
     */
    var imageOffset: Int = 0

    /*
     *  Offset of first head from char(33)YYYYMMDD.{GUID-HIGH}-{GUID_LOW}-4.TFS.
     *  From the index of the last s
     */
    var eyeImageFirstHeaderOffset = 0

    /**
     * Header size
     */
    var eyeImageHeaderSize=0

    /**
     * Image height position in header
     */
    var eyeImageInHeaderHeightPosition=0

    /**
     * Image width position in header
     */
    var eyeImageInHeaderWidthPosition=0

    /**
     * Image content offset from byte(03)ccd
     */
    var eyeImageContentOffset = 0

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
    final val escChar = 27.toChar()
}
