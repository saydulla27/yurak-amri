package uz.pdp.yurakamri.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.enums.Role;
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
            if (update.getMessage().hasContact()) {

                String phone = update.getMessage().getContact().getPhoneNumber();
                Optional<Ketmon> optionalKetmon = userRepository.findByPhoneNumber(phone);
                if (!optionalKetmon.isPresent()) {

                    userMessage = "siz admin ";
                    execute(null, null);

                }


            } else if (text.equals("/start")) {
                userMessage = "Xush kelibsiz!";
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                if (!byChatId.isPresent()) {
                    Ketmon u1 = new Ketmon();
                    if (userChatId == 473156670) {
                        u1.setRole(Role.ROLE_SUPER_ADMIN);
                        u1.setChatId(userChatId);
                        u1.setState(State.SUPERSTART);
                        u1.setFullName("Muminov Saydulla");
                        userMessage = "super admin";
                        supermenyu();

                    } else {
                        u1.setChatId(userChatId);
                        u1.setState(State.START);
                        menu();
                    }
                    userRepository.save(u1);
                }

            } else {
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                user = byChatId.get();
                String state = user.getState();
                String role = String.valueOf(user.getRole());
                switch (state) {
                    case State.START:
                        switch (text) {
                            case "Ariza yuborish":
                                userMessage = "Arizani to'ldirish";
                                execute(userServiceBot.getRegionList(), null);
                                user.setState(State.ARIZA);
                                break;
                            case "Admin":
                                userMessage = "raqam yuboring";
                                execute(userServiceBot.addcontact(), null);
                                user.setState(State.REG_ADMIN);
                                break;
                        }
                        break;

                    case State.ARIZA:
                        switch (text) {
                            case "Ariza yuborish":
                                userMessage = "Arizani to'ldirish";
                                execute(userServiceBot.getRegionList(), null);
                                user.setState(State.ARIZA);
                        }
                        break;
                    case State.ADMIN:


                        break;
                    case State.SUPERSTART:
                        switch (text) {
                            case "admin qoshish":
                                Ketmon admin = new Ketmon();
                                admin.setPhoneNumber(text);
                                admin.setState(State.REG_ADMIN_phone);
                                userRepository.save(admin);
                                break;
                        } case "malumot olish":
                            execute(null,null);
                            break;


                }
                userRepository.save(user);
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

    public ReplyKeyboardMarkup supermenyu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow.add("admin qoshish");
        keyboardRow1.add("adminlar royhati");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        execute(replyKeyboardMarkup, null);
        return replyKeyboardMarkup;

    }
}
