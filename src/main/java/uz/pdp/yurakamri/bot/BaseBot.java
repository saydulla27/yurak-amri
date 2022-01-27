package uz.pdp.yurakamri.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
        Ketmon client = null;
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
                        if (userChatId ==  637495326) {
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
                            menu();
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
                            menu();
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
                                        menu();
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
                                                        menu();
                                                        break;
                                                    }
                                                } else
                                                    userMessage = "Murojatingizni yozib yuboring \n" + user.getFullName() + " shu kungacha " + list.size() + " marta murojat qilgansiz ";
                                                user.setState(State.U_HELP_9);
                                                userRepository.save(user);
                                                execute(null, null);
                                                break;
                                            } else
                                                userMessage = "Shaxar yoki Tumaningiz";
                                            user.setState(State.U_HELP_0);
                                            user.setWhom(text);
                                            userRepository.save(user);
                                            execute(userServiceBot.getRegionList(), null);
                                            break;
                                        } else
                                            userMessage = user.getFullName() + " sizda yordam sörash imkoniyati yöq";
                                        menu();
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
                                        break;
                                    case Constant.YordamOlganlar:
                                        break;
                                    case Constant.Malumotlar:
                                        break;

                                }
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
                                    List<RequestUsers> requestUsers = requestUsersRepository.findByAnswerAndRegion_Name(false, shaxar);
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
                                        userMessage = byPhoneNumber.get().getFullName() + " ni malumotlari bor nima yordam beriladi";
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
                                        userMessage = "Töğri kiriting";
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
                                        execute(null, null);
                                        break;
                                    case "Hujjatlar yuklash":
                                        userMessage = "Shu yerga pasport yuklang \uD83C\uDDFA\uD83C\uDDFF";
                                        user.setState(State.A_REG_pasport);
                                        userRepository.save(user);
                                        execute(null, null);
                                        break;
                                    case "Saqlash ✅":
                                        List<ChildList> allByUsers = childListRepository.findAllByUsers(byBuffer.get());
                                        userMessage = "Malumotlar saqlandi";
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        execute(userServiceBot.adduser(), null);
                                        send_ms_admin(userChatId, year);
                                        List<HelpAndUsersPhotos> helps = helpAndUserPhotosRepository.findAllByHelps(helpAnd.get());
                                        for (HelpAndUsersPhotos help : helps) {
                                            Find_photo(-733377376, byBuffer.get().getFullName(), help.getPhotoId());
                                        }
                                        List<Passport> passports = passportRepository.findAllByUsers(byBuffer.get());
                                        for (Passport passport : passports) {
                                            Find_photo(-733377376, byBuffer.get().getFullName(), passport.getPhotoId());
                                        }
                                        byBuffer.get().setChildrenInfo(String.valueOf(allByUsers.size()));
                                        byBuffer.get().setBuffer(0);
                                        userRepository.save(byBuffer.get());
                                        helpAnd.get().setBuffer(0);
                                        helpAndUserRepository.save(helpAnd.get());

                                        break;
                                    case "Qöshimcha malumot kiritish  ✏️":
                                        userMessage = "Malumotlarini kiriting";
                                        user.setState(State.A_REG_FINISHAND);
                                        userRepository.save(user);
                                        execute(null, null);
                                        break;
                                    case "Bekor qilish ❎":
                                        List<ChildList> childLists = childListRepository.findAllByUsers(byBuffer.get());
                                        Optional<HelpAndUsers> helpAndUsers = helpAndUserRepository.findByBuffer(userChatId);
                                        for (ChildList list : childLists) {
                                            childListRepository.delete(list);
                                        }
                                        List<HelpAndUsersPhotos> allByHelpAndUsers = helpAndUserPhotosRepository.findAllByHelps(helpAndUsers.get());
                                        for (HelpAndUsersPhotos allByHelpAndUser : allByHelpAndUsers) {
                                            helpAndUserPhotosRepository.delete(allByHelpAndUser);
                                        }
                                        List<Passport> allByUsers1 = passportRepository.findAllByUsers(byBuffer.get());
                                        for (Passport passport : allByUsers1) {
                                            passportRepository.delete(passport);
                                        }
                                        helpAndUserRepository.delete(helpAndUsers.get());
                                        userRepository.delete(byBuffer.get());
                                        userMessage = "\uD83D\uDC4C";
                                        user.setState(State.START_ADMIN);
                                        userRepository.save(user);
                                        execute(userServiceBot.adduser(), null);
                                        break;

                                }
                                break;
                            case State.A_REG_FINISHAND:
                                if (!text.isEmpty()) {
                                    byBuffer.get().setDescription(text);
                                    userRepository.save(byBuffer.get());
                                    user.setState(State.A_REG_FINISH);
                                    userRepository.save(user);
                                    userMessage = "Malumotlar saqlandi";
                                    execute(userServiceBot.getfinish(), null);
                                }
                                break;


                            case State.U_HELP_0:
                                if (!text.isEmpty()) {
                                    if (text.equals(Constant.Back)) {
                                        user.setState(State.START);
                                        userRepository.save(user);
                                        userMessage = "nima qilamiz";
                                        menu();
                                        break;
                                    }
                                    Optional<Region> region = regionRepository.findByName(text);
                                    user.setState(State.U_HELP_1);
                                    user.setRegion(region.get());
                                    user.setRayon(region.get().getName());
                                    userRepository.save(user);
                                    userMessage = "Shahringiz";
                                    execute(userServiceBot.getCityList(text), null);
                                    break;
                                }

                                break;
                            case State.U_HELP_1:
                                if (!text.isEmpty()) {
                                    userMessage = "Ism Familya kiriting";
                                    user.setState(State.U_HELP_2);
                                    user.setCity(text);
                                    userRepository.save(user);
                                    execute(null, null);
                                }
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
                                        userMessage = "Töğri kiriting";
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
                                if (!text.isEmpty()) {
                                    RequestUsers requestUsers = new RequestUsers();
                                    requestUsers.setDescription(text);
                                    requestUsers.setUsers(user);
                                    requestUsers.setStatus(false);
                                    requestUsers.setAnswer(false);
                                    requestUsers.setActive(true);
                                    requestUsers.setRegion(user.getRegion());
                                    requestUsers.setDate(strDate);
                                    requestUsersRepository.save(requestUsers);
                                    userMessage = "Malumotlar yuborildi bizdan javob kuting";
                                    user.setState(State.START);
                                    user.setStatus(true);
                                    userRepository.save(user);
                                    menu();
                                }
                                break;

                            default:
                                send_messega_standart(userChatId, "/start");
                        }
                    } else send_messega_standart(userChatId, "/start");
                }
            }
            if (update.getMessage().hasContact()) {
                String phone = update.getMessage().getContact().getPhoneNumber();
                if (phone.length() > 12) {
                    phone = phone.substring(1, 13);
                } else phone = update.getMessage().getContact().getPhoneNumber();
                Optional<Ketmon> optionalKetmon = userRepository.findByPhoneNumber(phone);
                if (optionalKetmon.isPresent()) {
                    optionalKetmon.get().setChatId(userChatId);
                    optionalKetmon.get().setRole(Role.ROlE_ADMIN);
                    optionalKetmon.get().setState(State.START_ADMIN);
                    userRepository.save(optionalKetmon.get());
                    Optional<Ketmon> byChatId = userRepository.findByChatId(1);
                    userRepository.delete(byChatId.get());
                    userMessage = "Assalom aleykum  ";
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
                userChatId = update.getMessage().getChatId();
                Float lat = update.getMessage().getLocation().getLatitude();
                Float lot = update.getMessage().getLocation().getLongitude();
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);


            }
            if (update.getMessage().hasPhoto()) {
                userChatId = update.getMessage().getChatId();
                Optional<Ketmon> byChatId = userRepository.findByChatId(userChatId);
                user = byChatId.get();
                String state = user.getState();
                switch (state) {
                    case State.A_REG_FOTO:
                        Optional<Ketmon> ketmon = userRepository.findByBuffer(userChatId);
                        Optional<HelpAndUsers> byUsers = helpAndUserRepository.findByUsers(ketmon.get());
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
                        case Constant.Aloqa:
                            break;
                        case Constant.Rad:
                            send_rad(message_id, muhlis.getChatId());
                            request.setAnswer(true);
                            request.setActive(false);
                            requestUsersRepository.save(request);
                            break;
                        case Constant.Black:
                            break;
                        case Constant.Oziq:
                            break;
                        default:
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
        List<InlineKeyboardButton> keyboardRows1 = new ArrayList<>();


        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();

        inlineKeyboardButton.setText("Oziq ovqat").setCallbackData(answers.getId() + Constant.Oziq);
        inlineKeyboardButton1.setText("Rad etish").setCallbackData(answers.getId() + Constant.Rad);
        inlineKeyboardButton2.setText("Aloqaga chiqish").setCallbackData(answers.getId() + Constant.Aloqa);
        inlineKeyboardButton3.setText("Qora röyhatga qöshish").setCallbackData(answers.getId() + Constant.Black);

        keyboardRows.add(inlineKeyboardButton);
        keyboardRows.add(inlineKeyboardButton1);
        keyboardRows1.add(inlineKeyboardButton2);
        keyboardRows1.add(inlineKeyboardButton3);
        rowList.add(keyboardRows);
        rowList.add(keyboardRows1);


        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);


        try {
            execute(sendMessage);
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

    private void send_rad(long messeg, long userId) {

        EditMessageText new_message = new EditMessageText();
        new_message.setChatId(userChatId);
        new_message.setText("Ariza rad etildi");
        new_message.setMessageId(Math.toIntExact(messeg));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(userId);
        sendMessage.setText("Sizning Arizangiz rad etildi ");


        try {
            execute(sendMessage);
            execute(new_message);
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
        keyboardRow.add("Ehson qilish \uD83C\uDF19");
        keyboardRow1.add("Yordam sörash \uD83E\uDD32");
        keyboardRow2.add("admin");
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


    private void send_massage(Long chatId) {


        long tg = -1001653891548l;
        Optional<Ketmon> muhlislar = userRepository.findByChatId(chatId);
        Optional<RequestUsers> requestUsers = requestUsersRepository.findByBuffer(userChatId);
        Ketmon muhlis;
        muhlis = muhlislar.get();

        List<ChildList> allByUsers = childListRepository.findAllByUsers(muhlis);
        int a = allByUsers.size();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.
                append("<b>Ariza raqami</b> N-" + requestUsers.get().getId() + "    " + requestUsers.get().getDate()).
                append("\n").
                append("\n<b>Manzil:</b> " + muhlis.getRayon() + "\n" + muhlis.getCity() + " " + muhlis.getStreet_home() + "  uy").
                append("\n<b>Ism Familya :</b>" + muhlis.getFullName()).
                append("\n<b>Yoshi :</b> " + muhlis.getAge()).
                append("\n<b>Turmush örtog`i</b>  : " + muhlis.getInfo_man()).
                append("\n<b>Farzandlari :</b>  " + a + " ta");


        for (ChildList allByUser : allByUsers) {

            stringBuilder.
                    append("\n" + "<b>" + allByUser.getSex() + "i</b>  " + allByUser.getName() + " " + allByUser.getAge() + " yosh");

        }
        stringBuilder.
                append("\n<b>Yordam turi</b> " + requestUsers.get().getDescription()).
                append("\n<b>Telefon raqami:</b> " + muhlis.getPhoneNumber()).
                append("\n<b>Locatsiya</b>  ⤵️ ");
        SendLocation sendLocation = new SendLocation();
        sendLocation.setLatitude(muhlis.getLat());
        sendLocation.setLongitude(muhlis.getLon());
        sendLocation.setChatId(tg);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tg);
        sendMessage.setParseMode("HTML");
        sendMessage.setText(String.valueOf(stringBuilder));
        try {
            execute(sendMessage);
            execute(sendLocation);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

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

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(tg);
        sendMessage.setParseMode("HTML");
        sendMessage.setText(String.valueOf(stringBuilder));


        try {
            execute(sendMessage);
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
}

