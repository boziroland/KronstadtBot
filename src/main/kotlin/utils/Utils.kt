package utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import org.apache.commons.text.StringEscapeUtils
import java.awt.Color
import java.io.FileReader
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.random.Random

// based on https://stackoverflow.com/a/2218361
fun getRandomLineFromFile(filePath: String, unbiased: Boolean = false, minLineLength: Int = 3): String {
    val raf = RandomAccessFile(filePath, "r")

    val filePointerPos = (0 until raf.length()).random()
    var word: String
    do {
        word = getLineFromFile(raf, filePointerPos)
    } while (unbiased && Random.nextDouble(0.0, 1.0) > minLineLength / word.length.toDouble())

    return word
}

fun getPropertiesFromResourceFile(fileInResourcesFolder: String): Properties {
    FileReader("src/main/resources/$fileInResourcesFolder").use { reader ->
        val properties = Properties()
        properties.load(reader)
        return properties
    }
}

fun getJsonFromAPI(url: String): String {

    with(URL(url).openConnection() as HttpURLConnection) {
        requestMethod = "GET"

        if (responseCode == 200) {
            return inputStream.bufferedReader().readText()
        }
    }

    return ""
}

fun getJsonPropertyValue(json: String, value: String): String {
    val fields = Json.parseToJsonElement(json)
    return fields.jsonObject[value].toString().trim('"')
}

fun getJsonPropertyValueFromApi(url: String, value: String): String {
    return getJsonPropertyValue(getJsonFromAPI(url), value)
}

fun createBasicReactionEmbed(
    title: String,
    fields: List<String>,
    description: String = "",
    color: Color = Color.BLUE
): MessageEmbed {
    val embed = EmbedBuilder()
    embed.setTitle(StringEscapeUtils.unescapeHtml4(title))
    for (f in fields) {
        embed.addField(
            "${(fields.indexOf(f) + 1)}\u20E3 ${StringEscapeUtils.unescapeHtml4(f)}", "", false
        )
    }
    embed.setDescription(description)
    embed.setColor(color)
    return embed.build()
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun <T> List<T>.subList(from: Int): List<T> {
    return this.subList(1, this.size)
}

private fun getLineFromFile(file: RandomAccessFile, position: Long): String {
    var filePointerPos = position
    file.seek(filePointerPos)
    while (file.read().toChar() != '\n')
        file.seek(--filePointerPos)

    val readWord = file.readLine().toCharArray()
    val bytes = ByteArray(readWord.size)
    for (i in readWord.indices)
        bytes[i] = readWord[i].code.toByte()

    return String(bytes, Charsets.UTF_8)
}