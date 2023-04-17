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
import ru.karpov.NewsPublic.models.Categories;
import ru.karpov.NewsPublic.models.News;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AddNewsTest extends BaseTest {
    // Интеграция добавления статьи и БД
    // Добавляем статью и проверяем, что все корректно располагается и статья добавляется
    @Test
    @WithMockUser("Test")
    public void addPostTest() throws Exception{
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
        // Проверяем, что раньше такой статьи не было в БД
        Assertions.assertEquals(0, newsRepo.findAll().size());

        // Создаем Mock файл картинки
        final byte[] random = RandomStringUtils.random(5).getBytes();
        final MockMultipartFile file = new MockMultipartFile("photo", "image.jpg", null, random);

        // Заполняем прочие аттрибуты
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("Title", "Test1");
        requestParams.add("text", "john");
        requestParams.add("category", "Sport");

        // Добавляем статью
        mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addNews")
                        .file(file)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        // Проверяем, что статья добавилась БД
        List<News> list = newsRepo.findAll();
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("Test1", list.get(0).getName());
        Assertions.assertEquals("john", list.get(0).getText());
        Assertions.assertEquals("Test", list.get(0).getAuthorName());
        Assertions.assertEquals(Categories.Sport, list.get(0).getCategory());
    }
}
