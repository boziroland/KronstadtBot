package commandrelated.quiz

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object QuizRepository {

    // the "database" :)
    const val DATA_FILE = "data/quizStats.json"

    fun saveStatistics(userStatistics: UserStatistics) {
        val json = Json.encodeToString(userStatistics)
        FileWriter(DATA_FILE).use { writer ->
            writer.write(json)
        }
    }

    fun readStatistics(): UserStatistics? {
        val dataFile = File(DATA_FILE)
        if (dataFile.exists()) {
            FileReader(DATA_FILE).use { reader ->
                return Json.decodeFromString(reader.readText())
            }
        }

        return null
    }

}