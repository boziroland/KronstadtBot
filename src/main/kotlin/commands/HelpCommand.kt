package commands

import commandrelated.AbstractCommand
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.util.*

class HelpCommand(private val commands: MutableMap<String, AbstractCommand> = mutableMapOf()) : AbstractCommand() {

    override val commandData = CommandData(name(), description())

    init {
        commandData.addOption(OptionType.STRING, "command", "The command you're curious about")
    }

    override fun name(): String {
        return "help"
    }

    override fun description(): String {
        return "Tells what the command in the 2nd parameter does, Usage: /help <command>"
    }

    override fun help(): String {
        return """
            Tells what the command in the 2nd parameter does
            Usage: `/help <command>`
            """.trimIndent()
    }

    override fun execute(event: SlashCommandEvent) {

        if (event.options.isEmpty()) {
            event.reply(help()).setEphemeral(true).queue()
        } else {
            val command = event.options[0].asString.lowercase(Locale.getDefault())
            event.reply(commands[command]?.help() ?: "No such command exists!").setEphemeral(true).queue()
        }
    }
}