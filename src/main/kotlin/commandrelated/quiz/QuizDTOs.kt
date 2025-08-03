package commandrelated.quiz

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Categories(
    @SerialName("trivia_categories")
    val triviaCategories: List<Category>
)

@Serializable
data class Category(
    val id: Int,
    val name: String
)

@Serializable
data class QuestionResponse(
    @SerialName("response_code") val responseCode: Int,
    @SerialName("results") val questions: MutableList<Question>
    ) {

    @Serializable
    data class Question(
        val category: String,
        val type: String,
        val difficulty: String,
        val question: String,
        @SerialName("correct_answer")
        val correctAnswer: String,
        @SerialName("incorrect_answers")
        val incorrectAnswers: List<String>
    ) {
    }
}