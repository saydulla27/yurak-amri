package uz.pdp.yurakamri.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.yurakamri.controller.RegionController;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.Region;
import uz.pdp.yurakamri.entity.enums.Role;
import uz.pdp.yurakamri.repository.RegionRepository;
import uz.pdp.yurakamri.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BaseBot extends TelegramLongPollingBot {

    @Value("${bot.token}")
    String botToken;

    @Value("${bot.username}")
    String username;

    private Long userChatId;
    private String userMessage;

    @Autowired
    UserServiceBot userServiceBot;
    @Autowired
    UserRepository userRepository;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Ketmon user = null;
        userChatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        if (update.hasMessage()) {
            if (text.equals("/start")) {
                userMessage = "Xush kelibsiz!";
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                if (!byChatId.isPresent()) {
                    Ketmon u1 = new Ketmon();
                    u1.setChatId(userChatId);
                    u1.setState(State.START);
                    userRepository.save(u1);
                }
                menu();
            } else {
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                user = byChatId.get();
                String state = user.getState();
                switch (state) {
                    case State.START:
                        switch (text) {
                            case "Ariza yuborish":
                                userMessage = "Arizani to'ldirish";
                                execute(userServiceBot.getRegionList(), null);
                                break;
                            case "Admin":
                                userMessage = "Admin bo'lish parolni kiriting:";
                                if (text.equals("0000")) {
                                    user.setRole(Role.ROlE_ADMIN);
                                    user.setState(State.ADMIN);
                                } else {
                                    menu();
                                }
                                break;
                        }
                        break;
                    case State.ARIZA:
                        break;
                    case State.ADMIN:
                        break;
                }
            }

        }

    }

    private void execute(ReplyKeyboardMarkup replyKeyboardMarkup,
                         InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChatId);
        sendMessage.setText(userMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setOneTimeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
        }
        if (inlineKeyboardMarkup != null)
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public ReplyKeyboardMarkup menu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow.add("Ariza yuborish");
        keyboardRow1.add("Admin");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        execute(replyKeyboardMarkup, null);
        return replyKeyboardMarkup;
    }

}
