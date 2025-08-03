package commands

import commandrelated.AbstractCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BanCommand : AbstractCommand() {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(BanCommand::class.java)
    }

    override val commandData =  CommandData(name(), description())

    init {
        commandData.addOption(OptionType.USER, "user", "The user to ban", true)
        commandData.addOption(OptionType.STRING, "reason", "The ban reason")
    }

    override fun name(): String {
        return "ban"
    }

    override fun description(): String {
        return "Ban the specified user"
    }

    override fun help(): String {
        return "Usage: /ban <user>"
    }

    override fun execute(event: SlashCommandEvent) {
        if (event.member?.hasPermission(Permission.BAN_MEMBERS) == false) {
            event.reply("Sorry, but you don't have permission to ban members!").setEphemeral(true).queue()
        }

        try {
            val target = event.getOption("user")?.asUser!!

            event.deferReply().queue()
            val reason = event.getOption("reason")?.asString

            val action = event.guild?.ban(target, 0, reason)
            action?.queue {
                event.hook.editOriginal("**${target.asTag} was banned by ${event.user.asTag}!**")
            }
        } catch (e: Exception) {
            event.hook.editOriginal("Some kind of error occurred, are you sure I'm able to ban the user?")
            LOGGER.error("Error while banning user ${event.getOption("user")}")
            e.printStackTrace()
        }

    }
}