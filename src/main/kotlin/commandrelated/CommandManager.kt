package commandrelated

import commands.BanCommand
import commands.HangManCommand
import commands.HelpCommand
import commands.QuizCommand
import commands.ShutdownCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

class CommandManager(jda: JDA) {

    private val commands: MutableMap<String, AbstractCommand> = mutableMapOf()

    init {
        add(HangManCommand())
        add(QuizCommand())
        add(ShutdownCommand())
        add(BanCommand())


        add(HelpCommand(commands))
        jda.guilds.forEach { guild ->
            guild.updateCommands().addCommands(commands.values.map { it.commandData }).queue()
        }
    }

    fun handleCommand(event: SlashCommandEvent) {
        commands[event.name]?.execute(event)
    }

    private fun add(command: AbstractCommand) {
        if (!commands.containsKey(command.name())) {
            commands[command.name()] = command
        }
    }
}