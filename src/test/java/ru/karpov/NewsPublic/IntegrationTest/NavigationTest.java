package ru.karpov.NewsPublic.IntegrationTest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NavigationTest extends BaseTest{
    // Проверяем связь модуля навигации с остальными модулями с пользователем,
    // который НЕ прошел регистрацию до конца
    @WithMockUser("spring")
    @Test
    public void checkNavigationOfAuthorizationUserFirstTime() throws Exception {
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        // Проверяем, что есть 5 элементов в системе навигации, как и должно быть у авторизованного пользователя
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Elements els = document.select("a.me-3.py-2.text-dark.text-decoration-none");
        Assertions.assertEquals(5, els.size());

        // Проверяем Название кнопок системы навигации и то куда ведут они
        Assertions.assertEquals(1, els.get(0).getElementsByAttributeValue("href", "/")
                .get(0).getElementsContainingText("Main").size());
        Assertions.assertEquals(1, els.get(1).getElementsByAttributeValue("href", "/addNewsPage")
                .get(0).getElementsContainingText("Add").size());
        Assertions.assertEquals(1, els.get(2).getElementsByAttributeValue("href", "/subscriptionsPage")
                .get(0).getElementsContainingText("Subscriptions").size());
        Assertions.assertEquals(1, els.get(3).getElementsByAttributeValue("href", "/authProfilePage")
                .get(0).getElementsContainingText("Profile").size());
        Assertions.assertEquals(1, els.get(4).getElementsByAttributeValue("href", "/logout")
                .get(0).getElementsContainingText("Logout").size());

        // Проверяем, что при нажатии на кнопки системы навигации получаем нужные страницы
        // ВАЖНО, что при попытке перейти на страницу добавления поста или подписчиков нас отправляем на страницу добавления информации
        mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        mvc.perform(get("http://localhost:" + port + "/addNewsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addUserInfoPage"));
        mvc.perform(get("http://localhost:" + port + "/subscriptionsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("subscriptionsPage"));
        mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addUserInfoPage"));
        mvc.perform(get("http://localhost:" + port + "/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
    }

    // Проверяем связь модуля навигации с остальными модулями с пользователем,
    // который прошел регистрацию до конца
    @WithMockUser("spring")
    @Test
    public void checkNavigationOfAuthorizationUserNotFirstTime() throws Exception {
        // Добавляем нашего пользователя в БД, что будет говорить о том, что он зарегистрирован
        Utils.createNewUser(userRepo, "spring");
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        // Проверяем, что есть 5 элементов в системе навигации, как и должно быть у авторизованного пользователя
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Elements els = document.select("a.me-3.py-2.text-dark.text-decoration-none");
        Assertions.assertEquals(5, els.size());

        // Проверяем Название кнопок системы навигации и то куда ведут они, должно быть аналогично предыдущему тесту
        Assertions.assertEquals(1, els.get(0).getElementsByAttributeValue("href", "/")
                .get(0).getElementsContainingText("Main").size());
        Assertions.assertEquals(1, els.get(1).getElementsByAttributeValue("href", "/addNewsPage")
                .get(0).getElementsContainingText("Add").size());
        Assertions.assertEquals(1, els.get(2).getElementsByAttributeValue("href", "/subscriptionsPage")
                .get(0).getElementsContainingText("Subscriptions").size());
        Assertions.assertEquals(1, els.get(3).getElementsByAttributeValue("href", "/authProfilePage")
                .get(0).getElementsContainingText("Profile").size());
        Assertions.assertEquals(1, els.get(4).getElementsByAttributeValue("href", "/logout")
                .get(0).getElementsContainingText("Logout").size());

        // Проверяем, что при нажатии на кнопки системы навигации получаем нужные страницы
        // ВАЖНО, что при попытке перейти на страницу добавления поста или подписчиков нас отправляем на эти страницы
        mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));

        mvc.perform(get("http://localhost:" + port + "/addNewsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage"));

        mvc.perform(get("http://localhost:" + port + "/subscriptionsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("subscriptionsPage"));

        mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().isOk())
                .andExpect(view().name("authProfilePage"));

        mvc.perform(get("http://localhost:" + port + "/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
    }

    // Проверяем связь модуля навигации с остальными модулями с неавторизованным пользователем
    @Test
    public void checkNavigationOfNotAuthorizationUser() throws Exception {
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        // Проверяем, что есть 4 элементов в системе навигации, как и должно быть у неавторизованного пользователя, нет поля Профиль
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Elements els = document.select("a.me-3.py-2.text-dark.text-decoration-none");
        Assertions.assertEquals(4, els.size());

        // Проверяем название кнопок системы навигации и то куда ведут они, должно быть аналогично предыдущему тесту
        Assertions.assertEquals(1, els.get(0).getElementsByAttributeValue("href", "/")
                .get(0).getElementsContainingText("Main").size());
        Assertions.assertEquals(1, els.get(1).getElementsByAttributeValue("href", "/addNewsPage")
                .get(0).getElementsContainingText("Add").size());
        Assertions.assertEquals(1, els.get(2).getElementsByAttributeValue("href", "/subscriptionsPage")
                .get(0).getElementsContainingText("Subscriptions").size());
        Assertions.assertEquals(1, els.get(3).getElementsByAttributeValue("href", "/authProfilePage")
                .get(0).getElementsContainingText("LogIn").size());

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
