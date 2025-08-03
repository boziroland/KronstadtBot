package commandrelated.quiz

import net.dv8tion.jda.api.entities.Guild
import utils.getPropertiesFromResourceFile
import java.io.File
import java.util.*

typealias StatScore = MutableMap<String, Int>
typealias CategoryScore = MutableMap<String, StatScore>
typealias UserStatistics = MutableMap<String, CategoryScore>

object QuizStatistics {

    private val statisticsStringsProperties: Properties =
        getPropertiesFromResourceFile("QuizStatisticsStrings.properties")

    var userStatistics: UserStatistics? = null

    fun modifyStatistics(
        questionCategory: String,
        questionType: String,
        questionDifficulty: String,
        userId: String,
        isCorrect: Boolean = true
    ): Int {

        val setPoints = { statistic: String, score: Int ->
            userStatistics?.get(userId)?.get(questionCategory)?.set(statistic, score)
        }

        val getPoints = { statistic: String ->
            userStatistics?.get(userId)?.get(questionCategory)?.get(statistic) ?: 0
        }

        var point: Int = getPoints("total")
        setPoints("total", ++point)

        when (questionType) {
            "multiple" -> {
                point = getPoints("totalNonTrueFalse")
                setPoints("totalNonTrueFalse", ++point)
            }
            "boolean" -> {
                point = getPoints("totalTrueFalse")
                setPoints("totalTrueFalse", ++point)
            }
        }

        point = getPoints("current")
        if (isCorrect) {
            setPoints("current", ++point)
            point = getPoints("totalCorrect")
            setPoints("totalCorrect", ++point)
            when (questionDifficulty) {
                "easy" -> {
                    point = getPoints("correctEasy")
                    setPoints("correctEasy", ++point)
                }
                "medium" -> {
                    point = getPoints("correctMedium")
                    setPoints("correctMedium", ++point)
                }
                "hard" -> {
                    point = getPoints("correctHard")
                    setPoints("correctHard", ++point)
                }
            }
            when (questionType) {
                "multiple" -> {
                    point = getPoints("correctNonTrueFalse")
                    setPoints("correctNonTrueFalse", ++point)
                }
                "boolean" -> {
                    point = getPoints("correctTrueFalse")
                    setPoints("correctTrueFalse", ++point)
                }
            }
        } else {
            if (point > getPoints("longest")) {
                setPoints("longest", point)
            }
            setPoints("current", 0)
        }

        // return current streak
        return getPoints("current")
    }

    fun initializeStatisticsForGuild(guild: Guild, triviaCategories: Map<String, String>) {
        val file = File(QuizRepository.DATA_FILE)
        if (!file.exists()) {
            userStatistics = mutableMapOf()
            for (u in guild.members) {
                if (u.user.isBot)
                    continue

                val categoryStatisticMap: CategoryScore = mutableMapOf()
                for (c in triviaCategories) {
                    val statScoreMap: StatScore = mutableMapOf()
                    statScoreMap["longest"] = 0
                    statScoreMap["current"] = 0
                    statScoreMap["totalCorrect"] = 0
                    statScoreMap["total"] = 0
                    statScoreMap["correctEasy"] = 0
                    statScoreMap["correctMedium"] = 0
                    statScoreMap["correctHard"] = 0
                    statScoreMap["totalTrueFalse"] = 0
                    statScoreMap["totalNonTrueFalse"] = 0
                    statScoreMap["correctTrueFalse"] = 0
                    statScoreMap["correctNonTrueFalse"] = 0
                    categoryStatisticMap[c.key] = statScoreMap
                }
                val userId = u.id
                userStatistics?.set(userId, categoryStatisticMap)
            }
        } else {
            userStatistics = QuizRepository.readStatistics()
        }
    }

    fun prop(statStr: String): String {
        return statisticsStringsProperties.getProperty(statStr) ?: ""
    }
}