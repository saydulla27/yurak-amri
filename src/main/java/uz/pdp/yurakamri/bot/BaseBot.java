package uz.pdp.yurakamri.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.yurakamri.entity.*;
import uz.pdp.yurakamri.entity.enums.HelpType;
import uz.pdp.yurakamri.entity.enums.Role;
import uz.pdp.yurakamri.repository.*;

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

    @Autowired
    PassportRepository passportRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    ChildListRepository childListRepository;

    @Autowired
    HelpAndUserRepository helpAndUserRepository;

    @Autowired
    RequestUsersRepository requestUsersRepository;


    @Autowired
    RegionRepository regionRepository;

    @Autowired
    HelpAndUserPhotosRepository helpAndUserPhotosRepository;


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
        int year = Integer.parseInt(strDate.substring(strDate.length() - 4));
        Ketmon user = null;

        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String ism = update.getMessage().getFrom().getFirstName();
                String familya = update.getMessage().getFrom().getLastName();
                String delete = null;
                userChatId = update.getMessage().getChatId();
                String text = update.getMessage().getText();
                if (text.equals("/start")) {
                    userMessage = "Xush kelibsiz!";
                    Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                    if (!byChatId.isPresent()) {
                        Ketmon u1 = new Ketmon();
                        if (userChatId == 637495326) {
                            u1.setRole(Role.ROLE_SUPER_ADMIN);
                            u1.setChatId(userChatId);
                            u1.setState(State.SUPERSTART);
                            u1.setFullName("Muminov Saydulla");
                            u1.setPhoneNumber("+998338476311");
                            u1.setCompany("Yurak Amri");
                            userMessage = "super admin";
                            supermenyu();

                        } else {
                            u1.setChatId(userChatId);
                            u1.setState(State.START);
                            u1.setRole(Role.ROLE_USER);
                            menu(u1);
                        }
                        userRepository.save(u1);
                    }

                } else {
                    Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                    Optional<Ketmon> byBuffer = userRepository.findByBuffer(userChatId);
                    Optional<HelpAndUsers> helpAnd = helpAndUserRepository.findByBuffer(userChatId);
                    if (byChatId.isPresent()) {
                        if (text.equals("/restart")) {
                            byChatId.get().setState(State.START);
                            byChatId.get().setBuffer(0);
                            byChatId.get().setChatId(userChatId);
                            userRepository.save(byChatId.get());
                            userMessage = "restart";
                            menu(user);
                        } else
                            user = byChatId.get();
                        String state = user.getState();
                        switch (state) {
                            case State.START:
                                switch (text) {
                                    case "Ehson qilish \uD83C\uDF19":
                                        userMessage = "#YurakAmri bilan hayotingiz davomida ulgurolmagan yaxshi ishlaringiz, ezgulik va sahovatingizga shoshing azizlar. Biz birdam bo’lsak barokat keladi☝\uD83C\uDFFB\n" +
                                                "\n" +
                                                "Payme, Click, Apelsin, Zoomrad, Paynet Orqali Har kuni ehson qilishni odat qiling azizlar.\n" +
                                                "\n" +
                                                "Chet davlatdagilar Telegram orqali shu raqam bilan bog’laning \n" +
                                                "+998 93-565-63-65 \uD83C\uDF19\n" +
                                                "\n" +
                                                "Yetimlarning boshini silayman degan Sahiylar uchun tel:  93-390-83-83    93-389-19-40 \uD83C\uDF19";
                                        user.setState(State.START);
                                        userRepository.save(user);
                                        menu(user);
                                        break;
                                    case "Yordam sörash \uD83E\uDD32":
                                        if (!user.isBlacklist()) {
                                            if (user.isStatus()) {
                                                Optional<RequestUsers> answer = requestUsersRepository.findByUsersAndActive(user, true);
                                                List<RequestUsers> list = requestUsersRepository.findByUsers(user);
                                                if (answer.isPresent()) {
                                                    if (!answer.get().isAnswer()) {
                                                        userMessage = "Sizda " + answer.get().getDate() + " sana №-" + answer.get().getId() + " raqamli murojatingiz mavjud iltimos bizdan javob kuting ";
                                                        user.setState(State.START);
                                                        userRepository.save(user);
                                                        menu(user);
                                                        break;
                                                    }
                                                } else
                                                    userMessage = "Murojatingizni yozib yuboring \n" + user.getFullName() + " shu kungacha " + list.size() + " marta murojat qilgansiz ";
                                                user.setState(State.U_HELP_9);
                                                userRepository.save(user);
                                                execute(userServiceBot.back(), null);
                                                break;
                                            } else
                                                userMessage = "Shaxar yoki Tumaningiz";
                                            user.setState(State.U_HELP_0);
                                            userRepository.save(user);
                                            execute(userServiceBot.getRegionList(), null);
                                            break;
                                        } else
                                            userMessage = user.getFullName() + " sizda yordam sörash imkoniyati yöq";
                                        menu(user);
                                        break;
                                    case "admin":
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
                                    case "admin qöshish":
                                        Ketmon admin = new Ketmon();
                                        admin.setState(State.REG_ADMIN_phone);
                                        admin.setRole(Role.ROlE_ADMIN);
                                        userRepository.save(admin);
                                        user.setState(State.S_ADD_ADMIN);
                                        userRepository.save(user);
                                        userMessage = "raqam yozing";
                                        execute(null, null);
                                        break;

                                    case "admin öchirish":
                                        userMessage = "o`chadigan adminnni tanlang";
                                        user.setState(State.DELETE_ADMIN_1);
                                        userRepository.save(user);
                                        execute(userServiceBot.delateadmins(), null);
                                        break;

                                    case "adminlar röyhati":
                                        user.setState(State.SUPERSTART);
                                        userMessage = "röyxat";
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
                                    if (text.equals("menuga qaytish")) {
                                        user.setState(State.SUPERSTART);
                                        userRepository.save(user);
                                        userMessage = "menuga qaytish";
                                        supermenyu();
                                    } else
                                        user.setState(State.SUPERSTART);
                                    userRepository.save(user);
                                    Optional<Ketmon> byPhoneNumber = userRepository.findByPhoneNumber(text);
                                    userRepository.delete(byPhoneNumber.get());
                                    userMessage = "bu valantyör öchdi";
                                    supermenyu();

                                }
                                break;

                            case State.START_ADMIN:
                                switch (text) {
                                    case "Röyhatga olish":
                                        user.setState(State.A_REG_PHONE);
                                        userRepository.save(user);
                                        userMessage = "Telefon raqamini kiriting +998XXXXXXXXX ";
                                        execute(userServiceBot.back(), null);
                                        break;
                                    case Constant.ArizalarBilanIshlash:
                                        List<RequestUsers> foods = requestUsersRepository.findByFoodAndStatusAndAdmin(true, false, user.getFullName());
                                        if (foods.size() != 0) {
                                            user.setState(State.A_Answer_2);
                                            userRepository.save(user);
                                            send_foodAnswer(user);
                                            break;
                                        } else userMessage = "Avval ariza qabul qiling  \uD83D\uDC81\u200D♂️ ";
                                        execute(userServiceBot.adduser(), null);
                                        break;

                                    case Constant.Arizalar:
                                        user.setState(State.A_Answer);
                                        userRepository.save(user);
                                        List<RequestUsers> byAnswer = requestUsersRepository.findByAnswer(false);
                                        if (byAnswer.size() != 0) {
                                            userMessage = "Barcha arizalar";
                                            execute(userServiceBot.getRegionAnswer(), null);
                                            break;
                                        } else userMessage = "Yangi arizalar mavjud emas";
                                        execute(userServiceBot.getRegionAnswer(), null);
                                        break;
                                    case Constant.Qidirish:
                                        user.setState(State.Find);
                                        userRepository.save(user);
                                        userMessage = "Nima böyicha qidiramiz";
                                        execute(userServiceBot.find_list(), null);
                                        break;
                                    case Constant.YordamOlganlar:
                                        break;
                                    case Constant.Malumotlar:
                                        break;

                                }
                                break;

                            case State.Find:
                                switch (text) {
                                    case Constant.ismFamilya:
                                        userMessage = "Ismi yoki familyasini yozing";
                                        user.setState(State.Find_1);
                                        userRepository.save(user);
                                        execute(userServiceBot.back(), null);
                                        break;
                                    case Constant.Tel_nomer:
                                        break;
                                    case Constant.Child:
                                        break;
                                    case Constant.Ariza_raqami:
                                        break;
                                    case Constant.Back:
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        userMessage = "nima qilamiz ";
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                    default:
                                        send_messega_standart(userChatId, "Tugmalardan foydalaning");
                                        break;
                                }
                                break;

                            case State.Find_1:
                                List<Ketmon> ketmonList = userRepository.findByRoleAndFullNameContainingIgnoreCase(Role.ROLE_MUHLIS, text);
                                if (ketmonList.size() != 0) {
                                    user.setState(State.Find_2);
                                    userRepository.save(user);
                                    send_findName(text);
                                    break;
                                }
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz ";
                                    execute(userServiceBot.adduser(), null);
                                    break;
                                } else send_messega_standart(userChatId, "Bunday odam yöq");
                                break;

                            case State.Find_2:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz ";
                                    execute(userServiceBot.adduser(), null);
                                } else send_messega_standart(userChatId, "Tugmalardan foydalaning");
                                break;

                            case State.Find_3:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.Find);
                                    userRepository.save(user);
                                    userMessage = "Nima böyicha qidiramiz";
                                    execute(userServiceBot.find_list(), null);
                                    break;
                                } else send_messega_standart(userChatId, "Tugmalardan foydalaning");
                                break;

                            case State.A_Answer:
                                if (!text.isEmpty()) {
                                    if (text.equals(Constant.Back)) {
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        userMessage = "nima qilamiz ";
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                    }
                                    int index = text.indexOf(" (");
                                    String shaxar = text.substring(0, index);
                                    List<RequestUsers> requestUsers = requestUsersRepository.findByAnswerAndFoodAndRegion_Name(false, false, shaxar);
                                    if (requestUsers.size() != 0) {
                                        for (RequestUsers requestUser : requestUsers) {
                                            send_infoAnswer(requestUser, year);
                                            userMessage = "➖➖➖➖➖➖➖➖➖➖➖➖";
                                            execute(null, null);
                                        }
                                        user.setState(State.A_Answer_1);
                                        userRepository.save(user);
                                        userMessage = "Özingizga tegishli bölgan arizalarga javob bering ";
                                        execute(userServiceBot.back(), null);
                                        break;
                                    } else userMessage = "Yangi arizalar mavjud emas";
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    execute(userServiceBot.adduser(), null);
                                    break;


                                }
                                break;
                            case State.A_Answer_1:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz ";
                                    execute(userServiceBot.adduser(), null);
                                    break;
                                } else userMessage = "Tugmalardan foydalaning";
                                execute(userServiceBot.back(), null);
                                break;
                            case State.A_Answer_2:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz ";
                                    execute(userServiceBot.adduser(), null);
                                    break;
                                } else userMessage = "Tugmalardan foydalaning";
                                execute(userServiceBot.back(), null);
                                break;
                            case State.A_Answer_3:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz ";
                                    execute(userServiceBot.adduser(), null);
                                    break;
                                } else userMessage = "Tugmalardan foydalaning";
                                execute(userServiceBot.back(), null);
                                break;

                            case State.A_REG_PHONE:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz";
                                    execute(userServiceBot.adduser(), null);
                                    break;
                                }
                                if (text.length() == 13 && text.startsWith("+")) {
                                    Optional<Ketmon> byPhoneNumber = userRepository.findByPhoneNumber(text);
                                    if (byPhoneNumber.isPresent()) {
                                        user.setState(State.A_REG_HELP_3);
                                        userRepository.save(user);
                                        byPhoneNumber.get().setBuffer(userChatId);
                                        userRepository.save(byPhoneNumber.get());
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("\n Ism Familyasi : " + byPhoneNumber.get().getFullName()).
                                                append("\nManzili : " + byPhoneNumber.get().getCity()).
                                                append("\nTugilgan yili : " + byPhoneNumber.get().getAge()).
                                                append("\nFarzandlar soni : " + byPhoneNumber.get().getChildrenInfo()).
                                                append("\n").
                                                append("\n malumotlar bor nima yordam beriladi");
                                        if (byPhoneNumber.get().isBlacklist()) {
                                            stringBuilder.append("\n Qora röyhatda turadi ❌❌❌ ");
                                        }
                                        userMessage = String.valueOf(stringBuilder);
                                        execute(userServiceBot.gethelplist(), null);
                                        break;
                                    }
                                    Ketmon client1 = new Ketmon();
                                    client1.setPhoneNumber(text);
                                    client1.setBuffer(userChatId);
                                    userRepository.save(client1);
                                    user.setState(State.A_REG_REGION_1);
                                    userRepository.save(user);
                                    userMessage = "Viloyatni tanlang";
                                    execute(userServiceBot.getRegionList(), null);
                                    break;
                                } else
                                    user.setState(State.A_REG_PHONE);
                                userRepository.save(user);
                                userMessage = "Raqamni tögri kiriting  (+998... körinishida)";
                                execute(null, null);
                                break;

                            case State.A_REG_REGION_1:
                                if (!text.isEmpty()) {
                                    if (text.equals(Constant.Back)) {
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        userMessage = "nima qilamiz";
                                        Optional<Ketmon> ketmon = userRepository.findByBuffer(userChatId);
                                        userRepository.delete(ketmon.get());
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                    }
                                    Optional<Region> region = regionRepository.findByName(text);
                                    byBuffer.get().setRegion(region.get());
                                    byBuffer.get().setRayon(text);
                                    userRepository.save(byBuffer.get());
                                    user.setState(State.A_REG_REGION);
                                    userRepository.save(user);
                                    userMessage = "Shaxar yoki tumanni tanlang";
                                    execute(userServiceBot.getCityList(text), null);
                                }
                                break;
                            case State.A_REG_REGION:
                                if (!text.isEmpty()) {
                                    if (text.equals(Constant.Back)) {
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        userMessage = "nima qilamiz";
                                        Optional<Ketmon> ketmon1 = userRepository.findByBuffer(userChatId);
                                        userRepository.delete(ketmon1.get());
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                    }
                                    byBuffer.get().setCity(text);
                                    userRepository.save(byBuffer.get());
                                    user.setState(State.A_REG_NAME);
                                    userRepository.save(user);
                                    userMessage = "Ism familyasini kiriting :";
                                    execute(userServiceBot.back(), null);
                                    break;
                                }
                                break;
                            case State.A_REG_NAME:
                                if (!text.isEmpty()) {
                                    if (text.equals(Constant.Back)) {
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        userMessage = "nima qilamiz";
                                        Optional<Ketmon> ketmon2 = userRepository.findByBuffer(userChatId);
                                        userRepository.delete(ketmon2.get());
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                    }
                                    byBuffer.get().setFullName(text);
                                    userRepository.save(byBuffer.get());
                                    user.setState(State.A_REG_STREET);
                                    userRepository.save(user);
                                    userMessage = "Yashash manzili (Maxalla va Xonadon)";
                                    execute(userServiceBot.back(), null);
                                    break;
                                }
                                break;

                            case State.A_REG_STREET:
                                if (!text.isEmpty()) {
                                    if (text.equals(Constant.Back)) {
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        userMessage = "nima qilamiz";
                                        Optional<Ketmon> ketmon3 = userRepository.findByBuffer(userChatId);
                                        userRepository.delete(ketmon3.get());
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                    }
                                    byBuffer.get().setStreet_home(text);
                                    userRepository.save(byBuffer.get());
                                    user.setState(State.A_REG_AGE);
                                    userRepository.save(user);
                                    userMessage = "Tuğilgan yili (1970 körinishida)";
                                    execute(userServiceBot.back(), null);
                                    break;
                                }
                                break;
                            case State.A_REG_AGE:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START_ADMIN);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz";
                                    Optional<Ketmon> ketmon4 = userRepository.findByBuffer(userChatId);
                                    userRepository.delete(ketmon4.get());
                                    execute(userServiceBot.adduser(), null);
                                    break;
                                }
                                if (text.length() == 4) {
                                    byBuffer.get().setAge(text);
                                    userRepository.save(byBuffer.get());
                                    user.setState(State.INFO_MAN);
                                    userRepository.save(user);
                                    userMessage = "Turmush örtoği haqida malumot ";
                                    execute(userServiceBot.info_man(), null);
                                    break;
                                } else userMessage = "Töğri kiriting (1970 körinishida) ";
                                execute(userServiceBot.back(), null);
                                break;

                            case State.INFO_MAN:
                                switch (text) {
                                    case Constant.AJRASHGAN:
                                        byBuffer.get().setInfo_man(text);
                                        userRepository.save(byBuffer.get());
                                        user.setState(State.A_REG_HELP);
                                        userRepository.save(user);
                                        userMessage = "Farzandlari  ";
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.VAFOT_ETGAN:
                                        byBuffer.get().setInfo_man(text);
                                        userRepository.save(byBuffer.get());
                                        user.setState(State.A_REG_HELP);
                                        userRepository.save(user);
                                        userMessage = "Farzandlari  ";
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Tashlab_ketgan:
                                        byBuffer.get().setInfo_man(text);
                                        userRepository.save(byBuffer.get());
                                        user.setState(State.A_REG_HELP);
                                        userRepository.save(user);
                                        userMessage = "Farzandlari  ";
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Guruh_bir:
                                        byBuffer.get().setInfo_man(text);
                                        userRepository.save(byBuffer.get());
                                        user.setState(State.A_REG_HELP);
                                        userRepository.save(user);
                                        userMessage = "Farzandlari  ";
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Guruh_ikki:
                                        byBuffer.get().setInfo_man(text);
                                        userRepository.save(byBuffer.get());
                                        user.setState(State.A_REG_HELP);
                                        userRepository.save(user);
                                        userMessage = "Farzandlari  ";
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Yolgiz_ona:
                                        byBuffer.get().setInfo_man(text);
                                        userRepository.save(byBuffer.get());
                                        user.setState(State.A_REG_HELP);
                                        userRepository.save(user);
                                        userMessage = "Farzandlari  ";
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    default:
                                        send_messega_standart(userChatId, "Tugmalarning biridan foydalaning");
                                        break;
                                }

                                break;
                            case State.A_REG_HELP:
                                ChildList childList = new ChildList();
                                switch (text) {
                                    case Constant.FARZANDIM_YOQ:
                                        user.setState(State.A_REG_HELP_3);
                                        userRepository.save(user);
                                        byBuffer.get().setChildrenInfo(text);
                                        userRepository.save(byBuffer.get());
                                        userMessage = "Qanday yordam körsatildi";
                                        execute(userServiceBot.gethelplist(), null);
                                        break;
                                    case Constant.YAKUNLASH:
                                        user.setState(State.A_REG_HELP_3);
                                        userRepository.save(user);
                                        userMessage = "Qanday yordam körsatildi";
                                        execute(userServiceBot.gethelplist(), null);
                                        break;
                                    case Constant.OGIL:
                                        childList.setSex(text);
                                        childList.setUsers(byBuffer.get());
                                        childList.setBuffer(userChatId);
                                        childListRepository.save(childList);
                                        user.setState(State.A_REG_HELP_1);
                                        userRepository.save(user);
                                        userMessage = "Ismini kiriting";
                                        execute(null, null);
                                        break;
                                    case Constant.QIZ:
                                        childList.setSex(text);
                                        childList.setUsers(byBuffer.get());
                                        childList.setBuffer(userChatId);
                                        childListRepository.save(childList);
                                        user.setState(State.A_REG_HELP_1);
                                        userRepository.save(user);
                                        userMessage = "Ismini kiriting";
                                        execute(null, null);
                                        break;
                                    default:
                                        send_messega_standart(userChatId, "Tugmalarning biridan foydalaning");
                                        break;
                                }
                                break;

                            case State.A_REG_HELP_1:
                                Optional<ChildList> byBuffer2 = childListRepository.findByBuffer(userChatId);
                                if (!text.isEmpty()) {
                                    byBuffer2.get().setName(text);
                                    childListRepository.save(byBuffer2.get());
                                    user.setState(State.A_REG_HELP_2);
                                    userRepository.save(user);
                                    userMessage = "Yoshini kiriting";
                                    execute(userServiceBot.addchildage(), null);
                                }

                                break;
                            case State.A_REG_HELP_2:
                                if (!text.isEmpty()) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    Optional<ChildList> byBuffer1 = childListRepository.findByBuffer(userChatId);
                                    byBuffer1.get().setAge(Integer.parseInt(text));
                                    byBuffer1.get().setBuffer(0);
                                    childListRepository.save(byBuffer1.get());
                                    user.setState(State.A_REG_HELP);
                                    userRepository.save(user);
                                    for (ChildList farzandlari : childListRepository.findAllByUsers(byBuffer.get())) {
                                        stringBuilder.append("* " + farzandlari.getName() + "   " + farzandlari.getAge() + " yosh" + "\n");
                                    }
                                    userMessage = stringBuilder + "\nKeyingi farzandini kiriting boshqa farzandi bölmasa  YAKUNLASH tugmasini bosing";
                                    execute(userServiceBot.addchild(), null);
                                }
                                break;

                            case State.A_REG_HELP_3:
                                if (!text.isEmpty()) {
                                    if (text.equals(Constant.Back)) {
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        byBuffer.get().setBuffer(0);
                                        userRepository.save(byBuffer.get());
                                        userMessage = "nima qilamiz";
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                    }
                                    HelpAndUsers helpAndUsers = new HelpAndUsers();
                                    helpAndUsers.setHelpType(HelpType.valueOf(text));
                                    helpAndUsers.setUsers(byBuffer.get());
                                    helpAndUsers.setAdmin(user.getFullName());
                                    helpAndUsers.setDate(strDate);
                                    helpAndUsers.setBuffer(userChatId);
                                    helpAndUserRepository.save(helpAndUsers);
                                    user.setState(State.A_REG_FINISH);
                                    userRepository.save(user);
                                    userMessage = "Oxirgi qadam";
                                    execute(userServiceBot.getfinish(), null);
                                }
                                break;

                            case State.A_REG_FINISH:
                                switch (text) {
                                    case "Rasm yuklash 📸":
                                        userMessage = "Shu yerga yuboring";
                                        user.setState(State.A_REG_FOTO);
                                        userRepository.save(user);
                                        execute(userServiceBot.back(), null);
                                        break;
                                    case "Hujjatlar yuklash":
                                        List<Passport> passportList = passportRepository.findAllByUsers(byBuffer.get());
                                        if (passportList.size() != 0) {
                                            send_passport(byBuffer.get());
                                            user.setState(State.A_REG_pasport);
                                            userRepository.save(user);
                                            userMessage = "Bizdagi hujjatlari qöshimcha hujjat yuklamoqchi bölsangiz shu yerga yuboring";
                                            execute(userServiceBot.back(), null);
                                            break;
                                        } else
                                            userMessage = "Shu yerga pasport yuklang \uD83C\uDDFA\uD83C\uDDFF";
                                        user.setState(State.A_REG_pasport);
                                        userRepository.save(user);
                                        execute(userServiceBot.back(), null);
                                        break;
                                    case "Saqlash ✅":
                                        List<Passport> passportlar = passportRepository.findAllByUsers(byBuffer.get());
                                        List<HelpAndUsersPhotos> helpPhoto = helpAndUserPhotosRepository.findAllByHelps(helpAnd.get());
                                        if (passportlar.size() != 0 || helpPhoto.size() != 0) {
                                            List<ChildList> allByUsers = childListRepository.findAllByUsers(byBuffer.get());
                                            userMessage = "Malumotlar saqlandi";
                                            user.setState(State.START_ADMIN);
                                            userRepository.save(user);
                                            execute(userServiceBot.adduser(), null);
                                            send_ms_admin(userChatId, year);
                                            byBuffer.get().setChildrenInfo(String.valueOf(allByUsers.size()));
                                            byBuffer.get().setBuffer(0);
                                            byBuffer.get().setRole(Role.ROLE_MUHLIS);
                                            userRepository.save(byBuffer.get());
                                            helpAnd.get().setBuffer(0);
                                            helpAndUserRepository.save(helpAnd.get());
                                            break;
                                        } else userMessage = "Iltimos hujjat yoki rasm yuklang";
                                        execute(userServiceBot.getfinish(), null);

                                        break;
                                    case "Qöshimcha kiritish  ✏️":
                                        userMessage = "Malumotlarini kiriting";
                                        user.setState(State.A_REG_FINISHAND);
                                        userRepository.save(user);
                                        execute(userServiceBot.back(), null);
                                        break;
                                    case "Bekor qilish ❎":
                                        Optional<HelpAndUsers> helpAndUsers = helpAndUserRepository.findByBuffer(userChatId);
                                        List<HelpAndUsersPhotos> allByHelpAndUsers = helpAndUserPhotosRepository.findAllByHelps(helpAndUsers.get());
                                        for (HelpAndUsersPhotos allByHelpAndUser : allByHelpAndUsers) {
                                            helpAndUserPhotosRepository.delete(allByHelpAndUser);
                                        }
                                        helpAndUserRepository.delete(helpAndUsers.get());
                                        byBuffer.get().setBuffer(0);
                                        userRepository.save(byBuffer.get());
                                        userMessage = "\uD83D\uDC4C";
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        execute(userServiceBot.adduser(), null);
                                        break;
                                }
                                break;

                            case State.A_REG_pasport:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.A_REG_FINISH);
                                    userRepository.save(user);
                                    userMessage = "oxirida saqlash tugmasini bosing";
                                    execute(userServiceBot.getfinish(), null);
                                    break;
                                } else userMessage = "Tugmalardan foydalaning";
                                execute(null, null);
                                break;

                            case State.A_REG_FOTO:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.A_REG_FINISH);
                                    userRepository.save(user);
                                    userMessage = "oxirida saqlash tugmasini bosing";
                                    execute(userServiceBot.getfinish(), null);
                                    break;
                                } else userMessage = "Tugmalardan foydalaning";
                                execute(null, null);
                                break;
                            case State.A_REG_FINISHAND:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.A_REG_FINISH);
                                    userRepository.save(user);
                                    userMessage = "oxirida saqlash tugmasini bosing";
                                    execute(userServiceBot.getfinish(), null);
                                    break;
                                }
                                byBuffer.get().setDescription(text);
                                userRepository.save(byBuffer.get());
                                user.setState(State.A_REG_FINISH);
                                userRepository.save(user);
                                userMessage = "Malumotlar saqlandi";
                                execute(userServiceBot.getfinish(), null);
                                break;


                            case State.U_HELP_0:
                                Optional<Region> byName = regionRepository.findByName(text);
                                if (byName.isPresent()) {
                                    Optional<Region> region = regionRepository.findByName(text);
                                    user.setState(State.U_HELP_1);
                                    user.setRegion(region.get());
                                    user.setRayon(region.get().getName());
                                    userRepository.save(user);
                                    userMessage = "Shahringiz";
                                    execute(userServiceBot.getCityList(text), null);
                                    break;
                                }
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz";
                                    menu(user);
                                    break;
                                } else send_messega_standart(userChatId, "Tugmalarning biridan foydalaning");
                                break;

                            case State.U_HELP_1:
                                Optional<City> city = cityRepository.findByName(text);
                                if (city.isPresent()) {
                                    userMessage = "Ism Familya kiriting";
                                    user.setState(State.U_HELP_2);
                                    user.setCity(text);
                                    userRepository.save(user);
                                    execute(null, null);
                                    break;
                                }
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz";
                                    menu(user);
                                    break;
                                } else send_messega_standart(userChatId, "Tugmalarning biridan foydalaning");
                                break;
                            case State.U_HELP_2:
                                if (!text.isEmpty()) {
                                    user.setState(State.U_HELP_3);
                                    user.setFullName(text);
                                    userRepository.save(user);
                                    userMessage = "Yashash manzilingizni kiriting (Maxalla va xonadon)";
                                    execute(null, null);
                                }
                                break;
                            case State.U_HELP_3:
                                if (!text.isEmpty()) {
                                    user.setState(State.U_HELP_4);
                                    user.setStreet_home(text);
                                    userRepository.save(user);
                                    userMessage = "Tuğilgan yilingiz (1980 shu körinishda) ";
                                    execute(null, null);
                                }
                                break;
                            case State.U_HELP_4:
                                if (!text.isEmpty()) {
                                    if (text.length() == 4) {
                                        userMessage = "Turmush örtogingiz haqida malumot";
                                        user.setState(State.U_HELP_5);
                                        user.setAge(text);
                                        userRepository.save(user);
                                        execute(userServiceBot.info_man(), null);
                                        break;
                                    } else userMessage = "Töğri kiriting (1980 shu körinishda) ";
                                    execute(null, null);
                                    break;
                                }
                                break;
                            case State.U_HELP_5:
                                switch (text) {
                                    case Constant.AJRASHGAN:
                                        user.setState(State.U_HELP_6);
                                        user.setInfo_man(text);
                                        userMessage = "Katta farzandingizni kiriting :";
                                        userRepository.save(user);
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.VAFOT_ETGAN:
                                        user.setState(State.U_HELP_6);
                                        user.setInfo_man(text);
                                        userMessage = "Katta farzandingizni kiriting :";
                                        userRepository.save(user);
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Tashlab_ketgan:
                                        user.setState(State.U_HELP_6);
                                        user.setInfo_man(text);
                                        userMessage = "Katta farzandingizni kiriting :";
                                        userRepository.save(user);
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Guruh_bir:
                                        user.setState(State.U_HELP_6);
                                        user.setInfo_man(text);
                                        userMessage = "Katta farzandingizni kiriting :";
                                        userRepository.save(user);
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Guruh_ikki:
                                        user.setState(State.U_HELP_6);
                                        user.setInfo_man(text);
                                        userMessage = "Katta farzandingizni kiriting :";
                                        userRepository.save(user);
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    case Constant.Yolgiz_ona:
                                        user.setState(State.U_HELP_6);
                                        user.setInfo_man(text);
                                        userMessage = "Katta farzandingizni kiriting :";
                                        userRepository.save(user);
                                        execute(userServiceBot.addchild(), null);
                                        break;
                                    default:
                                        send_messega_standart(userChatId, "Tugmalarning biridan foydalaning");
                                        break;
                                }
                                break;

                            case State.U_HELP_6:
                                ChildList childList1 = new ChildList();
                                switch (text) {
                                    case Constant.FARZANDIM_YOQ:
                                        user.setState(State.U_HELP_phone);
                                        user.setChildrenInfo(text);
                                        userRepository.save(user);
                                        userMessage = "Telefon raqamingizni kiriting : (+998... körinishida)";
                                        execute(null, null);
                                        break;
                                    case Constant.YAKUNLASH:
                                        user.setState(State.U_HELP_phone);
                                        userRepository.save(user);
                                        userMessage = "Telefon raqamingizni kiriting : (+998... körinishida)";
                                        execute(null, null);
                                        break;
                                    case Constant.OGIL:
                                        childList1.setSex(text);
                                        childList1.setUsers(user);
                                        childList1.setBuffer(userChatId);
                                        childListRepository.save(childList1);
                                        user.setState(State.U_HELP_7);
                                        userRepository.save(user);
                                        userMessage = "Ismini kiriting";
                                        execute(null, null);
                                        break;
                                    case Constant.QIZ:
                                        childList1.setSex(text);
                                        childList1.setUsers(user);
                                        childList1.setBuffer(userChatId);
                                        childListRepository.save(childList1);
                                        user.setState(State.U_HELP_7);
                                        userRepository.save(user);
                                        userMessage = "Ismini kiriting";
                                        execute(null, null);
                                        break;
                                    default:
                                        send_messega_standart(userChatId, "Tugmalarning biridan foydalaning");
                                        break;
                                }
                                break;

                            case State.U_HELP_7:
                                Optional<ChildList> byBuffer3 = childListRepository.findByBuffer(userChatId);
                                if (!text.isEmpty()) {
                                    byBuffer3.get().setName(text);
                                    childListRepository.save(byBuffer3.get());
                                    user.setState(State.U_HELP_8);
                                    userRepository.save(user);
                                    userMessage = "Yoshini kiriting";
                                    execute(userServiceBot.addchildage(), null);
                                }

                                break;
                            case State.U_HELP_8:
                                Optional<ChildList> byBuffer4 = childListRepository.findByBuffer(userChatId);
                                StringBuilder stringBuilder = new StringBuilder();
                                if (!text.isEmpty()) {
                                    byBuffer4.get().setAge(Integer.parseInt(text));
                                    byBuffer4.get().setBuffer(0);
                                    childListRepository.save(byBuffer4.get());
                                    user.setState(State.U_HELP_6);
                                    userRepository.save(user);
                                    for (ChildList bollari : childListRepository.findAllByUsers(user)) {
                                        stringBuilder.append("* " + bollari.getName() + "   " + bollari.getAge() + " yosh" + "\n");
                                    }
                                    userMessage = stringBuilder + "\nKeyingi farzandingizni kiriting boshqa farzandingiz bölmasa  YAKUNLASH tugmasini bosing";
                                    execute(userServiceBot.addchild(), null);
                                }
                                break;

                            case State.U_HELP_phone:
                                if (text.length() == 13 && text.startsWith("+")) {
                                    user.setPhoneNumber(text);
                                    user.setState(State.U_HELP_9);
                                    userRepository.save(user);
                                    userMessage = "Murojatingiz sababini qisqacha yozing";
                                    execute(null, null);
                                    break;
                                } else
                                    user.setState(State.U_HELP_phone);
                                userRepository.save(user);
                                userMessage = "Raqamingizni tögri kiriting  (+998... körinishida)";
                                execute(null, null);
                                break;
                            case State.U_HELP_9:
                                if (text.equals(Constant.Back)) {
                                    user.setState(State.START);
                                    userRepository.save(user);
                                    userMessage = "nima qilamiz";
                                    menu(user);
                                    break;
                                }
                                RequestUsers requestUsers = new RequestUsers();
                                requestUsers.setDescription(text);
                                requestUsers.setUsers(user);
                                requestUsers.setStatus(false);
                                requestUsers.setAnswer(false);
                                requestUsers.setActive(true);
                                requestUsers.setRegion(user.getRegion());
                                requestUsers.setDate(strDate);
                                requestUsersRepository.save(requestUsers);
                                userMessage = "Murojatingiz yuborildi bizdan javob kuting";
                                user.setState(State.START);
                                user.setStatus(true);
                                user.setRole(Role.ROLE_MUHLIS);
                                userRepository.save(user);
                                menu(user);
                                break;

                            default:
                                send_messega_standart(userChatId, "tugmalardan foydalaning");
                        }
                    } else send_messega_standart(userChatId, "/start");
                }
            }
            if (update.getMessage().hasContact()) {
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                String phone = update.getMessage().getContact().getPhoneNumber();
                if (phone.length() > 12) {
                    phone = phone.substring(1, 13);
                } else phone = update.getMessage().getContact().getPhoneNumber();
                Optional<Ketmon> optionalKetmon = userRepository.findByPhoneNumber(phone);
                user = byChatId.get();
                String state = user.getState();
                switch (state) {
                    case State.START:
                        if (!optionalKetmon.isPresent()) {
                            userMessage = "Sizni tanimadik";
                            menu(user);
                            break;
                        }
                        Role role = optionalKetmon.get().getRole();
                        switch (role) {
                            case ROlE_ADMIN:
                                userRepository.delete(byChatId.get());
                                optionalKetmon.get().setChatId(userChatId);
                                optionalKetmon.get().setRole(Role.ROlE_ADMIN);
                                optionalKetmon.get().setState(State.START_ADMIN);
                                userRepository.save(optionalKetmon.get());
                                userMessage = "Assalom aleykum  ";
                                execute(userServiceBot.adduser(), null);
                                break;
                        }
                        break;
                }


            }
            if (update.getMessage().hasLocation()) {
                userChatId = update.getMessage().getChatId();
                Float lat = update.getMessage().getLocation().getLatitude();
                Float lot = update.getMessage().getLocation().getLongitude();
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                user = byChatId.get();
                String state = user.getState();
                switch (state) {
                    case State.A_REG_FINISH:
                        Optional<Ketmon> ketmon = userRepository.findByBuffer(userChatId);
                        ketmon.get().setLat(lat);
                        ketmon.get().setLon(lot);
                        userRepository.save(ketmon.get());
                        userMessage = "location saqlandi";
                        execute(userServiceBot.getfinish(), null);
                        break;
                    case State.START:
                        user.setLat(lat);
                        user.setLon(lot);
                        userRepository.save(user);
                        userMessage = "Location saqlandi";
                        menu(user);
                        break;
                }


            }
            if (update.getMessage().hasPhoto()) {
                userChatId = update.getMessage().getChatId();
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                user = byChatId.get();
                String state = user.getState();
                switch (state) {
                    case State.A_REG_FOTO:
                        Optional<Ketmon> ketmon = userRepository.findByBuffer(userChatId);
                        Optional<HelpAndUsers> byUsers = helpAndUserRepository.findByBuffer(userChatId);
                        List<PhotoSize> photos = update.getMessage().getPhoto();
                        String f_id = photos.stream()
                                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                                .findFirst()
                                .orElse(null).getFileId();

                        HelpAndUsersPhotos helpAndUsersPhotos = new HelpAndUsersPhotos();
                        helpAndUsersPhotos.setPhotoId(f_id);
                        helpAndUsersPhotos.setHelps(byUsers.get());
                        helpAndUserPhotosRepository.save(helpAndUsersPhotos);

                        user.setState(State.A_REG_FINISH);
                        userRepository.save(user);
                        userMessage = "Saqlandi";
                        execute(userServiceBot.getfinish(), null);
                        break;
                    case State.A_REG_pasport:
                        Optional<Ketmon> ketmon1 = userRepository.findByBuffer(userChatId);
                        List<PhotoSize> photos1 = update.getMessage().getPhoto();
                        String f_id1 = photos1.stream()
                                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                                .findFirst()
                                .orElse(null).getFileId();

                        Passport passport = new Passport();
                        passport.setPhotoId(f_id1);
                        passport.setUsers(ketmon1.get());
                        passportRepository.save(passport);

                        user.setState(State.A_REG_FINISH);
                        userRepository.save(user);
                        userMessage = "Saqlandi";
                        execute(userServiceBot.getfinish(), null);
                        break;
                }
            }
        } else if (update.hasCallbackQuery()) {
            userChatId = update.getCallbackQuery().getMessage().getChatId();
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            Optional<Ketmon> optionalUser = userRepository.findByChatId(userChatId);
            user = optionalUser.get();
            String state = user.getState();
            int index = call_data.indexOf("(");
            Integer id = Integer.valueOf(call_data.substring(0, index));
            String call = call_data.substring(index);
            switch (state) {
                case State.A_Answer_1:
                    Optional<RequestUsers> byId = requestUsersRepository.findById(id);
                    RequestUsers request = byId.get();
                    Ketmon muhlis = byId.get().getUsers();
                    switch (call) {
                        case Constant.Oziq:
                            send_food(message_id, muhlis.getChatId(), "Arizalar bilan ishlash bölimiga joylandi", "Assalom aleykum sizga oziq ovqatdan yordam körsata olamiz. \nKerakmi ? ⤵️ ", request);
                            request.setAnswer(false);
                            request.setActive(false);
                            request.setFood(true);
                            request.setAdmin(user.getFullName());
                            requestUsersRepository.save(request);
                            break;
                        case Constant.Rad:
                            send_rad(message_id, muhlis.getChatId(), "Ariza rad etildi", "Sizning arizangiz rad etildi");
                            request.setAnswer(true);
                            request.setActive(false);
                            requestUsersRepository.save(request);
                            break;

                        default:
                    }
                    break;
                case State.START:
                    Optional<RequestUsers> byId1 = requestUsersRepository.findById(id);
                    switch (call) {
                        case Constant.xa:
                            byId1.get().setAnswer(true);
                            requestUsersRepository.save(byId1.get());
                            send_muhlis(message_id, "Sizga tez orada aloqaga chiqamiz");
                            break;
                        case Constant.yoq:
                            byId1.get().setAnswer(true);
                            byId1.get().setActive(false);
                            byId1.get().setStatus(true);
                            requestUsersRepository.save(byId1.get());
                            send_muhlis(message_id, "Raxmat");
                            break;
                    }
                    break;
                case State.A_Answer_2:
                    switch (call) {
                        case Constant.ism:
                            user.setState(State.A_Answer_3);
                            userRepository.save(user);
                            Optional<RequestUsers> byId2 = requestUsersRepository.findById(id);
                            send_foodInfo(byId2.get(), year, message_id);

                            break;
                        case Constant.inlineBACK:
                            user.setState(State.START_ADMIN);
                            userRepository.save(user);
                            send_removeBack(message_id, "\uD83D\uDD19");
                            userMessage = "\uD83D\uDC40";
                            execute(userServiceBot.adduser(), null);
                            break;
                    }
                    break;
                case State.A_Answer_3:
                    switch (call) {
                        case Constant.yordam:
                            Optional<RequestUsers> byId5 = requestUsersRepository.findById(id);
                            byId5.get().setAnswer(true);
                            byId5.get().setActive(false);
                            byId5.get().setStatus(true);
                            byId5.get().getUsers().setBuffer(userChatId);
                            requestUsersRepository.save(byId5.get());
                            userRepository.save(byId5.get().getUsers());
                            send_removeBack(message_id, "\uD83D\uDCDD");
                            user.setState(State.A_REG_HELP_3);
                            userRepository.save(user);
                            userMessage = "Qanday yordam körsatiladi";
                            execute(userServiceBot.gethelplist(), null);
                            break;
                        case Constant.LocatsiyaON:
                            Optional<RequestUsers> byId4 = requestUsersRepository.findById(id);
                            sendLocation(byId4.get().getUsers().getId());
                            break;
                        case Constant.LocatsiyaOFF:
                            Optional<RequestUsers> byId2 = requestUsersRepository.findById(id);
                            send_locButton(byId2.get().getUsers().getChatId());
                            userMessage = "Sörov yoborildi";
                            execute(userServiceBot.back(), null);
                            break;
                        case Constant.bekor:
                            Optional<RequestUsers> byId3 = requestUsersRepository.findById(id);
                            byId3.get().setAnswer(true);
                            byId3.get().setActive(false);
                            byId3.get().setStatus(true);
                            requestUsersRepository.save(byId3.get());
                            user.setState(State.START_ADMIN);
                            userRepository.save(user);
                            send_removeBack(message_id, "Bekor qilindi ❌ ");
                            userMessage = "\uD83D\uDC40";
                            execute(userServiceBot.adduser(), null);
                            send_messega_standart(byId3.get().getUsers().getChatId(), "Sizning arizangiz bekor qilindi");
                            break;
                        case Constant.inlineBACK:
                            user.setState(State.START_ADMIN);
                            userRepository.save(user);
                            send_removeBack(message_id, "\uD83D\uDD19");
                            execute(userServiceBot.adduser(), null);
                            break;
                    }
                    break;
                case State.Find_2:
                    switch (call) {
                        case Constant.ism:
                            user.setState(State.Find_3);
                            userRepository.save(user);
                            send_helpInfo(id, year, Math.toIntExact(message_id));
                            break;
                        case Constant.inlineBACK:
                            user.setState(State.START_ADMIN);
                            userRepository.save(user);
                            send_removeBack(message_id, "\uD83D\uDD19");
                            userMessage = "\uD83D\uDC40";
                            execute(userServiceBot.adduser(), null);
                            break;

                    }
                    break;
                case State.Find_3:
                    Optional<Ketmon> ketmon = userRepository.findById(id);
                    switch (call) {
                        case Constant.yordam:
                            ketmon.get().setBuffer(userChatId);
                            userRepository.save(ketmon.get());
                            send_removeBack(message_id, "\uD83D\uDCDD");
                            user.setState(State.A_REG_HELP_3);
                            userRepository.save(user);
                            userMessage = "Qanday yordam körsatiladi";
                            execute(userServiceBot.gethelplist(), null);
                            break;
                        case Constant.hujjat:
                            break;
                        case Constant.LocatsiyaON:
                            sendLocation(ketmon.get().getId());
                            break;
                        case Constant.blacklist:
                            ketmon.get().setBlacklist(true);
                            userRepository.save(ketmon.get());
                            send_helpInfo(id, year, Math.toIntExact(message_id));
                            break;
                        case Constant.whitelist:
                            ketmon.get().setBlacklist(false);
                            userRepository.save(ketmon.get());
                            send_helpInfo(id, year, Math.toIntExact(message_id));
                            break;
                        case Constant.inlineBACK:
                            user.setState(State.Find);
                            userRepository.save(user);
                            userMessage = "Nima böyicha qidiramiz";
                            execute(userServiceBot.find_list(), null);
                            break;
                        default:
                            send_messega_standart(userChatId, "Tugmalardan foydalaning");
                    }
                    break;
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

    private void send_photo(List<PhotoSize> photo, String caption) {
        String f_id = photo.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null).getFileId();

        SendPhoto msg = new SendPhoto()
                .setChatId((long) -733377376)
                .setCaption(caption)
                .setPhoto(f_id);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void send_infoAnswer(RequestUsers answers, Integer year) {
        int board = Integer.parseInt(answers.getUsers().getAge());
        int yoshi = year - board;

        List<RequestUsers> byUsers = requestUsersRepository.findByUsers(answers.getUsers());
        int yordamSoni = byUsers.size();

        List<RequestUsers> status = requestUsersRepository.findByStatus(true);


        List<ChildList> allByUsers = childListRepository.findAllByUsers(answers.getUsers());
        int a = allByUsers.size();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.
                append("<b>Ariza raqami</b> N-" + answers.getId() + "    " + answers.getDate()).
                append("\n").
                append("\n<b>Manzil:</b> " + answers.getUsers().getRayon() + "\n" + answers.getUsers().getCity() + " " + answers.getUsers().getStreet_home() + "  uy").
                append("\n<b>Ism Familya :</b>" + answers.getUsers().getFullName()).
                append("\n<b>Yoshi :</b> " + yoshi).
                append("\n<b>Turmush örtog`i</b>  : " + answers.getUsers().getInfo_man()).
                append("\n<b>Farzandlari :</b>  " + a + " ta");


        for (ChildList allByUser : allByUsers) {

            stringBuilder.
                    append("\n" + "<b>" + allByUser.getSex() + "i</b>  " + allByUser.getName() + " " + allByUser.getAge() + " yosh");

        }
        stringBuilder.
                append("\n<b>Muammosi </b> " + answers.getDescription()).
                append("\n<b>Telefon raqami:</b> " + answers.getUsers().getPhoneNumber()).
                append("\n").
                append("<b>Murojatlar soni: </b> " + yordamSoni + " ta");


        if (status.size() != 0) {
            for (RequestUsers requestUsers : status) {
                stringBuilder.append("\n<b>Yordam berilgan: </b> " + requestUsers.getHelpType() + "  " + requestUsers.getDate());
            }
        }


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChatId);
        sendMessage.setParseMode("HTML");
        sendMessage.setText(String.valueOf(stringBuilder));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRows = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();


        inlineKeyboardButton.setText("Oziq ovqat").setCallbackData(answers.getId() + Constant.Oziq);
        inlineKeyboardButton1.setText("Rad etish").setCallbackData(answers.getId() + Constant.Rad);


        keyboardRows.add(inlineKeyboardButton);
        keyboardRows.add(inlineKeyboardButton1);

        rowList.add(keyboardRows);


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);


        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_foodAnswer(Ketmon user) {

        List<RequestUsers> foodUser = requestUsersRepository.findByFoodAndStatusAndAdmin(true, false, user.getFullName());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChatId);
        sendMessage.setParseMode("HTML");
        sendMessage.setText("<b>Oziq ovqat beriladiganlar</b>");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();


        for (RequestUsers requestUsers : foodUser) {
            List<InlineKeyboardButton> keyboardRows = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(requestUsers.getUsers().getFullName() + "  " + requestUsers.getUsers().getCity() + "  " + requestUsers.getUsers().getAge()).setCallbackData(requestUsers.getId() + Constant.ism);
            keyboardRows.add(inlineKeyboardButton);
            rowList.add(keyboardRows);
        }
        List<InlineKeyboardButton> keyboardRows2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        keyboardRows2.add(inlineKeyboardButton.setText(Constant.Back).setCallbackData(user.getId() + Constant.inlineBACK));
        rowList.add(keyboardRows2);


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(Constant.Back);
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setChatId(userChatId);
        sendMessage1.setText("tanlang ✅");
        sendMessage1.setReplyMarkup(replyKeyboardMarkup);


        try {
            execute(sendMessage);
            execute(sendMessage1);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_findName(String name) {

        List<Ketmon> ketmonList = userRepository.findByRoleAndFullNameContainingIgnoreCase(Role.ROLE_MUHLIS, name);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChatId);
        sendMessage.setParseMode("HTML");
        sendMessage.setText("\uD83D\uDD0E <b>" + name + "</b>");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();


        for (Ketmon muhlis : ketmonList) {
            List<InlineKeyboardButton> keyboardRows = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(muhlis.getFullName() + "  " + muhlis.getCity() + "  " + muhlis.getAge()).setCallbackData(muhlis.getId() + Constant.ism);
            keyboardRows.add(inlineKeyboardButton);
            rowList.add(keyboardRows);
        }
        List<InlineKeyboardButton> keyboardRows2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        keyboardRows2.add(inlineKeyboardButton.setText(Constant.Back).setCallbackData("4546" + Constant.inlineBACK));
        rowList.add(keyboardRows2);


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);


        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(Constant.Back);
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

        SendMessage sendMessage1 = new SendMessage();
        sendMessage1.setChatId(userChatId);
        sendMessage1.setText("tanlang ✅");
        sendMessage1.setReplyMarkup(replyKeyboardMarkup);


        try {
            execute(sendMessage);
            execute(sendMessage1);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendLocation(Integer id) {
        Optional<Ketmon> muhlis = userRepository.findById(id);

        Double lat = Double.valueOf(muhlis.get().getLat());
        Double lon = Double.valueOf(muhlis.get().getLon());

        SendLocation sendLocation = new SendLocation();
        sendLocation.setLatitude(lat.floatValue());
        sendLocation.setLongitude(lon.floatValue());
        sendLocation.setChatId(userChatId);

        try {
            execute(sendLocation);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_foodInfo(RequestUsers requestUsers, int year, long messeg) {
        int board = Integer.parseInt(requestUsers.getUsers().getAge());
        int yoshi = year - board;

        List<RequestUsers> byUsers = requestUsersRepository.findByUsers(requestUsers.getUsers());
        int yordamSoni = byUsers.size();


        List<ChildList> allByUsers = childListRepository.findAllByUsers(requestUsers.getUsers());
        int a = allByUsers.size();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.
                append("<b>Ariza raqami</b> N-" + requestUsers.getId() + "    " + requestUsers.getDate()).
                append("\n").
                append("\n<b>Manzil:</b> " + requestUsers.getUsers().getRayon() + "\n" + requestUsers.getUsers().getCity() + " " + requestUsers.getUsers().getStreet_home() + "  uy").
                append("\n<b>Ism Familya :</b>" + requestUsers.getUsers().getFullName()).
                append("\n<b>Yoshi :</b> " + yoshi).
                append("\n<b>Turmush örtog`i</b>  : " + requestUsers.getUsers().getInfo_man()).
                append("\n<b>Farzandlari :</b>  " + a + " ta");


        for (ChildList allByUser : allByUsers) {

            stringBuilder.
                    append("\n" + "<b>" + allByUser.getSex() + "i</b>  " + allByUser.getName() + " " + allByUser.getAge() + " yosh");

        }
        stringBuilder.
                append("\n<b>Muammosi </b> " + requestUsers.getDescription()).
                append("\n<b>Telefon raqami:</b> " + requestUsers.getUsers().getPhoneNumber()).
                append("\n").
                append("<b>Murojatlar soni: </b> " + yordamSoni + " ta");
        if (requestUsers.getUsers().isBlacklist()) {
            stringBuilder.
                    append("\n<b>Qora röyhatda turadi ❌❌❌ : </b>");
        }


        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(userChatId);
        new_message.setText(String.valueOf(stringBuilder));
        new_message.setMessageId(Math.toIntExact(messeg));
        new_message.setParseMode("HTML");


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRows1 = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();


        inlineKeyboardButton.setText("Yordam berish").setCallbackData(requestUsers.getId() + Constant.yordam);
        if (requestUsers.getUsers().getLat() != null) {
            inlineKeyboardButton1.setText("Locatsiya").setCallbackData(requestUsers.getId() + Constant.LocatsiyaON);
        } else
            inlineKeyboardButton1.setText("Locatsiya sörash").setCallbackData(requestUsers.getId() + Constant.LocatsiyaOFF);
        inlineKeyboardButton2.setText("Bekor qilish").setCallbackData(requestUsers.getId() + Constant.bekor);
        inlineKeyboardButton3.setText(Constant.Back).setCallbackData(requestUsers.getId() + Constant.inlineBACK);


        keyboardRows.add(inlineKeyboardButton);
        keyboardRows.add(inlineKeyboardButton1);
        keyboardRows.add(inlineKeyboardButton2);
        keyboardRows1.add(inlineKeyboardButton3);

        rowList.add(keyboardRows);
        rowList.add(keyboardRows1);


        inlineKeyboardMarkup.setKeyboard(rowList);
        new_message.setReplyMarkup(inlineKeyboardMarkup);


        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_helpInfo(Integer id, int year, long messeg) {
        Optional<Ketmon> users = userRepository.findById(id);
        Ketmon user = users.get();
        int board = Integer.parseInt(user.getAge());
        int yoshi = year - board;


        List<HelpAndUsers> byUsers = helpAndUserRepository.findByUsers(user);
        int yordamSoni = byUsers.size();


        List<ChildList> allByUsers = childListRepository.findAllByUsers(user);
        int a = allByUsers.size();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.
                append("<b>Ism Familyasi : </b>  " + user.getFullName()).
                append("\n").
                append("\n<b>Manzil:</b> " + user.getRayon() + "\n" + user.getCity() + " " + user.getStreet_home() + "  uy").
                append("\n<b>Yoshi :</b> " + yoshi).
                append("\n<b>Turmush örtog`i</b>  : " + user.getInfo_man()).
                append("\n<b>Farzandlari :</b>  " + a + " ta");


        for (ChildList allByUser : allByUsers) {

            stringBuilder.
                    append("\n" + "<b>" + allByUser.getSex() + "i</b>  " + allByUser.getName() + " " + allByUser.getAge() + " yosh");

        }
        stringBuilder.
                append("\n<b>Telefon raqami:</b> " + user.getPhoneNumber()).
                append("\n").
                append("<b>Yordam berilgan: </b> " + yordamSoni + " marta");

        if (user.isBlacklist()) {
            stringBuilder.append("\n<b>Qora röyhatda turadi ❌❌❌</b>");
        }

        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(userChatId);
        new_message.setText(String.valueOf(stringBuilder));
        new_message.setMessageId(Math.toIntExact(messeg));
        new_message.setParseMode("HTML");


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRows = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRows1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRows2 = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();


        inlineKeyboardButton.setText("Yordam berish").setCallbackData(user.getId() + Constant.yordam);
        inlineKeyboardButton1.setText("Hujjatlari").setCallbackData(user.getId() + Constant.hujjat);


        if (user.getLat() != null) {
            InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
            inlineKeyboardButton2.setText("Locatsiya").setCallbackData(user.getId() + Constant.LocatsiyaON);
            keyboardRows1.add(inlineKeyboardButton2);
        }
        if (user.isBlacklist()) {
            inlineKeyboardButton3.setText("Qora röyxatdan chiqarish").setCallbackData(user.getId() + Constant.whitelist);
        } else

            inlineKeyboardButton3.setText("Qora röyxatga qöshish").setCallbackData(user.getId() + Constant.blacklist);
        keyboardRows1.add(inlineKeyboardButton3);

        inlineKeyboardButton4.setText(Constant.Back).setCallbackData(user.getId() + Constant.inlineBACK);


        keyboardRows.add(inlineKeyboardButton);
        keyboardRows.add(inlineKeyboardButton1);
        keyboardRows2.add(inlineKeyboardButton4);

        rowList.add(keyboardRows);
        rowList.add(keyboardRows1);
        rowList.add(keyboardRows2);


        inlineKeyboardMarkup.setKeyboard(rowList);
        new_message.setReplyMarkup(inlineKeyboardMarkup);


        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void Find_photo(long chatId, String caption, String photoId) {

        SendPhoto msg = new SendPhoto()
                .setChatId(chatId)
                .setCaption(caption)
                .setPhoto(photoId);

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_food(long messeg, long userId, String userSms, String muhlisSms, RequestUsers requestUsers) {

        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(userChatId);
        new_message.setText(userSms);
        new_message.setMessageId(Math.toIntExact(messeg));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(muhlisSms);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRows = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();


        inlineKeyboardButton.setText("Kerak").setCallbackData(requestUsers.getId() + Constant.xa);
        inlineKeyboardButton1.setText("Kerak emas").setCallbackData(requestUsers.getId() + Constant.yoq);


        keyboardRows.add(inlineKeyboardButton);
        keyboardRows.add(inlineKeyboardButton1);

        rowList.add(keyboardRows);


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);


        try {
            execute(sendMessage);
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_rad(long messeg, long userId, String userSms, String muhlisSms) {

        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(userChatId);
        new_message.setText(userSms);
        new_message.setMessageId(Math.toIntExact(messeg));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText(muhlisSms);


        try {
            execute(sendMessage);
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_removeBack(long messeg, String sms) {

        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(userChatId);
        new_message.setText(sms);
        new_message.setMessageId(Math.toIntExact(messeg));


        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_muhlis(long messeg, String muhlisSms) {

        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(userChatId);
        new_message.setText(muhlisSms);
        new_message.setMessageId(Math.toIntExact(messeg));

        try {
            execute(new_message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_locButton(long mChatId) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("<b>Bizga locatsiyangiz kerak iltomos location \uD83D\uDCCD tugmani bosing</b>");
        sendMessage.setChatId(mChatId);
        sendMessage.setParseMode("HTML");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(new KeyboardButton("Location \uD83D\uDCCD").setRequestLocation(true));
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public ReplyKeyboardMarkup menu(Ketmon user) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow.add("Ehson qilish \uD83C\uDF19");
        keyboardRow1.add("Yordam sörash \uD83E\uDD32");
        if (user.getRole().equals(Role.ROLE_MUHLIS) && user.getLat() == null) {
            keyboardRow3.add(new KeyboardButton("Location yuborish \uD83D\uDCCD").setRequestLocation(true));
            keyboardRows.add(keyboardRow3);
        } else
            keyboardRow2.add((new KeyboardButton("Xodimlar uchun \uD83D\uDD10").setRequestContact(true)));
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
        keyboardRow.add("admin qöshish");
        keyboardRow1.add("admin öchirish");
        keyboardRow2.add("adminlar röyhati");
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


//   private void send_massage(Long chatId) {
//
//
//        long tg = -1001653891548l;
//        Optional<Ketmon> muhlislar = userRepository.findByChatId(chatId);
//        Optional<RequestUsers> requestUsers = requestUsersRepository.findByBuffer(userChatId);
//        Ketmon muhlis;
//        muhlis = muhlislar.get();
//
//        List<ChildList> allByUsers = childListRepository.findAllByUsers(muhlis);
//        int a = allByUsers.size();
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.
//                append("<b>Ariza raqami</b> N-" + requestUsers.get().getId() + "    " + requestUsers.get().getDate()).
//                append("\n").
//                append("\n<b>Manzil:</b> " + muhlis.getRayon() + "\n" + muhlis.getCity() + " " + muhlis.getStreet_home() + "  uy").
//                append("\n<b>Ism Familya :</b>" + muhlis.getFullName()).
//                append("\n<b>Yoshi :</b> " + muhlis.getAge()).
//                append("\n<b>Turmush örtog`i</b>  : " + muhlis.getInfo_man()).
//                append("\n<b>Farzandlari :</b>  " + a + " ta");
//
//
//        for (ChildList allByUser : allByUsers) {
//
//            stringBuilder.
//                    append("\n" + "<b>" + allByUser.getSex() + "i</b>  " + allByUser.getName() + " " + allByUser.getAge() + " yosh");
//
//        }
//        stringBuilder.
//                append("\n<b>Yordam turi</b> " + requestUsers.get().getDescription()).
//                append("\n<b>Telefon raqami:</b> " + muhlis.getPhoneNumber()).
//                append("\n<b>Locatsiya</b>  ⤵️ ");
//        SendLocation sendLocation = new SendLocation();
//        sendLocation.setLatitude(muhlis.getLat());
//        sendLocation.setLongitude(muhlis.getLon());
//        sendLocation.setChatId(tg);
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(tg);
//        sendMessage.setParseMode("HTML");
//        sendMessage.setText(String.valueOf(stringBuilder));
//        try {
//            execute(sendMessage);
//            execute(sendLocation);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }

    private void send_ms_admin(Long chatId, int year) {


        long tg = -733377376;
        Optional<Ketmon> muhlislar = userRepository.findByBuffer(chatId);
        Optional<HelpAndUsers> helpAndUsers = helpAndUserRepository.findByBuffer(chatId);
        Ketmon muhlis;
        muhlis = muhlislar.get();
        int yil = year;
        int board = Integer.parseInt(muhlis.getAge());
        int yoshi = yil - board;

        List<ChildList> allByUsers = childListRepository.findAllByUsers(muhlis);
        int a = allByUsers.size();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.
                append("<b>Yordam raqami</b> N-" + helpAndUsers.get().getId() + "    " + helpAndUsers.get().getDate()).
                append("\n").
                append("\n<b>Manzil:</b> " + muhlis.getRayon() + "\n" + muhlis.getCity() + " " + muhlis.getStreet_home() + "  uy").
                append("\n<b>Ism Familya :</b>" + muhlis.getFullName()).
                append("\n<b>Yoshi :</b> " + yoshi).
                append("\n<b>Turmush örtoği</b>  : " + muhlis.getInfo_man()).
                append("\n<b>Farzandlari :</b>  " + a + " ta");


        for (ChildList allByUser : allByUsers) {

            stringBuilder.
                    append("\n" + "<b>" + allByUser.getSex() + "i</b>  " + allByUser.getName() + " " + allByUser.getAge() + " yosh");

        }
        stringBuilder.
                append("\n<b>Yordam berildi :</b> " + helpAndUsers.get().getHelpType()).
                append("\n<b>Telefon raqami:</b> " + muhlis.getPhoneNumber());

        if (muhlis.getDescription() != null) {
            stringBuilder.
                    append("\n<b>Qöshimcha malumotlar</b> " + muhlis.getDescription()).append("\n");
            ;
        } else stringBuilder.append("\n");

        stringBuilder.
                append("\n<b>Yordam bergan hodim </b> " + helpAndUsers.get().getAdmin());
        List<InputMedia> media = new ArrayList<>();

        List<Passport> passports = passportRepository.findAllByUsers(muhlis);
        List<HelpAndUsersPhotos> helpAndUsersPhotos = helpAndUserPhotosRepository.findAllByHelps(helpAndUsers.get());

        if (helpAndUsersPhotos.size() != 0) {
            InputMedia photo = new InputMediaPhoto();
            photo.setCaption(String.valueOf(stringBuilder));
            photo.setMedia(helpAndUsersPhotos.get(0).getPhotoId());
            photo.setParseMode("HTML");
            media.add(photo);
        } else {
            InputMedia photo4 = new InputMediaPhoto();
            photo4.setCaption(String.valueOf(stringBuilder));
            photo4.setMedia(passports.get(0).getPhotoId());
            photo4.setParseMode("HTML");
            media.add(photo4);
        }


        for (Passport passport : passports) {
            InputMedia photo1 = new InputMediaPhoto();
            photo1.setMedia(passport.getPhotoId());
            media.add(photo1);
        }

        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(String.valueOf(-733377376));
        mediaGroup.setMedia(media);


        try {
            execute(mediaGroup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send_messega_standart(Long userChatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userChatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }


    public void handlePhoto() {
        List<HelpAndUsersPhotos> all = helpAndUserPhotosRepository.findAll();
        List<Passport> all1 = passportRepository.findAll();
        List<InputMedia> media = new ArrayList<>();
        InputMedia photo = new InputMediaPhoto();
        photo.setCaption("saasfsfa");
        photo.setMedia(all1.get(0).getPhotoId());
        media.add(photo);

        for (HelpAndUsersPhotos passport : all) {
            InputMedia photo1 = new InputMediaPhoto();
            photo1.setMedia(passport.getPhotoId());
            media.add(photo1);
        }


        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(userChatId);
        mediaGroup.setMedia(media);


        try {
            execute(mediaGroup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send_passport(Ketmon user) {
        List<Passport> all = passportRepository.findAllByUsers(user);
        List<InputMedia> media = new ArrayList<>();
        for (Passport passport : all) {
            InputMedia photo1 = new InputMediaPhoto();
            photo1.setMedia(passport.getPhotoId());
            media.add(photo1);
        }

        SendMediaGroup mediaGroup = new SendMediaGroup();
        mediaGroup.setChatId(userChatId);
        mediaGroup.setMedia(media);


        try {
            execute(mediaGroup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

