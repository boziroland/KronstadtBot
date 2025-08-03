[KronstadtBot](https://en.wikipedia.org/wiki/Kotlin_Island#History) is a very simple Discord bot written in Kotlin, using [JDA](https://github.com/DV8FromTheWorld/JDA). The bot
provides various features such as banning users, playing hangman or playing quiz.

## Requirements

- JDK 17
- Gradle

## How to run

### Required files:
`src/main/resources/config/ConfigurationKeys.properties` 2 fields:
- **DiscordToken**: The Discord bot token, from the developer portal *(I have created a token for testing the bot, which I have purposefully left in the file, so this step can be skipped)*
- **OwnerId**: ID of the owner of the bot (needed for the shutdown command, the bot can be run without it)

###Steps for testing

1. Create a [Discord account](https://discord.com/register)
2. Create a server with the account
3. Go to [this link](https://discord.com/oauth2/authorize?client_id=815580002938519583&scope=bot&permissions=470019135)
4. Add the bot to the created server
5. (Re)start the bot
6. The bot's commands can be seen and called after typing a `/` character in the chat box

How to get the `OwnerId` (needed for the shutdown command):
1. In discord, go to `User Settings -> App settings -> Advanced`
2. Enable Developer mode
3. Leave the settings page
4. Right click on your username in the chat or in the user list and select `Copy ID`
5. Paste this ID into the file referenced above
