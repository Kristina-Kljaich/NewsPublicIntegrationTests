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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class SubscriptionsProfileTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void checkValidSubscription() throws Exception {
        // Создаем двух пользователей
        Utils.createNewUser(userRepo, "Test");
        Utils.createNewUser(userRepo, "Test1");
        Utils.createNewSubscription(subscribeRepo, "Test", "Test1");
        // Получаем страницу подписок пользователя Test
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/subscriptionsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("subscriptionsPage"))
                .andReturn();
        // Проверяем, что у него нет подписок
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        // Находим там пользователя Test1
        Assertions.assertEquals("Test1", document.select("a[href=/profilePage/Test1]").get(0).text());
        res = mvc.perform(get("http://localhost:" + port + "/profilePage/Test1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage")).andDo(print())
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Test1", document.select("h1.display-4.fw-normal.text-center").first().text());
        Assertions.assertEquals("Test", document.select("p.fs-5.text-muted.text-center").first().text());
        Assertions.assertEquals("", document.select("img").first().attributes().get("src"));
        Assertions.assertEquals("Age:", document.select("label.fs-5.text-muted.text-center").get(0).text());
        Assertions.assertEquals("34", document.select("label.fs-5.text-muted.text-center").get(1).text());
        Assertions.assertEquals("Mark:", document.select("label.fs-5.text-muted.text-center").get(2).text());
        Assertions.assertEquals("0.0", document.select("label.fs-5.text-muted.text-center").get(3).text());

    }
}
