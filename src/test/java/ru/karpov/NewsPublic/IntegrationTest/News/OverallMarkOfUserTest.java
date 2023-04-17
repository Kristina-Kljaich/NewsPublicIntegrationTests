package ru.karpov.NewsPublic.IntegrationTest.News;

import org.h2.engine.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.IntegrationTest.Utils;
import ru.karpov.NewsPublic.models.userInfo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class OverallMarkOfUserTest extends BaseTest {
    // Интеграция системы оценок и профиля пользователя
    // Один пользователя оценивает новости другого пользователя и
    // проверяется, что средняя оценка установилась правильно у пользователя в профиле
    @Test
    @WithMockUser("Test")
    public void checkOverallMarkOfUser() throws Exception {
        // Создаем нового пользователя
        Utils.createNewUser(userRepo, "Test");
        // Создаем другого пользователя и новость от него
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test1");
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Culture", "Test1");
        // Переходим на главную страницы
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Получаем ссылку на ново созданную новость
        String hrefOFirstNews = document.select("a[href*=/newsPage/]").get(0).attributes().get("href");
        String hrefOfSecondNews = document.select("a[href*=/newsPage/]").get(1).attributes().get("href");
        // Проверяем, что ссылка правильного формата
        Assertions.assertTrue(hrefOFirstNews.matches("/newsPage/([0-9]+)"));
        Assertions.assertTrue(hrefOfSecondNews.matches("/newsPage/([0-9]+)"));
        String idFirstNews = hrefOFirstNews.replaceAll("[^0-9]", "");
        String idSecondNews = hrefOfSecondNews.replaceAll("[^0-9]", "");
        // Проверяем, что до оценки новости у пользователя Test1 нет оценок
        userInfo userInfo = userRepo.findUserById("Test1");
        Assertions.assertEquals(0, userInfo.getCountOfMarks());
        Assertions.assertEquals(0, userInfo.getSummaryOfMarks());
        // Оцениваем первую новость
        mvc.perform(post("http://localhost:" + port + "/rateNews/" + idFirstNews)
                        .param("mark", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        // Проверяем, что у пользователя Test1 появилась одна оценка
        userInfo = userRepo.findUserById("Test1");
        Assertions.assertEquals(1, userInfo.getCountOfMarks());
        Assertions.assertEquals(5, userInfo.getSummaryOfMarks());
        // Оцениваем вторую новость
        mvc.perform(post("http://localhost:" + port + "/rateNews/" + idSecondNews)
                        .param("mark", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        // Проверяем, что у пользователя Test1 две оценки
        userInfo = userRepo.findUserById("Test1");
        Assertions.assertEquals(2, userInfo.getCountOfMarks());
        Assertions.assertEquals(7, userInfo.getSummaryOfMarks());
        // Проверяем, среднюю оценку у пользователя в профиле
        res = mvc.perform(get("http://localhost:" + port + "/profilePage/Test1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage")).andDo(print())
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Mark:", document.select("label.fs-5.text-muted.text-center").get(2).text());
        Assertions.assertEquals("3.5", document.select("label.fs-5.text-muted.text-center").get(3).text());
    }
}
