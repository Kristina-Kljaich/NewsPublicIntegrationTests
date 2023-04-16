package ru.karpov.NewsPublic.IntegrationTest.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.IntegrationTest.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class GettingDeletedNewsTest extends BaseTest {
    // Создаем статью, пользователь Test1 загружает страницу со статьями, после этого пользователь Test удаляет свою статью
    // и при попытке пользователя Test1 зайти на удаленную статью должно высветиться сообщение об ошибке
    @Test
    @WithMockUser("Test1")
    public void getDeletedNews() throws Exception {
        // Пользователь Test создает статью
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test");
        // Создаем пользователя Test1
        Utils.createNewUser(userRepo, "Test1");
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что загрузилась одна новость
        Assertions.assertEquals(1, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        String hrefOfNews = document.select("a[href*=/newsPage/]").get(0).attributes().get("href");
        // Проверяем, что ссылка правильного формата
        Assertions.assertTrue(hrefOfNews.matches("/newsPage/([0-9]+)"));
        String idNews = hrefOfNews.replaceAll("[^0-9]", "");
        // Удаляем новость, предварительно проверяем, что новость была, а после проверяем, что была успешно удалена
        Assertions.assertNotNull(newsRepo.findNewsById(Integer.parseInt(idNews)));
        deleteNews(idNews);
        Assertions.assertNull(newsRepo.findNewsById(Integer.parseInt(idNews)));

        // Переходим на удаленную новость
        res = mvc.perform(get("http://localhost:" + port + hrefOfNews))
                .andExpect(status().isOk())
                .andExpect(view().name("newsPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что выведено сообщение об ошибке
        Assertions.assertEquals("Can not find news", document.select("h1.display-4.fw-normal").first().text());
    }

    @WithMockUser("Test")
    public void deleteNews(String id) throws Exception {
        mvc.perform(get("http://localhost:" + port + "/deleteNews/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
    }
}
