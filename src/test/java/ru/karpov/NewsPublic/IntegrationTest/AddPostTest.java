package ru.karpov.NewsPublic.IntegrationTest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AddPostTest extends BaseTest{
    @Test
    @WithMockUser("Test")
    public void addPostTest() throws Exception{
        // Создаем нового пользователя
        Utils.createNewUser(userRepo, "Test");
        // Переходим на страницу добавления поста
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/addNewsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage")).andDo(print())
                .andReturn();
        // Проверяем страницу поста на валидность
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Title", document.select("label.form-control").get(0).text());
        Assertions.assertEquals("Text", document.select("label.form-control").get(1).text());
        Assertions.assertEquals("Category", document.select("label.form-control").get(2).text());
        // Проверяем, что раньше такой статьи не было в БД
        // Добавляем статью
        // Проверяем, что статья добавилась БД
    }
}
