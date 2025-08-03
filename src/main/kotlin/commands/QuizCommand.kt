package commands

import commandrelated.AbstractCommand
import commandrelated.quiz.QuestionData
import commandrelated.quiz.QuizRepository
import commandrelated.quiz.QuizService
import commandrelated.quiz.QuizStatistics
import commandrelated.quiz.QuizStatistics.prop
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import utils.createBasicReactionEmbed
import java.awt.Color

class QuizCommand : AbstractCommand() {

    private val service = QuizService()
    private var requestedCategory = "99"
    private lateinit var event: SlashCommandEvent
    private lateinit var lastQuestion: QuestionData

    override val commandData = CommandData(name(), description())

    init {
        commandData
            .addSubcommands(
                SubcommandData("answer", "Answer the question")
                    .addOption(OptionType.INTEGER, "number", "The number of the answer")
            )
        commandData
            .addSubcommands(
                SubcommandData("category", "Change question category")
                    .addOption(OptionType.INTEGER, "category", "The category to change to")
            )
        commandData
            .addSubcommands(
                SubcommandData("statistics", "Show your statistics for a question category")
                    .addOption(OptionType.INTEGER, "category", "The category to show statistics for")
            )
        commandData
            .addSubcommands(SubcommandData("categories", "List all question categories"))
        commandData
            .addSubcommands(SubcommandData("start", "Start the quiz!"))
    }

    override fun name(): String {
        return "quiz"
    }

    override fun description(): String {
        return "Trivia quiz game, start with /quiz or see /help quiz for instructions!"
    }

    override fun help(): String {
        return """
            Basic command and requesting a question: `/quiz`
            Answering the question: `/quiz <result number (1-4)>`
            Listing the categories: `/quiz categories`
            For questions exclusively from a certain category: `/quiz category <category number (1-24, 99)>`. For all categories: `99`
            Statistics for certain categories: `/quiz statistics <category (optional)>` If no category is given, statistics for the category of the last queried question will be shown
            """.trimIndent()
    }

    override fun execute(event: SlashCommandEvent) {
        this.event = event
        val options = event.options

        if (QuizStatistics.userStatistics == null) {
            QuizStatistics.initializeStatisticsForGuild(event.guild!!, service.categories)
        }

        when (event.subcommandName) {
            "answer" -> {
                if (options.size == 1 && QuizService.QUESTION_ACTIVE) {
                    val isCorrect = service.checkGuess(options[0].asString[0])
                    val currentStreak =
                        QuizStatistics.modifyStatistics(
                            requestedCategory,
                            lastQuestion.type,
                            lastQuestion.difficulty,
                            event.user.id,
                            isCorrect
                        )
                    if (isCorrect) {
                        val rightMessage = buildString {
                            this.append("You are right! :) ")
                            this.append(
                                if (currentStreak < 5)
                                    "Your current streak is now $currentStreak, well done!"
                                else
                                    "Holy sh#t, you are actually insane! Your current streak is $currentStreak, well done!"
                            )
                        }
                        lastQuestion = service.sendQuestion(requestedCategory)
                        event.reply(rightMessage).addEmbeds(createQuestionEmbed()).setEphemeral(false).queue()
                    } else {
                        QuizRepository.saveStatistics(QuizStatistics.userStatistics!!)
                        event.reply("Incorrect! The correct answer was ${lastQuestion.correctAnswer} :(")
                            .setEphemeral(false)
                            .queue()
                    }
                } else {
                    event.reply("There's no active question currently!")
                        .setEphemeral(true)
                        .queue()
                }
            }
            "category" -> {
                if (options.isNotEmpty())
                    event.reply(changeCategory(options[0].asString)).queue()
                else
                    event.reply("You need to add the category as well!").setEphemeral(true).queue()
            }
            "statistics" -> {
                if (options.isNotEmpty())
                    event.reply(listStats(event.user.id, options[0].asString)).queue()
                else
                    event.reply("You need to add the category as well!").setEphemeral(true).queue()
            }
            "categories" -> {
                event.reply(listCategories()).queue()
            }
            "start" -> {
                if (!QuizService.QUESTION_ACTIVE) {
                    lastQuestion = service.sendQuestion(requestedCategory)
                    event.replyEmbeds(createQuestionEmbed()).queue()
                }
            }
        }
    }

    private fun createQuestionEmbed(): MessageEmbed {
        return createBasicReactionEmbed(
            lastQuestion.question,
            lastQuestion.answers,
            "Category: *${service.getCategoryNameById(requestedCategory)}*",
            if (lastQuestion.difficulty == "easy") Color.GREEN else if (lastQuestion.difficulty == "medium") Color.YELLOW else Color.RED
        )
    }

    private fun changeCategory(category: String): String {
        if (isValidCategory(category)) {
            requestedCategory = category
            return "Category has been set to category `${category} : ${service.getCategoryNameById(category)}`"
        }
        return "The given category is invalid. See possible categories with `/quiz categories`"
    }

    private fun isValidCategory(category: String): Boolean {
        val nr = category.toInt()
        return nr in 1..24 || nr == 99
    }

    private fun listStats(userId: String, category: String = "99"): String {
        if (!isValidCategory(category)) {
            return "No such category!"
        }
        val stats = QuizStatistics.userStatistics?.get(userId)?.get(category)

        val msg = buildString {
            this.append("```").append("Your statistics for category ${category}: ${service.getCategoryNameById(category)}\n\n")
            stats?.forEach { this.append("${prop(it.key)}: ${it.value}\n") }
            this.append("```")
        }

        return msg
    }

    private fun listCategories(): String {
        return buildString {
            this.append(
                "```The current category is: $requestedCategory : ${service.getCategoryNameById(requestedCategory)}\n\n"
            )
            this.append(
                service.categories
                    .map { "${it.key} : ${it.value}" }
                    .joinToString(System.lineSeparator())
            )
            this.append("```")
        }
    }

}