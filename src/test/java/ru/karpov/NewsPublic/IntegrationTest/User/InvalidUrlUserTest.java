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

public class InvalidUrlUserTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void getNotExistingUserUrl() throws Exception {
        Utils.createNewUser(userRepo, "Test");
        Assertions.assertNull(userRepo.findUserByName("Test1"));
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/profilePage/Test1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profilePage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Can not find such user", document.select("div.text-danger").text());
    }
}
