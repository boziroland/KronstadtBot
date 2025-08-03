package commandrelated.quiz

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import utils.getJsonFromAPI
import utils.getJsonPropertyValue
import utils.getJsonPropertyValueFromApi

internal class QuizController {
    companion object {
        private const val API_URL = "https://opentdb.com"
    }

    private var questionToken = getQuestionToken()

    private fun getQuestionToken(): String {
        return getJsonPropertyValueFromApi("$API_URL/api_token.php?command=request", "token")
    }

    fun getQuestions(questionCategory: String): MutableList<QuestionResponse.Question> {
        var questionUrl =
            "$API_URL/api.php?amount=${QuizService.QUESTION_COUNT_IN_ONE_API_REQUEST}&category=${
                getAPIQuestionCategory(
                    questionCategory
                )
            }&token=$questionToken"

        var receivedContent = getJsonFromAPI(questionUrl)

        val responseCode = getJsonPropertyValue(receivedContent, "response_code")

        if (!isCorrectResponseCode(responseCode)) {
            questionToken = getQuestionToken()
            questionUrl = questionUrl.substring(0, questionUrl.lastIndexOf("=") + 1) + questionToken
            receivedContent = getJsonFromAPI(questionUrl)
        }

        val questionResponse = try {
            Json.decodeFromString(receivedContent)
        } catch (e: Exception) {
            QuestionResponse(-1, mutableListOf())
        }

        return questionResponse.questions
    }

    fun getCategories(): Categories {
        val receivedContent = getJsonFromAPI("$API_URL/api_category.php")

        return Json.decodeFromString(receivedContent)
    }

    private fun getAPIQuestionCategory(category: String): String {
        val intCategory = category.toInt()
        return if (intCategory == 99) "0" else (intCategory + 8).toString()
    }

    private fun isCorrectResponseCode(responseCode: String) = responseCode == "0"
}