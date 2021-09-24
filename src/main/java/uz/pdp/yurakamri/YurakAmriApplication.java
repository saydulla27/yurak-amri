package uz.pdp.yurakamri;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import uz.pdp.yurakamri.bot.BaseBot;

@SpringBootApplication
public class YurakAmriApplication {
    public static void main(String[] args) {
        SpringApplication.run(YurakAmriApplication.class, args);
    }
}
