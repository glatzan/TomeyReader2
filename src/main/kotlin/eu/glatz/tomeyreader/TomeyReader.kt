package eu.glatz.tomeyreader

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.Banner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import picocli.CommandLine


/**
 * Run
 * -postPlugins Fiji.app\plugins
 * -macro default3.ijm
 * -postDir post
 */
@SpringBootApplication
@PropertySource("application.yml")
@EnableConfigurationProperties
class TomeyReader @Autowired constructor(
        private val fileTagSettings: FileTagSettings) : CommandLineRunner, ExitCodeGenerator {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private var settings: Settings = Settings()

    private var exitCode = 0

    @Value("\${tomey.fileName}")
    private val stringValue: String? = null

    override fun run(vararg args: String?) {
        logger.info("Starting.. $stringValue")
        logger.info(fileTagSettings.fileName)
        try {
            val c = CommandLine(settings).parseArgs(*args)

            if (c.isUsageHelpRequested) {
                CommandLine(settings).usage(System.out)
                exitCode = 0
                return
            }

            if (!settings.validate()) {
                logger.error("Settings not valid")
                exitCode = 1
                return
            } else {
                logger.info("Settings initialized")
            }

            logger.info("Max memory: ${Settings.toMByte(settings.maxMemory)} MB")
            logger.info("Free memory: ${Settings.toMByte(settings.freeMemory)} MB")

            var postProcessor: PostProcessor? = null

            if (settings.postProcessMacro.isNotEmpty()) {
                logger.info("Creating postprocessor")
                try {
                    postProcessor = PostProcessor(settings, fileTagSettings)
                } catch (e: IllegalStateException) {
                    logger.info("Creating postprocessor failed: ${e.message}")
                    exitCode = 1
                    return
                }
            }

            val reader = FileReader(settings, fileTagSettings, postProcessor)
            reader.run()
        }catch (e : Exception) {
            logger.error("Execution failed: ${e.message}")
            exitCode = 1
            return
        }catch (e: Throwable){
            logger.error("Execution failed: ${e.message}")
            exitCode = 1
            return
        }
    }

    override fun getExitCode(): Int {
        return exitCode
    }

}

fun main(args: Array<String>) {
    //exitProcess(SpringApplication.exit(SpringApplication.run(TomeyReader::class.java, *args)))
    runApplication<TomeyReader>(*args) {
        setHeadless(false)
        setBannerMode(Banner.Mode.OFF)
    }
//    SpringApplication.run(TomeyReader::class.java, *args)
}



