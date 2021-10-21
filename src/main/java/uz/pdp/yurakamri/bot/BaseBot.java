package uz.pdp.yurakamri.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        Ketmon client = null;
        Ketmon user = null;
        userChatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String ism = update.getMessage().getFrom().getFirstName();
        String familya = update.getMessage().getFrom().getLastName();
        String delete = null;

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (text.equals("/start")) {
                    userMessage = "Xush kelibsiz!";
                    Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                    if (!byChatId.isPresent()) {
                        Ketmon u1 = new Ketmon();
                        if (userChatId == 1637495326) {
                            u1.setRole(Role.ROLE_SUPER_ADMIN);
                            u1.setChatId(userChatId);
                            u1.setState(State.SUPERSTART);
                            u1.setFullName("Muminov Saydulla");
                            u1.setPhoneNumber("+998338476311");
                            u1.setDate(strDate);
                            u1.setCompany("Yurak Amri");
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
                    Optional<Ketmon> byBuffer = userRepository.findByBuffer(userChatId);
                    user = byChatId.get();
                    String state = user.getState();

                    switch (state) {
                        case State.START:
                            switch (text) {
                                case "Yordam berish":
                                    userMessage = "raxmat";
                                    execute(null, null);
                                    user.setState(State.ARIZA);
                                    userRepository.save(user);
                                    break;
                                case "Yordam sorash":
                                    userMessage = "Yordam kimga kerak";
                                    execute(userServiceBot.help_1(), null);
                                    user.setState(State.U_HELP);
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
                        case State.SUPERSTART:
                            switch (text) {
                                case "admin qoshish":
                                    Ketmon admin = new Ketmon();
                                    admin.setState(State.REG_ADMIN_phone);
                                    admin.setRole(Role.ROlE_ADMIN);
                                    userRepository.save(admin);
                                    user.setState(State.S_ADD_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "raqam yozing";
                                    execute(null, null);
                                    break;


                                case "admin o`chirish":
                                    userMessage = "o`chadigan adminnni tanlang";
                                    user.setState(State.DELETE_ADMIN_1);
                                    userRepository.save(user);
                                    execute(userServiceBot.delateadmins(), null);
                                    break;

                                case "adminlar royhati":
                                    user.setState(State.SUPERSTART);
                                    userMessage = "royxat";
                                    userRepository.save(user);
                                    execute(userServiceBot.getadmins(), null);
                                    break;

                                case "menyuga qaytish":

                                    supermenyu();
                                    break;
                            }
                            break;

                        case State.S_ADD_ADMIN:
                            if (!text.isEmpty()) {
                                Optional<Ketmon> byState = userRepository.findByState(State.REG_ADMIN_phone);
                                byState.get().setPhoneNumber(text);
                                byState.get().setState(State.REG_ADMIN_phone_1);
                                userRepository.save(byState.get());
                                user.setState(State.S_ADD_ADMIN_NAME);
                                userRepository.save(user);
                                userMessage = "Ismini kiriting";
                                execute(null, null);
                            }
                            break;

                        case State.S_ADD_ADMIN_NAME:
                            if (!text.isEmpty()) {
                                Optional<Ketmon> byState = userRepository.findByState(State.REG_ADMIN_phone_1);
                                byState.get().setFullName(text);
                                byState.get().setState(State.REG_ADMIN_ok);
                                userRepository.save(byState.get());
                                user.setState(State.SUPERSTART);
                                userRepository.save(user);
                                userMessage = "ok";
                            }
                            supermenyu();
                            break;


                        case State.DELETE_ADMIN_1:
                            if (!text.isEmpty()) {
                                if (text.equals("menyuga qaytish")) {
                                    user.setState(State.SUPERSTART);
                                    userRepository.save(user);
                                    userMessage = "menyuga qaytish";
                                    supermenyu();
                                } else
                                    user.setState(State.SUPERSTART);
                                userRepository.save(user);
                                Optional<Ketmon> byPhoneNumber = userRepository.findByPhoneNumber(text);
                                userRepository.delete(byPhoneNumber.get());
                                userMessage = "bu admin tugadi";
                                supermenyu();

                            }
                            break;

                        case State.START_ADMIN:
                            switch (text) {
                                case "Royhatga olish":
                                    user.setState(State.A_REG_PHONE);
                                    userRepository.save(user);
                                    userMessage = "Telefon raqamini kiriting +998XXXXXXXXX ";
                                    execute(null, null);
                                    break;

                                case "Royxatni korish":


                                    break;

                            }
                            break;
                        case State.A_REG_PHONE:
                            if (!text.isEmpty()) {
                                Ketmon client1 = new Ketmon();
                                client1.setPhoneNumber(text);
                                client1.setBuffer(userChatId);
                                userRepository.save(client1);

                                user.setState(State.A_REG_REGION);
                                userRepository.save(user);
                                userMessage = "Shaxar yoki tumannu tanlang";
                                execute(userServiceBot.getRegionList(), null);
                            }
                            break;
                        case State.A_REG_REGION:
                            if (!text.isEmpty()) {
                                byBuffer.get().setRegion(text);
                                userRepository.save(byBuffer.get());
                                user.setState(State.A_REG_NAME);
                                userRepository.save(user);
                                userMessage = "Ism familyasini kiriting :";
                                execute(null, null);
                            }
                            break;

                        case State.A_REG_NAME:
                            if (!text.isEmpty()) {
                                byBuffer.get().setFullName(text);
                                userRepository.save(byBuffer.get());
                                user.setState(State.A_REG_STREET);
                                userRepository.save(user);
                                userMessage = "Yashash manzili";
                                execute(null, null);
                            }
                            break;

                        case State.A_REG_STREET:
                            if (!text.isEmpty()) {
                                byBuffer.get().setStreet_home(text);
                                userRepository.save(byBuffer.get());
                                user.setState(State.A_REG_AGE);
                                userRepository.save(user);
                                userMessage = "Yoshi";
                                execute(null, null);
                            }
                            break;
                        case State.A_REG_AGE:
                            if (!text.isEmpty()) {
                                byBuffer.get().setAge(text);
                                userRepository.save(byBuffer.get());
                                user.setState(State.A_REG_HELP);
                                userRepository.save(user);
                                userMessage = "Voyaga yetmagan bolalar soni ";
                                execute(userServiceBot.addchild(), null);
                            }
                            break;

                        case State.A_REG_HELP:
                            if (!text.isEmpty()) {
                                byBuffer.get().setChildrenCount(Integer.valueOf(text));
                                userRepository.save(byBuffer.get());
                                user.setState(State.A_REG_LOCATION);
                                userRepository.save(user);
                                userMessage = "Locatsiya yuboring ";
                                execute(userServiceBot.addlocation(), null);
                            }
                            break;

                        case State.A_REG_HELPLIST:
                            if (!text.isEmpty()) {
                                byBuffer.get().setHelpTypeList(text);
                                userRepository.save(byBuffer.get());
                                byChatId.get().setState(State.A_REG_FINISH);
                                userRepository.save(byChatId.get());
                                userMessage = "tugadi";
                                execute(userServiceBot.getfinish(), null);

                            }
                            break;

                        case State.A_REG_FINISH:
                            switch (text) {
                                case "Saqlash":
                                    userMessage = "Malumotlar saqlandi";
                                    byBuffer.get().setBuffer(0);
                                    userRepository.save(byBuffer.get());
                                    byChatId.get().setState(State.START_ADMIN);
                                    userRepository.save(byChatId.get());
                                    execute(userServiceBot.adduser(), null);
                                    break;
                                case "qoshimcha malumot kiritish":
                                    userMessage = "Malumotlarini kiriting";
                                    byChatId.get().setState(State.A_REG_FINISHAND);
                                    userRepository.save(byChatId.get());
                                    execute(null, null);
                                    break;

                            }
                            break;
                        case State.A_REG_FINISHAND:
                            if (!text.isEmpty()) {
                                byBuffer.get().setDescription(text);
                                byBuffer.get().setBuffer(0);
                                userRepository.save(byBuffer.get());
                                byChatId.get().setState(State.START_ADMIN);
                                userRepository.save(byChatId.get());
                                userMessage = "Malumotlar saqlandi";
                                execute(userServiceBot.adduser(), null);


                            }
                            break;
                        case State.U_HELP:
                            switch (text) {
                                case "O`zimga":
                                    userMessage = "Shaxar yoki Tumaningiz";
                                    execute(userServiceBot.getRegionList(), null);
                                    user.setState(State.U_HELP_1);
                                    user.setWhom(text);
                                    userRepository.save(user);
                                    break;
                                case "Boshqa insonga":
                                    userMessage = "Shaxar yoki Tumani";
                                    execute(userServiceBot.getRegionList(), null);
                                    user.setState(State.U_HELP_1);
                                    user.setWhom(text);
                                    userRepository.save(user);
                                    break;

                            }
                            break;

                        case State.U_HELP_1:
                            if (!text.isEmpty()) {
                                userMessage = "Ism Familya kiriting";
                                user.setState(State.U_HELP_2);
                                user.setRegion(text);
                                userRepository.save(user);
                                execute(null, null);
                            } break;
                        case State.U_HELP_2:
                            if (!text.isEmpty()) {
                                user.setState(State.U_HELP_3);
                                user.setFullName(text);
                                userRepository.save(user);
                                userMessage = "Yashash manzilingizni kiriting";
                                execute(null, null);
                            }break;
                        case State.U_HELP_3:
                            if (!text.isEmpty()) {
                                user.setState(State.U_HELP_4);
                                user.setStreet_home(text);
                                userRepository.save(user);
                                userMessage = "Yoshingizni kiriting (faqat yoshingiz) ";
                                execute(null, null);
                            }break;
                        case State.U_HELP_4:
                            if (!text.isEmpty()) {
                                userMessage = "Turmush o`rtogingiz haqida malumot";
                                execute(userServiceBot.info_man(), null);
                                user.setState(State.U_HELP_5);
                                user.setAge(text);
                                userRepository.save(user);
                            }break;
                        case State.U_HELP_5:
                            if (!text.isEmpty()) {
                                user.setState(State.U_HELP_6);
                                user.setInfo_man(text);
                                userMessage = "Voyaga yetmagan farzandlar soni";
                                execute(userServiceBot.addchild(), null);
                                userRepository.save(user);
                            }break;

                    }
                }


            }

            if (update.getMessage().hasContact()) {
                String phone = update.getMessage().getContact().getPhoneNumber();
                Optional<Ketmon> optionalKetmon = userRepository.findByPhoneNumber(phone);
                if (optionalKetmon.isPresent()) {
                    optionalKetmon.get().setChatId(userChatId);
                    optionalKetmon.get().setRole(Role.ROlE_ADMIN);
                    optionalKetmon.get().setDate(strDate);
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
                    menu();
                }


            }
            if (update.getMessage().hasLocation()) {
                Float lat = update.getMessage().getLocation().getLatitude();
                Float lot = update.getMessage().getLocation().getLongitude();
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                Optional<Ketmon> byBuffer = userRepository.findByBuffer(userChatId);
                if (byChatId.get().getState().equals(State.A_REG_LOCATION)) {
                    byBuffer.get().setLat(lat);
                    byBuffer.get().setLon(lot);
                    byBuffer.get().setRole(Role.ROLE_USER);
                    userRepository.save(byBuffer.get());
                    byChatId.get().setState(State.A_REG_HELPLIST);
                    userRepository.save(byChatId.get());
                    userMessage = "Yordam turi";
                    execute(userServiceBot.gethelplist(), null);

                }

            }
            if (update.getMessage().hasPhoto()) {
                List<PhotoSize> photo = update.getMessage().getPhoto();

                for (PhotoSize photoSize : photo) {
                    String fileId = photoSize.getFileId();

                    //rasm kelganda bitta guruhga setchatId qilib saqlab turgan holatda bazaga faqatgina fileId yozib qoyasz keyin rasm kerak bo'lganda rasm o'sha id bo'yicha tortib keladi

//                    photoSize.get
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
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow.add("Yordam berish");
        keyboardRow1.add("Yordam sorash");
        keyboardRow2.add("Admin");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        execute(replyKeyboardMarkup, null);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup supermenyu() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow.add("admin qoshish");
        keyboardRow1.add("admin o`chirish");
        keyboardRow2.add("adminlar royhati");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
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

    public InlineKeyboardMarkup abs() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("a");
        inlineKeyboardButton1.setCallbackData("Button \"a\" has been pressed");
        inlineKeyboardButton2.setText("b");
        inlineKeyboardButton2.setCallbackData("Button \"Тык2\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("s").setCallbackData("CallFi4a"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        execute(null, inlineKeyboardMarkup);
        return inlineKeyboardMarkup;
    }
}


