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

public class SubscribeTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void checkValidSubscription() throws Exception {
        // Создаем двух пользователей
        Utils.createNewUser(userRepo, "Test");
        Utils.createNewUser(userRepo, "Test1");
        // Получаем страницу подписок пользователя Test
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/subscriptionsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("subscriptionsPage"))
                .andReturn();
        // Проверяем, что у него нет подписок
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Subscriptions", document.select("h1.display-4").get(0).text());
        Assertions.assertEquals("There is no subscriptions", document.select("h2.display-4").get(0).text());
        Assertions.assertNull(subscribeRepo.findSubscribeByIdUserSubscribeAndIdUser("Test1", "Test"));
        // Добавляем подписку
        mvc.perform(get("http://localhost:" + port + "/subscribeUser/Test1"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        // Проверяем, что запись добавилась в БД
        Assertions.assertNotNull(subscribeRepo.findSubscribeByIdUserSubscribeAndIdUser("Test1", "Test"));
        // Заново получаем страницу подписок
        res = mvc.perform(get("http://localhost:" + port + "/subscriptionsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("subscriptionsPage")).andDo(print())
                .andReturn();
        document = Jsoup.parse(res.getResponse().getContentAsString());
        // Находим там пользователя Test1
        Assertions.assertEquals("Test1", document.select("a[href=/profilePage/Test1]").get(0).text());
    }
}
