package commandrelated.quiz

data class QuestionData(
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
    val difficulty: String,
    val category: String,
    val type: String,
)