package ru.karpov.NewsPublic.IntegrationTest.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.IntegrationTest.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class NewsPositiveTest extends BaseTest {
    // Интеграция главной страницы и модуля новостей
    // Переход на новость через главную страницу
    @Test
    @WithMockUser("Test")
    public void checkGettingNews() throws Exception {
        // Создаем пользователя
        Utils.createNewUser(userRepo, "Test");
        // Создаем другого пользователя и новость от него
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test1");
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        // Проверяем, что новость загрузилась
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals(1, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test1]").size());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        String hrefOfNews = document.select("a[href*=/newsPage/]").get(0).attributes().get("href");
        // Проверяем, что ссылка правильного формата
        Assertions.assertTrue(hrefOfNews.matches("/newsPage/([0-9]+)"));
        // Переходим на эту новость
        res = mvc.perform(get("http://localhost:" + port + hrefOfNews))
                .andExpect(status().isOk())
                .andExpect(view().name("newsPage"))
                .andReturn();
        // Проверяем, что в новости все поля правильные
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Test1", document.select("h1.display-4.fw-normal").first().text());
        Assertions.assertEquals("Test1", document.select("a[href=/profilePage/Test1]").first().text());
        Assertions.assertEquals("Bla", document.select("p.h3").first().text());
    }
}
