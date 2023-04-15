package ru.karpov.NewsPublic.IntegrationTest;

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
import ru.karpov.NewsPublic.models.userInfo;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class AddUserInfoTests extends BaseTest {
    // Проверяем страницу добавления и API добавления информации о пользователе в БД
    @WithMockUser("spring")
    @Test
    public void addUserInfoCheckTest() throws Exception {
        System.out.println("PATH" + new File(".").getAbsolutePath());
        // Заходим через В профиль пользователя первый раз, проверяем, что перед нами страница addUserInfoPage
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addUserInfoPage"))
                .andReturn();

        // Проверяем наполнение страницы добавления информации
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals(document.select("label.form-label").get(0).getElementsContainingText("Username").size(), 1);
        Assertions.assertEquals(document.select("label.form-label").get(1).getElementsContainingText("Age").size(), 1);
        Assertions.assertEquals(document.select("label.form-label").get(2).getElementsContainingText("Description").size(), 1);
        Assertions.assertEquals(document.select("label.form-label").get(3).getElementsContainingText("Photo").size(), 1);
        Assertions.assertEquals(document.select("button").get(0).getElementsContainingText("Add info").size(), 1);

        // Создаем Mock файл картинки
        final byte[] random = RandomStringUtils.random(5).getBytes();
        final MockMultipartFile file = new MockMultipartFile("image", "image.jpg", null, random);

        // Заполняем прочие аттрибуты
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("description", "spring");
        requestParams.add("username", "john");
        requestParams.add("age", "30");

        // Отправляем запрос на добавление информации о пользователе и проверяем, что нас переправит на главную страницу
        mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addUserInfo")
                        .file(file)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));

        // Проверяем, что пользователь успешно добавлен
        userInfo user = userRepo.findUserById("spring");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getName(), "john");
        Assertions.assertEquals(user.getId(), "spring");
        Assertions.assertEquals(user.getCountOfMarks(), 0);
        Assertions.assertEquals(user.getSummaryOfMarks(), 0);
        Assertions.assertEquals(user.getAge(), 30);
        Assertions.assertEquals(user.getDescription(), "spring");

        // Проверяем, что после добавления пользователя, он попадает на страницу authProfilePage, т.е. на свой профиль
        mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().isOk())
                .andExpect(view().name("authProfilePage"));
    }
}
