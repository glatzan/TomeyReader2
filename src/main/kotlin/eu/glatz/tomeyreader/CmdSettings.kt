package eu.glatz.tomeyreader

import org.springframework.stereotype.Component
import picocli.CommandLine
import java.util.concurrent.Callable

@Component
@CommandLine.Command(name = "settings", mixinStandardHelpOptions = true)
class CmdSettings : Callable<Integer> {

    @CommandLine.Option(names = arrayOf("-path"), description = arrayOf("Path containing .exame files"))
    var dataFolder: String = ""

    override fun call(): Integer {
        return Integer(23);
    }
}