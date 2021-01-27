# Nirubot Discord-Bot

This Bot is just a fun project for my own Discord Server,
don't expect high quality code or unique features.
You can download and edit the bot however you want.

## Installation

Clone the repo and run `bash build.sh` and then go to `/release/`. You will find a `nirubot-{$VERSION}-all.jar`. Copy this, and the `config.json` from `src/main/resources/` into one folder (anywhere on your computer).
Now execute `java -jar nirubot.jar` inside the directory with the jar and the config.
Don't forget to put your bot-token into the config! And __NEVER__ share the bot-token with anyone!

The `host` and `tmpDirPath` token are requiered for the youtubedl command. If you don't have a webserver or just don't want to download files larger than 8mb, you can just leave those two fields as `""`.
The config shoulde look something like this:

```json
{
  "prefix":"!",
  "activity":"!help / !h",
  "activityType":"listening",
  "token":"YOUR BOT TOKEN",
  "owners":[208979474988007425, 208981656999034890],
  "googleApiToken":"YOUR_GOOGLE_API_TOKEN",
  "host": "https://nirusu99.de",
  "tmpDirPath": "/discord/tmp/"
}
```

The bot directory should look like this:
```
bot-directory
    ├── nirubot.jar
    └── config.json
```

The bot will create a guilds directory on the first start:
```
bot-directory
    ├── nirubot.jar
    ├── config.json
    └── guilds
        └── Snowflake{GUILD_ID_LONG}.json
        └── ...
    └── tmp
        └── ... tmp files
```

## Contact

- [Discord](https://discord.gg/FZ546P3)
- [Invite the Bot!](https://discord.com/api/oauth2/authorize?client_id=642884956040724490&permissions=8&scope=bot)
- Discord-Tag: Nirusu#0420
- Email: nils.pukropp@outlook.de
