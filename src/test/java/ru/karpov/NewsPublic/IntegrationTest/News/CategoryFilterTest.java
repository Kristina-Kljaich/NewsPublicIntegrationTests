package ru.karpov.NewsPublic.IntegrationTest.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.IntegrationTest.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CategoryFilterTest extends BaseTest {
    // Интеграция модуля фильтрации новостей и БД
    // Добавляются несколько новостей разных категорий и проверяется, что отображаются только те новостт, категория которых выбрана
    @Test
    public void checkCategoryFilter() throws Exception {
        // Создаем пользователя и новости от него
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test");
        Utils.createNewNews(userRepo, newsRepo, "Test2", "Sport", "Test1");
        Utils.createNewNews(userRepo, newsRepo, "Test3", "Culture", "Test");

        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что есть кнопка перезагрузки страницы
        Assertions.assertEquals(1, document.select("form[action=/reloadMain]").size());
        // Проверяем, что загрузились все три новости, при этом две новости от пользователя Test и одна от пользователя Test1
        Assertions.assertEquals(3, document.select("div.col").size());
        Assertions.assertEquals(2, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test1]").size());
        Assertions.assertEquals(3, document.select("a[href*=/newsPage/]").size());

        // Выбираем категорию Sport
        res = mvc.perform(post("http://localhost:" + port + "/reloadMain")
                        .param("category", "Sport"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что загрузились только две новости, при этом одна новость от пользователя Test и одна от пользователя Test1
        Assertions.assertEquals(2, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test1]").size());
        Assertions.assertEquals(2, document.select("a[href*=/newsPage/]").size());

        // Выбираем категорию Culture
        res = mvc.perform(post("http://localhost:" + port + "/reloadMain")
                        .param("category", "Culture"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что загрузились только одна новость от пользователя Test
        Assertions.assertEquals(1, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());

        // Выбираем категорию Economic
        res = mvc.perform(post("http://localhost:" + port + "/reloadMain")
                        .param("category", "Economic"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что не загрузилась ни одна новость
        Assertions.assertEquals(0, document.select("div.col").size());
        Assertions.assertEquals(0, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(0, document.select("a[href*=/newsPage/]").size());

        res = mvc.perform(post("http://localhost:" + port + "/reloadMain")
                        .param("category", "All"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что снова загрузились все три новости, при этом две новости от пользователя Test и одна от пользователя Test1
        Assertions.assertEquals(3, document.select("div.col").size());
        Assertions.assertEquals(2, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test1]").size());
        Assertions.assertEquals(3, document.select("a[href*=/newsPage/]").size());
    }
}
