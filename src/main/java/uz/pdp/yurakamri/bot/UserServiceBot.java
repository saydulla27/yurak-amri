package uz.pdp.yurakamri.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.yurakamri.entity.City;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.Region;
import uz.pdp.yurakamri.entity.RequestUsers;
import uz.pdp.yurakamri.entity.enums.HelpType;
import uz.pdp.yurakamri.entity.enums.Role;
import uz.pdp.yurakamri.repository.CityRepository;
import uz.pdp.yurakamri.repository.RegionRepository;
import uz.pdp.yurakamri.repository.RequestUsersRepository;
import uz.pdp.yurakamri.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UserServiceBot {

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    RequestUsersRepository requestUsersRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BaseBot baseBot;

    @Autowired
    CityRepository cityRepository;

    public ReplyKeyboardMarkup getRegionList() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<Region> all = regionRepository.findAll();
        for (Region region : all) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(region.getName()));
            keyboardRows.add(keyboardRow);
        }
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(Constant.Back);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getRegionAnswer() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<Region> all = regionRepository.findAll();
        for (Region region : all) {
            List<RequestUsers> byAnswer = requestUsersRepository.findByAnswerAndRegion_Name(false, region.getName());
            if (byAnswer.size()!=0) {
                String size = String.valueOf(byAnswer.size());
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(new KeyboardButton(region.getName()+" ("+size+") \uD83D\uDD14"));
                keyboardRows.add(keyboardRow);
            }


        }


        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(Constant.Back);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getCityList(String city) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<City> all = cityRepository.findByRegion_Name(city);
        for (City city1 : all) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(city1.getName()));
            keyboardRows.add(keyboardRow);
        }
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(Constant.Back);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup addcontact() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("raqam yuborish").setRequestContact(true));
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup adduser() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();
        KeyboardRow keyboardRow4 = new KeyboardRow();
        keyboardRow.add("Röyhatga olish");
        keyboardRow1.add(Constant.Arizalar);
        keyboardRow1.add(Constant.ArizalarBilanIshlash);
        keyboardRow2.add(Constant.Qidirish);
        keyboardRow2.add(Constant.YordamOlganlar);
        keyboardRow3.add(Constant.Malumotlar);


        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getadmins() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<Ketmon> all = userRepository.findByRole(Role.ROlE_ADMIN);
        for (Ketmon admin : all) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(admin.getFullName() + " --> " + admin.getPhoneNumber()));
            keyboardRows.add(keyboardRow);
        }
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("menyuga qaytish"));
        keyboardRows.add(keyboardRow1);


        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup delateadmins() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<Ketmon> all = userRepository.findByRole(Role.ROlE_ADMIN);
        for (Ketmon admin : all) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(admin.getPhoneNumber()));
            keyboardRows.add(keyboardRow);
        }
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("menyuga qaytish"));
        keyboardRows.add(keyboardRow1);


        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }


    public ReplyKeyboardMarkup addchild() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow.add(Constant.OGIL);
        keyboardRow.add(Constant.QIZ);
        keyboardRow1.add(Constant.FARZANDIM_YOQ);
        keyboardRow1.add(Constant.YAKUNLASH);
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }


    public ReplyKeyboardMarkup back() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        keyboardRow.add(Constant.Back);
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup addchildage() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow.add("1");
        keyboardRow.add("2");
        keyboardRow.add("3");
        keyboardRow.add("4");
        keyboardRow.add("5");
        keyboardRow1.add("6");
        keyboardRow1.add("7");
        keyboardRow1.add("8");
        keyboardRow1.add("9");
        keyboardRow1.add("10");
        keyboardRow2.add("11");
        keyboardRow2.add("12");
        keyboardRow2.add("13");
        keyboardRow2.add("14");
        keyboardRow2.add("15");
        keyboardRow3.add("16");
        keyboardRow3.add("17");
        keyboardRow3.add("18");
        keyboardRow3.add("19");
        keyboardRow3.add("20");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup addlocation() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("locatsiya yuborish").setRequestLocation(true));
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup gethelplist() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<HelpType> helpTypeArrayList = Arrays.asList(HelpType.values());
        for (HelpType helpType : helpTypeArrayList) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(helpType.name()));
            keyboardRows.add(keyboardRow);
        }
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(Constant.Back);
        keyboardRows.add(keyboardRow1);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getfinish() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardRow keyboardRow3 = new KeyboardRow();
        KeyboardRow keyboardRow4 = new KeyboardRow();
        KeyboardRow keyboardRow5 = new KeyboardRow();
        keyboardRow.add("Rasm yuklash \uD83D\uDCF8");
        keyboardRow1.add("Hujjatlar yuklash");
        keyboardRow2.add("Qöshimcha malumot kiritish  ✏️");
        keyboardRow3.add(new KeyboardButton("locatsiya yuborish  \uD83D\uDCCD").setRequestLocation(true));
        keyboardRow4.add("Saqlash ✅");
        keyboardRow5.add("Bekor qilish ❎");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow3);
        keyboardRows.add(keyboardRow4);
        keyboardRows.add(keyboardRow5);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup help_1() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow.add("Özimga");
        keyboardRow1.add("Boshqa insonga");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup info_man() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow.add(Constant.VAFOT_ETGAN);
        keyboardRow.add(Constant.AJRASHGAN);
        keyboardRow1.add(Constant.Tashlab_ketgan);
        keyboardRow1.add(Constant.Yolgiz_ona);
        keyboardRow2.add(Constant.Guruh_bir);
        keyboardRow2.add(Constant.Guruh_ikki);
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

}
