package eu.glatz.tomeyreader

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import picocli.CommandLine
import kotlin.system.exitProcess

@SpringBootApplication
class TomeyReader @Autowired constructor(
        private val fileTagSettings: FileTagSettings) : CommandLineRunner, ExitCodeGenerator {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private var settings: CmdSettings = CmdSettings()

    private var exitCode = 0

    override fun run(vararg args: String?) {
        CommandLine(settings).parseArgs(*args)

        println("hallo")
        println(settings.dataFolder)
    }

    override fun getExitCode(): Int {
        return exitCode
    }
}

fun main(args: Array<String>) {
    exitProcess(SpringApplication.exit(SpringApplication.run(TomeyReader::class.java, *args)))
}
