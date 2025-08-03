package commandrelated.quiz

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import utils.swap

class QuizService {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(QuizService::class.java)
        const val QUESTION_COUNT_IN_ONE_API_REQUEST = 10
        val QUESTION_CATEGORY = "99"
        var QUESTION_ACTIVE = false
    }

    private val controller = QuizController()
    private val questionBuffer: MutableList<QuestionResponse.Question> = mutableListOf()
    private var correctAnswer = '-'
    val categories: Map<String, String> = listCategories()

    fun sendQuestion(category: String = "99"): QuestionData {
        if (questionBuffer.isEmpty() || category == QUESTION_CATEGORY) {
            questionBuffer.addAll(controller.getQuestions(category))
        }
        val question = questionBuffer.removeLast()
        val answers = question.incorrectAnswers as MutableList<String>
        answers.add(question.correctAnswer)
        LOGGER.info("The correct answer is ${question.correctAnswer}")
        if (answers.size > 2) {
            answers.shuffle()
        } else {
            if (answers[0] == "False") {
                answers.swap(0, 1)
            }
        }
        correctAnswer = Character.forDigit(answers.indexOf(question.correctAnswer) + 1, 10)

        QUESTION_ACTIVE = true
        return QuestionData(
            question.question,
            answers,
            question.correctAnswer,
            question.difficulty,
            question.category,
            question.type
        )
    }

    fun checkGuess(guess: Char): Boolean {
        QUESTION_ACTIVE = false
        return guess == correctAnswer
    }

    private fun listCategories(): Map<String, String> {
        val categories = controller.getCategories().triviaCategories
            .associate { (it.id - 8).toString() to it.name }.toMutableMap()
        categories["99"] = "All"

        return categories
    }

    fun getCategoryNameById(id: String): String = if (id == "99") "All" else categories[id] ?: ""
}