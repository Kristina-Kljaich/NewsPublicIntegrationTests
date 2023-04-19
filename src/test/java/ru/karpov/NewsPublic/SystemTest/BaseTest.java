package ru.karpov.NewsPublic.SystemTest;

import ch.qos.logback.core.joran.spi.DefaultClass;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import ru.karpov.NewsPublic.NewsPublicApplication;
import ru.karpov.NewsPublic.repos.markRepo;
import ru.karpov.NewsPublic.repos.newsRepo;
import ru.karpov.NewsPublic.repos.subscribeRepo;
import ru.karpov.NewsPublic.repos.userRepo;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootTest(
        classes = NewsPublicApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected userRepo userRepo;

    @Autowired
    protected newsRepo newsRepo;

    @Autowired
    protected subscribeRepo subscribeRepo;

    @Autowired
    protected markRepo markRepo;

    // После каждого теста очищаем БД
    @BeforeEach
    public void cleanUserRepo() {
        userRepo.deleteAll();
        newsRepo.deleteAll();
        subscribeRepo.deleteAll();
        markRepo.deleteAll();
    }
}
