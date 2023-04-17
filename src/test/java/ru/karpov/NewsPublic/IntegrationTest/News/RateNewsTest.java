package ru.karpov.NewsPublic.IntegrationTest.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.IntegrationTest.Utils;
import ru.karpov.NewsPublic.models.Mark;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class RateNewsTest extends BaseTest {
    // Интеграция системы оценок и страницы новости
    // Производится оценка новости пользователем и производится проверка, что у пользователя нет возможности
    // повторно оценить ту же новости и текущая оценка добавлена в БД
    @Test
    @WithMockUser("Test")
    public void rateNews() throws Exception {
        // Создаем нового пользователя
        Utils.createNewUser(userRepo, "Test");
        // Создаем другого пользователя и новость от него
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test1");
        // Переходим на главную страницы
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что загрузились только одна новость от пользователя Test1
        Assertions.assertEquals(1, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test1]").size());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        String hrefOfNews = document.select("a[href*=/newsPage/]").get(0).attributes().get("href");
        String idNews = hrefOfNews.replaceAll("[^0-9]", "");
        // Проверяем, что ссылка правильного формата
        Assertions.assertTrue(hrefOfNews.matches("/newsPage/([0-9]+)"));
        // Переходим на эту новость
        res = mvc.perform(get("http://localhost:" + port + hrefOfNews))
                .andExpect(status().isOk())
                .andExpect(view().name("newsPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что можно оценить новость
        Assertions.assertEquals("/rateNews/" + idNews, document.select("form").first().attr("action"));
        Assertions.assertEquals(1, document.select("select#mark").size());
        Assertions.assertEquals("1", document.select("option").get(0).text());
        Assertions.assertEquals("2", document.select("option").get(1).text());
        Assertions.assertEquals("3", document.select("option").get(2).text());
        Assertions.assertEquals("4", document.select("option").get(3).text());
        Assertions.assertEquals("5", document.select("option").get(4).text());
        Assertions.assertEquals("Rate", document.select("button[type=submit]").first().text());
        // Проверяем, что оценок раньше не было
        Assertions.assertEquals(0, markRepo.findAll().size());
        mvc.perform(post("http://localhost:" + port + "/rateNews/" + idNews)
                        .param("mark", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        // Проверяем, что оценка корректно добавилась
        List<Mark> marks = markRepo.findAll();
        Assertions.assertEquals(1, marks.size());
        Assertions.assertEquals(5, marks.get(0).getMark());
        Assertions.assertEquals(Integer.parseInt(idNews), marks.get(0).getIsNews());
        Assertions.assertEquals("Test", marks.get(0).getIdUser());
        // Повторно заходим на новость
        res = mvc.perform(get("http://localhost:" + port + hrefOfNews))
                .andExpect(status().isOk())
                .andExpect(view().name("newsPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что повторно оценить новость нельзя
        Assertions.assertEquals(0, document.select("form").size());
        Assertions.assertEquals(0, document.select("select#mark").size());
        Assertions.assertEquals(0, document.select("option").size());
    }
}
