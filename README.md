# DiscordSync
**Configuration:**

~~~
token: "" //The bot's API token
mc-room-name: "" //The channel where all the magic happens
custom-status: "{watching} Doing chores for {players} players!" //Can use {listening} {watching} or {playing} here
maintenance-status: "{playing} Maintenance Simulator 2019" //Status the bot will show when Maintenance mode from WolfplzzFixes is enabled
enable_join_leave_messages: true //Do we show when players join?
enable_death_messages: true //Do we show when players die?
disable_mention_all: true //Do we disable @all @here @everyone?
global_message_username: "Server" //What username will the webhook have?
minecraft_message_format: '<{user}> {message}' //When users in Discord talk, they appear like this in Minecraft
server-start-message: "Server started." //What message to state when the server finishes loading
helpop-room: "" //Room where /helpop messages will be relayed
relay-prefix: "[&9Discord&f]" //Prefix that will be clickable in Minecraft to join the Discord server
enable_kickban_messages: true //Do we show kick/ban messages and reasons?
avatar-player-joined: "" //Do you want custom avatars for webhooks? Add the URLs here
avatar-player-left: ""
avatar-player-died: ""
avatar-player-kickbanned: ""
mod-role: "Moderator" //These users can use !casinostats <playername> in Discord channel
~~~