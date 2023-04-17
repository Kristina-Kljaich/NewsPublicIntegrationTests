package ru.karpov.NewsPublic.IntegrationTest.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.IntegrationTest.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

public class LogoutTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void logoutUser() throws Exception {
        Utils.createNewUser(userRepo, "Test");
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что в меню навигации есть кнопка Logout и Profile, что говорит, о том что пользователь аутентифицировался
        Assertions.assertEquals("Logout", document.select("a[href=/logout].me-3.py-2.text-dark.text-decoration-none").text());
        Assertions.assertEquals("Profile", document.select("a[href=/authProfilePage].me-3.py-2.text-dark.text-decoration-none").text());
        res = mvc.perform(get("http://localhost:" + port + "/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что в меню навигации есть кнопка LogIn и нет кнопки Logout, что говорит, о том что пользователь НЕ аутентифицирован
        Assertions.assertEquals(0, document.select("a[href=/logout].me-3.py-2.text-dark.text-decoration-none").size());
        Assertions.assertEquals("LogIn", document.select("a[href=/authProfilePage].me-3.py-2.text-dark.text-decoration-none").text());
        // Проверяем, что при нажатии на кнопки модуля навигации, на всех, кроме главной страницы, нас перенаправляет на /sso/login
        mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));

        mvc.perform(get("http://localhost:" + port + "/addNewsPage"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/sso/login"));

        mvc.perform(get("http://localhost:" + port + "/subscriptionsPage"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/sso/login"));

        mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/sso/login"));
    }
}
