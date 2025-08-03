package commands

import commandrelated.AbstractCommand
import commandrelated.hangman.HangManGame
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class HangManCommand(private val game: HangManGame = HangManGame()) : AbstractCommand() {

    override val commandData = CommandData(name(), description())

    init {
        commandData.addOption(OptionType.STRING, "letter", "Letter")
    }

    override fun name(): String {
        return "hangman"
    }

    override fun description(): String {
        return "Hangman game, start with /hangman or see /help ${name()} for instructions!"
    }

    override fun help(): String {
        return """
            Usage:
            Start: `/hangman`
            Afterwards: `/hangman <your letter>`
            """.trimIndent()
    }

    override fun execute(event: SlashCommandEvent) {
        if (!HangManGame.GAME_IN_PROGRESS) {
            event.reply(game.newGame()).queue()
        } else {
            if (event.options.isNotEmpty()) {
                val guess = event.options[0].asString
                val guessedLetter = guess[0].lowercaseChar()
                event.reply(game.guessLetter(guessedLetter)).queue()
            } else {
                event.reply("A game is already going on! Try guessing a letter!").queue()
            }
        }
    }
}