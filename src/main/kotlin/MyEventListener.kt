import commandrelated.CommandManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MyEventListener(jda: JDA) : ListenerAdapter() {

    private val commandManager = CommandManager(jda)

    override fun onSlashCommand(event: SlashCommandEvent) {
        commandManager.handleCommand(event)
    }
}