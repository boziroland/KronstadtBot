package commands

import commandrelated.AbstractCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ShutdownCommand : AbstractCommand() {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ShutdownCommand::class.java)
    }

    override val commandData = CommandData(name(), description())

    override fun name(): String {
        return "shutdown"
    }

    override fun description() = "Shuts down the bot, only the bot owner can do it!"

    override fun help(): String {
        return description()
    }

    override fun execute(event: SlashCommandEvent) {
        if (event.user.id == getOwner()) {
            LOGGER.info("Shutting down due to command")
            event.reply("Shutting down...").queue()
            event.jda.shutdown()
        } else {
            event.reply("You cannot shut down the bot!")
        }
    }

    private fun getOwner() = utils.getPropertiesFromResourceFile("config/ConfigurationKeys.properties")
        .getProperty("OwnerId") ?: ""
}