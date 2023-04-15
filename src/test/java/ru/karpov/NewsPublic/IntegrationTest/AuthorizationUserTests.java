package ru.karpov.NewsPublic.IntegrationTest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class AuthorizationUserTests extends BaseTest{
    // Проверяем связь модуля авторизации и остального приложения
    @WithMockUser("spring")
    @Test
    public void authWithCorrectUser() throws Exception {
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что в меню навигации есть кнопка Logout и Profile, что говорит, о том что пользователь аутентифицировался
        Assertions.assertEquals(1, document
                .select("a[href=/logout].me-3.py-2.text-dark.text-decoration-none")
                .get(0)
                .getElementsContainingText("Logout")
                .size());
        Assertions.assertEquals(1, document
                .select("a[href=/authProfilePage].me-3.py-2.text-dark.text-decoration-none")
                .get(0)
                .getElementsContainingText("Profile")
                .size());
    }

    @Test
    public void authWithAnonymousUser() throws Exception {
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Проверяем, что в меню навигации есть кнопка LogIn и нет кнопки Logout, что говорит, о том что пользователь НЕ аутентифицирован
        Assertions.assertEquals(0, document
                .select("a[href=/login].me-3.py-2.text-dark.text-decoration-none")
                .size());
        Assertions.assertEquals(1, document
                .select("a[href=/authProfilePage].me-3.py-2.text-dark.text-decoration-none")
                .get(0)
                .getElementsContainingText("LogIn")
                .size());
    }
}

