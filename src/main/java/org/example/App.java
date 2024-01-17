package org.example;


import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App
{
    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot();
        bot.startBot();
    }
}