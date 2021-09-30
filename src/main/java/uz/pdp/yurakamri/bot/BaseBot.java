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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);

        Ketmon user = null;
        userChatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String ism = update.getMessage().getFrom().getFirstName();
        String familya = update.getMessage().getFrom().getLastName();
        String delete = null;

        if (update.hasMessage()) {
            if (update.getMessage().hasContact()) {
                String phone = update.getMessage().getContact().getPhoneNumber();
                Optional<Ketmon> optionalKetmon = userRepository.findByPhoneNumber(phone);
                if (optionalKetmon.isPresent()) {
                    optionalKetmon.get().setChatId(userChatId);
                    optionalKetmon.get().setFullName(ism + " " + familya);
                    optionalKetmon.get().setRole(Role.ROlE_ADMIN);
                    optionalKetmon.get().setState(State.START_ADMIN);
                    userRepository.save(optionalKetmon.get());
                    Optional<Ketmon> byChatId = userRepository.findByChatId(1);
                    userRepository.delete(byChatId.get());
                    userMessage = "Assalom aleykum  " + ism + " " + familya;
                    execute(userServiceBot.adduser(), null);

                } else {
                    Optional<Ketmon> optionalKetmon1 = userRepository.findByState(State.REG_ADMIN);
                    optionalKetmon1.get().setState(State.START);
                    userMessage = "siz admin emassiz";
                    optionalKetmon1.get().setChatId(userChatId);
                    userRepository.save(optionalKetmon1.get());
                }
                menu();
                if (update.getMessage().hasLocation()) {
                    Float lat = update.getMessage().getLocation().getLatitude();
                    Float lot = update.getMessage().getLocation().getLongitude();
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
                        u1.setPhoneNumber("+998998476311");
                        u1.setDate(strDate);
                        userMessage = "super admin";
                        supermenyu();

                    } else {
                        u1.setChatId(userChatId);
                        u1.setState(State.START);
                        u1.setRole(Role.ROLE_USER);
                        menu();
                    }
                    userRepository.save(u1);
                }

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
                                user.setState(State.ARIZA);
                                userRepository.save(user);
                                break;
                            case "Admin":
                                userMessage = "raqam yuboring";
                                execute(userServiceBot.addcontact(), null);
                                user.setState(State.REG_ADMIN);
                                user.setChatId(1);
                                userRepository.save(user);
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
                                admin.setState(State.REG_ADMIN_phone);
                                admin.setRole(Role.ROlE_ADMIN);
                                userRepository.save(admin);
                                Optional<Ketmon> byChatId1 = userRepository.findByChatId(userChatId);
                                byChatId1.get().setState(State.S_ADD_ADMIN);
                                userRepository.save(byChatId1.get());
                                userMessage = "raqam yozing";
                                execute(null, null);
                                break;

                            case "adminlar royhati":
                                Optional<Ketmon> byChatId2 = userRepository.findByChatId(userChatId);
                                byChatId2.get().setState(State.SELECT_ADMIN);
                                userMessage = "royxat";
                                userRepository.save(byChatId2.get());
                                execute(userServiceBot.getadmins(), null);
                                break;
                        }
                        break;

                    case State.S_ADD_ADMIN:
                        if (!text.isEmpty()) {
                            Optional<Ketmon> byState = userRepository.findByState(State.REG_ADMIN_phone);
                            byState.get().setPhoneNumber(text);
                            byState.get().setState(State.REG_ADMIN_ok);
                            userRepository.save(byState.get());
                            byChatId.get().setState(State.SUPERSTART);
                            userRepository.save(byChatId.get());
                            userMessage = "ok";

                        }
                        supermenyu();
                        break;

                    case State.SELECT_ADMIN:
                        Optional<Ketmon> byPhoneNumber = userRepository.findByPhoneNumber(text);
                        String phoneNumber = byPhoneNumber.get().getPhoneNumber();
                        if (text.equals(phoneNumber)) {
                            delete = phoneNumber;
                            userMessage = byPhoneNumber.get().getPhoneNumber() + "  " + byPhoneNumber.get().getRole();
                            byChatId.get().setState(State.SELECT_ADMIN_1);
                            userRepository.save(byChatId.get());
                            execute(deletuser(), null);
                        }
                    case State.SELECT_ADMIN_1:
                        switch (text) {
                            case "O`chirish":
                                Optional<Ketmon> byPhoneNumber1 = userRepository.findByPhoneNumber(delete);
                                userRepository.delete(byPhoneNumber1.get());
                                userMessage="tugadi";
                                break;
                            case "orqaga qaytish":
                                userMessage = "omad";
                                user.setState(State.SUPERSTART);
                                userRepository.save(user);
                                supermenyu();
                                break;
                        }
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


    public SendMessage regphone(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId);
        sendMessage.setText("Tel raqam kiriting ");


        return sendMessage;
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

    public ReplyKeyboardMarkup deletuser() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow.add("O`chirish");
        keyboardRow1.add("orqaga qaytish");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }
}
