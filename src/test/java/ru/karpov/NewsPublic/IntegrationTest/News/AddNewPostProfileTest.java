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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AddNewPostProfileTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void addNewsAnotherProfile() throws Exception{
        Utils.createNewUser(userRepo, "Test");
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test1");
        // Переходим по ссылке на пользователя указанного на новости
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/profilePage/Test1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage"))
                .andReturn();
        // Проверяем, что у этого пользователя есть статья
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Test1", document.select("h1.display-4.fw-normal.text-center").first().text());
        Assertions.assertEquals("Test", document.select("p.fs-5.text-muted.text-center").first().text());
        Assertions.assertEquals("Age:", document.select("label.fs-5.text-muted.text-center").get(0).text());
        Assertions.assertEquals("34", document.select("label.fs-5.text-muted.text-center").get(1).text());
        Assertions.assertEquals("Mark:", document.select("label.fs-5.text-muted.text-center").get(2).text());
        Assertions.assertEquals("0.0", document.select("label.fs-5.text-muted.text-center").get(3).text());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        // Выбираем категорию Sport
        res = mvc.perform(post("http://localhost:" + port + "/reloadProfilePage/Test1")
                        .param("category", "Sport"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals(1, document.select("div.col").size());
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
        Assertions.assertEquals("Test1", document.select("h1.display-4.fw-normal").text());
        Assertions.assertEquals("Test", document.select("a[href=/profilePage/Test]").text());
        Assertions.assertEquals("Bla", document.select("p.h3").text());

        // Выбираем категорию Culture
        res = mvc.perform(post("http://localhost:" + port + "/reloadProfilePage/Test1")
                        .param("category", "Culture"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что загрузились только одна новость от пользователя Test
        Assertions.assertEquals(0, document.select("div.col").size());
        Assertions.assertEquals(0, document.select("a[href*=/newsPage/]").size());
    }
    @Test
    @WithMockUser("Test")
    public void addNewsMyProfile() throws Exception{
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test");
        // Переходим по ссылке на пользователя указанного на новости
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/profilePage/Test"))
                .andExpect(status().isOk())
                .andExpect(view().name("authProfilePage"))
                .andReturn();
        // Проверяем, что у этого пользователя есть статья
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Test", document.select("h1.display-4.fw-normal.text-center").first().text());
        Assertions.assertEquals("Test", document.select("p.fs-5.text-muted.text-center").first().text());
        Assertions.assertEquals("Age:", document.select("label.fs-5.text-muted.text-center").get(0).text());
        Assertions.assertEquals("34", document.select("label.fs-5.text-muted.text-center").get(1).text());
        Assertions.assertEquals("Mark:", document.select("label.fs-5.text-muted.text-center").get(2).text());
        Assertions.assertEquals("0.0", document.select("label.fs-5.text-muted.text-center").get(3).text());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        // Выбираем категорию Sport
        res = mvc.perform(post("http://localhost:" + port + "/reloadAuthProfilePage")
                        .param("category", "Sport"))
                .andExpect(status().isOk())
                .andExpect(view().name("authProfilePage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals(1, document.select("div.col").size());
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
        Assertions.assertEquals("Test1", document.select("h1.display-4.fw-normal").text());
        Assertions.assertEquals("Test", document.select("a[href=/profilePage/Test]").text());
        Assertions.assertEquals("Bla", document.select("p.h3").text());

        // Выбираем категорию Culture
        res = mvc.perform(post("http://localhost:" + port + "/reloadAuthProfilePage")
                        .param("category", "Culture"))
                .andExpect(status().isOk())
                .andExpect(view().name("authProfilePage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что загрузились только одна новость от пользователя Test
        Assertions.assertEquals(0, document.select("div.col").size());
        Assertions.assertEquals(0, document.select("a[href*=/newsPage/]").size());
    }
}
