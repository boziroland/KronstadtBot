package commandrelated

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

abstract class AbstractCommand {

    abstract val commandData: CommandData

    open fun name(): String {
        return commandData.name
    }

    open fun description(): String {
        return commandData.description
    }

    abstract fun help(): String

    abstract fun execute(event: SlashCommandEvent)

}