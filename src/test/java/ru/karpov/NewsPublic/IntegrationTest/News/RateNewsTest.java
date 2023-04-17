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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class RateNewsTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void retaNews() throws Exception {
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
        // Проверяем, что загрузились только две новости, при этом одна новость от пользователя Test и одна от пользователя Test1
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
        mvc.perform(post("http://localhost:" + port + "/rateNews/" + idNews)
                        .param("mark", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        res = mvc.perform(get("http://localhost:" + port + hrefOfNews))
                .andExpect(status().isOk())
                .andExpect(view().name("newsPage")).andDo(print())
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что повторно оценить новость нельзя
        Assertions.assertEquals(0, document.select("form").size());
        Assertions.assertEquals(0, document.select("select#mark").size());
        Assertions.assertEquals(0, document.select("option").size());
    }
}
