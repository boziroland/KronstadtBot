
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import utils.getPropertiesFromResourceFile
import javax.security.auth.login.LoginException

private val LOGGER: Logger = LoggerFactory.getLogger("MainLogger")

fun readToken(): String? {
    return getPropertiesFromResourceFile("config/ConfigurationKeys.properties")
        .getProperty("DiscordToken")
}

fun getIntents(): List<GatewayIntent> {
    return listOf(
        GatewayIntent.GUILD_EMOJIS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_PRESENCES,
        GatewayIntent.GUILD_VOICE_STATES
    )
}

fun main() {

    try {
        val token = readToken()

        token?.let {
            val jda: JDA = JDABuilder
                .create(it, getIntents())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build()
                .awaitReady()

            jda.addEventListener(MyEventListener(jda))
        } ?: LOGGER.error("Could not read token from property DiscordToken properties file!")
    } catch (e: LoginException) {
        LOGGER.error("Error while running JDA instance: ${e.message}")
        e.printStackTrace()
    }
}
