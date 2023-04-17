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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AddNewsWithEmptyFieldsTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void addNewsWithEmptyTitle() throws Exception{
        // Создаем нового пользователя
        Utils.createNewUser(userRepo, "Test");
        // Переходим на страницу добавления поста
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/addNewsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage"))
                .andReturn();
        // Проверяем страницу поста на валидность
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("/addNews", document.select("form.needs-validation").get(0).attr("action"));
        Assertions.assertEquals("Title", document.select("label.form-label").get(0).text());
        Assertions.assertEquals("Text", document.select("label.form-label").get(1).text());
        Assertions.assertEquals("Category", document.select("label.form-label").get(2).text());
        Assertions.assertEquals(1, document.select("input#photo").size());
        // Проверяем, что нет новостей в БД
        Assertions.assertEquals(0, newsRepo.findAll().size());

        // Создаем Mock файл картинки
        final byte[] random = RandomStringUtils.random(5).getBytes();
        final MockMultipartFile file = new MockMultipartFile("photo", "image.jpg", null, random);

        // Заполняем прочие аттрибуты
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("Title", "");
        requestParams.add("text", "john");
        requestParams.add("category", "Sport");

        // Добавляем статью
        res = mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addNews")
                        .file(file)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage")).andDo(print())
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Enter information", document.select("div.text-danger").first().text());
        // Проверяем, что нет новостей в БД
        Assertions.assertEquals(0, newsRepo.findAll().size());
    }
    @Test
    @WithMockUser("Test")
    public void addNewsWithEmptyText() throws Exception{
        // Создаем нового пользователя
        Utils.createNewUser(userRepo, "Test");
        // Переходим на страницу добавления поста
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/addNewsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage"))
                .andReturn();
        // Проверяем страницу поста на валидность
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("/addNews", document.select("form.needs-validation").get(0).attr("action"));
        Assertions.assertEquals("Title", document.select("label.form-label").get(0).text());
        Assertions.assertEquals("Text", document.select("label.form-label").get(1).text());
        Assertions.assertEquals("Category", document.select("label.form-label").get(2).text());
        Assertions.assertEquals(1, document.select("input#photo").size());
        // Проверяем, что нет новостей в БД
        Assertions.assertEquals(0, newsRepo.findAll().size());

        // Создаем Mock файл картинки
        final byte[] random = RandomStringUtils.random(5).getBytes();
        final MockMultipartFile file = new MockMultipartFile("photo", "image.jpg", null, random);

        // Заполняем прочие аттрибуты, кроме
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("Title", "Test");
        requestParams.add("text", "");
        requestParams.add("category", "Sport");

        // Добавляем статью
        res = mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addNews")
                        .file(file)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage")).andDo(print())
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Enter information", document.select("div.text-danger").first().text());
        // Проверяем, что нет новостей в БД
        Assertions.assertEquals(0, newsRepo.findAll().size());
    }
}
