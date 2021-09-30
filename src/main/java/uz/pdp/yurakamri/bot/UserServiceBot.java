package uz.pdp.yurakamri.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.pdp.yurakamri.entity.Ketmon;
import uz.pdp.yurakamri.entity.Region;
import uz.pdp.yurakamri.repository.RegionRepository;
import uz.pdp.yurakamri.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceBot {

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BaseBot baseBot;

    public ReplyKeyboardMarkup getRegionList() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<Region> all = regionRepository.findAll();
        for (Region region : all) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(region.getName()));
            keyboardRows.add(keyboardRow);
        }
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
        keyboardRow.add("Royhatga olish");
        keyboardRow1.add("Royxatni korish");
        keyboardRows.add(keyboardRow);
        keyboardRows.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup getadmins() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        List<Ketmon> all = userRepository.findAll();
        for (Ketmon admin : all) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(admin.getPhoneNumber()));
            keyboardRows.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }



}
