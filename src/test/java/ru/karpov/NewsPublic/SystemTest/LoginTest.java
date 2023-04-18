package ru.karpov.NewsPublic.SystemTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.karpov.NewsPublic.NewsPublicApplication;
import io.github.bonigarcia.wdm.WebDriverManager;


import java.time.Duration;
import java.util.concurrent.TimeUnit;


@SpringBootTest(
        classes = NewsPublicApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LoginTest {
    @LocalServerPort
    protected int port;
    WebDriver driver;

    @BeforeEach
    void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(ops);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(5000));
        driver.manage().timeouts().setScriptTimeout(5000, TimeUnit.MILLISECONDS);
        driver.manage().timeouts().pageLoadTimeout(10000, TimeUnit.MILLISECONDS);
    }

    @AfterEach
    void teardown() {
//        driver.quit();
    }

    @Test
    public void userCanLoginByUsername() {
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofMillis(5000));
        driver.get("http://localhost:8081/");
        driver.manage().window().setSize(new Dimension(1920, 1040));
        driver.findElement(By.linkText("LogIn")).click();
        Assertions.assertTrue(driver.getCurrentUrl().matches("http://localhost:\\d*/auth/realms/SAT/protocol/openid-connect/auth[\\s\\S]*"));
        driver.findElement(By.linkText("Register")).click();
        Assertions.assertTrue(driver.getCurrentUrl().matches("http://localhost:\\d*/auth/realms/SAT/login-actions/registration\\?client_id=NewsPublic[\\s\\S]*"));
        driver.findElement(By.id("email")).sendKeys("user@example.com");
        driver.findElement(By.id("username")).sendKeys("example");
        driver.findElement(By.id("password")).sendKeys("secret");
        driver.findElement(By.id("password-confirm")).sendKeys("secret");
        driver.findElement(By.cssSelector(".pf-c-button")).click();
        Assertions.assertEquals(driver.getCurrentUrl(), "http://localhost:" + port + "/favicon.ico");
    }
}
