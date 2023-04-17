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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class NewsToProfileTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void loadFromNewsToProfile() throws Exception {
        // Создаем пользователя
        Utils.createNewUser(userRepo, "Test");
        // Создаем другого пользователя и новость от него
        Utils.createNewNews(userRepo, newsRepo, "Test1", "Sport", "Test1");
        // Переходим на главную страницу
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        // Проверяем, что новость загрузилась
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals(1, document.select("div.col").size());
        Assertions.assertEquals(1, document.select("a[href=/profilePage/Test1]").size());
        Assertions.assertEquals(1, document.select("a[href*=/newsPage/]").size());
        String hrefOfNews = document.select("a[href*=/newsPage/]").get(0).attributes().get("href");
        // Проверяем, что ссылка правильного формата
        Assertions.assertTrue(hrefOfNews.matches("/newsPage/([0-9]+)"));
        // Переходим по ссылке на пользователя указанного на новости
        res = mvc.perform(get("http://localhost:" + port + "/profilePage/Test1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage"))
                .andReturn();
        // Проверяем, что у этого пользователя есть статья и мы действительно перешли на этого пользователя
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Test1", document.select("h1.display-4.fw-normal.text-center").first().text());
        Assertions.assertEquals("Test", document.select("p.fs-5.text-muted.text-center").first().text());
        Assertions.assertEquals("Age:", document.select("label.fs-5.text-muted.text-center").get(0).text());
        Assertions.assertEquals("34", document.select("label.fs-5.text-muted.text-center").get(1).text());
        Assertions.assertEquals("Mark:", document.select("label.fs-5.text-muted.text-center").get(2).text());
        Assertions.assertEquals("0.0", document.select("label.fs-5.text-muted.text-center").get(3).text());
        Assertions.assertEquals(hrefOfNews, document.select("a[href*=/newsPage/]").first().attr("href"));
    }
}
