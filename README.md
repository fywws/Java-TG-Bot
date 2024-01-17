# Java-TG-Bot


Neccesary dependency
```
<dependency>
      <groupId>org.telegram</groupId>
      <artifactId>telegrambots</artifactId>
      <version>6.9.0</version>
</dependency>
```

Its very simple tg-bot created on java

It has functions to send diffrent type of messages, collect messages, states.

This bot has functions :
```
sendHTMLMessage(long chat_id; String text); // Simple formated message
sendMessage(long chat_id; String text); // Just message
sendMessageWithButtonInKeyboard(long chat_id; String text); // Message with button in keyboard
sendMessageWithInlineKeyboard(long chat_id; String text); // Message with button under the itself
```
And some other stuff.

> [!IMPORTANT]
> **YOU NEED TO CHANGE YOUR BOT TOKEN IN getBotToken() IN Bot.java, to get token you should use https://t.me/BotFather**







> This is code kinda sucks, so you better to find any other repo or write some changes by urself
