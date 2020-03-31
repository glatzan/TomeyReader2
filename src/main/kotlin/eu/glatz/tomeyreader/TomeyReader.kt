package eu.glatz.tomeyreader

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import picocli.CommandLine

@SpringBootApplication
class TomeyReader @Autowired constructor(private var settings: CmdSettings, private var factory: CommandLine.IFactory) : CommandLineRunner, ExitCodeGenerator {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private var exitCode = 0

    override fun run(vararg args: String?) {
        exitCode = CommandLine(settings, factory).execute(*args)
    }

    override fun getExitCode(): Int {
        return exitCode
    }
}

fun main(args: Array<String>) {
    System.exit(SpringApplication.exit(SpringApplication.run(TomeyReader::class.java, *args)))
}
