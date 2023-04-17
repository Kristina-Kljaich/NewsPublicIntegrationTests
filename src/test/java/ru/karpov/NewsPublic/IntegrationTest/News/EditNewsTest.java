package ru.karpov.NewsPublic.IntegrationTest.News;

import org.apache.commons.lang.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.IntegrationTest.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EditNewsTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void editNews() throws Exception {
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test");
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        // Проверяем, что новость загрузилась
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals(1, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        String hrefOfNews = document.select("a[href*=/newsPage/]").get(0).attributes().get("href");
        // Проверяем, что ссылка правильного формата
        Assertions.assertTrue(hrefOfNews.matches("/newsPage/([0-9]+)"));
        String idNews = hrefOfNews.replaceAll("[^0-9]", "");
        // Переходим на эту новость
        res = mvc.perform(get("http://localhost:" + port + hrefOfNews))
                .andExpect(status().isOk())
                .andExpect(view().name("newsPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Edit publication", document.select("a[href=/editNews/" + idNews + "]").text());
        mvc.perform(get("http://localhost:" + port + "/editNews/" + idNews))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage"));

        final byte[] random = RandomStringUtils.random(5).getBytes();
        final MockMultipartFile file = new MockMultipartFile("photo", "image.jpg", null, random);

        // Заполняем прочие аттрибуты
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("Title", "Test1");
        requestParams.add("text", "john");
        requestParams.add("category", "Culture");

        // Добавляем статью
        mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addNews")
                        .file(file)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        // Проверяем, что новость загрузилась
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals(1, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test]").size());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        hrefOfNews = document.select("a[href*=/newsPage/]").get(0).attributes().get("href");
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
        Assertions.assertEquals("john", document.select("p.h3").text());
    }
}
