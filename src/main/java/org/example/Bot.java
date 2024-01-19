package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;
import java.util.stream.Collectors;

public class Bot extends TelegramLongPollingBot {

    long ChatId;
    private Map<Long, String> userStates = new HashMap<>();
    private Set<Task> taskSet = new HashSet<>();


    @Override
    public String getBotUsername() {
        return "@BOT NAME";
    }

    @Override
    public String getBotToken() {
        return "BOT TOKEN"; // CHANGE YOUR TOKEN HERE
    }

    @Override
    public void onUpdateReceived(Update update) {
         if (update.hasMessage() && update.getMessage().hasText()) {
             long chat_id = update.getMessage().getChatId();
             String text = update.getMessage().getText();


             if ("All tasks".equals(text)) {
                 try {
                     if (!taskSet.isEmpty()) {
                         String tasksText = taskSet.stream()
                                 .map(task -> task.getTask_id() + "  |  " + task.getText())
                                 .collect(Collectors.joining("\n"));
                         sendHTMLMessage(chat_id, tasksText);
                     } else {
                         sendMessage(chat_id, "⚠\uFE0F You dont have any tasks.");
                     }
                 } catch (TelegramApiException e) {
                     throw new RuntimeException(e);
                 }
             }
             else if(update.getMessage().getText().equals("/start")){
                 sendMessageWithButtonInKeyboard(chat_id, "\uD83D\uDCAC Choose action...");
             }
             else if ("Remove task".equals(text)) {
                 userStates.put(chat_id, "awaitingNumValue");
                 try {
                     sendMessage(chat_id, "\uD83D\uDCAC Write an id of ur task... ");
                 } catch (TelegramApiException e) {
                     throw new RuntimeException(e);
                 }
             }

             else if (userStates.containsKey(chat_id) && "awaitingNumValue".equals(userStates.get(chat_id))) {
                 try {
                     long task_id = Long.valueOf(text);

                     if (taskSet.removeIf(task -> task.getTask_id() == task_id)) {
                         userStates.remove(chat_id);
                         sendMessage(chat_id, "✅ Successfully removed");
                     } else {
                         sendMessage(chat_id, "\uD83D\uDEAB Task id not found.");
                     }

                     sendMessageWithButtonInKeyboard(chat_id, "\uD83D\uDCAC Choose action...");
                 } catch (NumberFormatException e) {
                     try {
                         sendMessage(chat_id, "⚠\uFE0F Invalid input. Please enter a valid number.");
                     } catch (TelegramApiException ex) {
                         throw new RuntimeException(ex);
                     }
                 } catch (TelegramApiException e) {
                     e.printStackTrace();
                 }
             }

             else if(update.getMessage().getText().equals("Add task")){
                 userStates.put(chat_id, "awaitingValue");
                 try {
                     sendMessage(chat_id, "\uD83D\uDCAC Write a name of ur task... ");
                 } catch (TelegramApiException e) {
                     throw new RuntimeException(e);
                 }

             } else if(userStates.containsKey(chat_id) && "awaitingValue".equals(userStates.get(chat_id))){
                 String Value = text;
                 userStates.remove(chat_id);

                 Task t = new Task(text);
                 t.setTask_id(taskSet.size()+1);
                 taskSet.add(t);

                 try {
                     sendMessage(chat_id, "✅ Successfully added");
                     sendMessageWithButtonInKeyboard(chat_id, "\uD83D\uDCAC Choose action...");
                 } catch (TelegramApiException e) {
                     e.printStackTrace();
                 }

             } else {
                 sendMessageWithButtonInKeyboard(chat_id, "\uD83D\uDCAC Choose action...");
             }

         }

    }

    public void sendMessageWithInlineKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        // Создаем inline keyboard
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Создаем кнопки
        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("asd");
        button.setCallbackData("button_pressed");

        row.add(button);
        // Вы можете добавить больше кнопок в этот ряд или создать новые ряды

        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);

        // Устанавливаем inline keyboard в сообщение
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageWithButtonInKeyboard(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("All tasks");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("Add task");
        row2.add("Remove task");

        keyboard.add(row);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendHTMLMessage( long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("```" + "TASKS\n" + text + "```");
        message.setParseMode(ParseMode.MARKDOWNV2);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(long id, String s) throws TelegramApiException {
        SendMessage sender = new SendMessage();
        sender.setChatId(id);
        sender.setText(s);
        execute(sender);
    }


    public void startBot() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            botsApi.registerBot(this);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

class Task {
    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    long task_id;

    public Task(String text) {
        this.text = text;
    }

    String text;
}
