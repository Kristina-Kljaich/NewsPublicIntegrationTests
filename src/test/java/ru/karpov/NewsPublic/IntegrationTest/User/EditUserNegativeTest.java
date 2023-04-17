package ru.karpov.NewsPublic.IntegrationTest.User;

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
import ru.karpov.NewsPublic.IntegrationTest.BaseTest;
import ru.karpov.NewsPublic.models.userInfo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class EditUserNegativeTest extends BaseTest {
    @Test
    @WithMockUser("Test")
    public void editUserWithAgeMoreThan100() throws Exception {
        checkErrorWhileUpdate("101", null, null);
    }

    @Test
    @WithMockUser("Test")
    public void editUserWithAgeLessThanZero() throws Exception {
        checkErrorWhileUpdate("-1", null, null);
    }

    @Test
    @WithMockUser("Test")
    public void editUserWithEmptyUsername() throws Exception {
        checkErrorWhileUpdate(null, null, "");
    }

    @Test
    @WithMockUser("Test")
    public void editUserWithEmptyDescription() throws Exception {
        checkErrorWhileUpdate(null, "", null);
    }

    @Test
    @WithMockUser("Test")
    public void editUserWithUsernameMoreThan15() throws Exception {
        String username = RandomStringUtils.random(16);
        Assertions.assertTrue(username.length() > 15);
        checkErrorWhileUpdate(null, null, username);
    }

    @Test
    @WithMockUser("Test")
    public void editUserWithDescriptionMoreThan300() throws Exception {
        String description = RandomStringUtils.random(301);
        Assertions.assertTrue(description.length() > 300);
        checkErrorWhileUpdate(null, description, null);
    }

    public void checkErrorWhileUpdate(String updatedAge, String updatedDescription, String updatedUsername) throws Exception {
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
        userInfo user = userRepo.findUserById("Test");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getName(), "john");
        Assertions.assertEquals(user.getId(), "Test");
        Assertions.assertEquals(user.getCountOfMarks(), 0);
        Assertions.assertEquals(user.getSummaryOfMarks(), 0);
        Assertions.assertEquals(user.getAge(), 30);
        Assertions.assertEquals(user.getDescription(), "spring");
        Assertions.assertEquals(user.getImageUrl(), "/images/image.jpg");

        // Заполняем аттрибуты для обновления информации
        LinkedMultiValueMap<String, String> requestParams1 = new LinkedMultiValueMap<>();
        requestParams1.add("description", updatedDescription == null ? "text" : updatedDescription);
        requestParams1.add("username", updatedUsername == null ? "john" : updatedUsername);
        requestParams1.add("age", updatedAge == null ? "23" : updatedAge);

        // Обновляем информацию о пользователе
        MvcResult res = mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addUserInfo")
                        .file(file)
                        .params(requestParams1))
                .andExpect(status().isOk())
                .andExpect(view().name("addUserInfoPage")).andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        if (updatedAge != null) {
            Assertions.assertEquals("You must be younger 100 and older than 0", document.select("div.text-danger").first().text());
        }
        if ((updatedDescription != null && updatedDescription.isEmpty()) || (updatedUsername != null && updatedUsername.isEmpty())) {
            Assertions.assertEquals("Enter information", document.select("div.text-danger").first().text());
        } else if (updatedUsername != null || updatedDescription != null) {
            Assertions.assertEquals("Description max - 300, username max - 15", document.select("div.text-danger").first().text());
        }

        // Проверяем, что данные пользователя не поменялись в БД
        user = userRepo.findUserById("Test");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getName(), "john");
        Assertions.assertEquals(user.getId(), "Test");
        Assertions.assertEquals(user.getCountOfMarks(), 0);
        Assertions.assertEquals(user.getSummaryOfMarks(), 0);
        Assertions.assertEquals(user.getAge(), 30);
        Assertions.assertEquals(user.getDescription(), "spring");
        Assertions.assertEquals(user.getImageUrl(), "/images/image.jpg");
    }
}
