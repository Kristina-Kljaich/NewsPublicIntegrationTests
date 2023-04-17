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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class EditUserTest extends BaseTest {
    // Интеграция модуля редактирования профиля и модуля профиля
    // Редактируем профиль и проверяем, что в БД изменились значения, а также профиль отображает измененные поля
    @Test
    @WithMockUser("Test")
    public void editUser() throws Exception {
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
        requestParams1.add("description", "love cat");
        requestParams1.add("username", "jane");
        requestParams1.add("age", "45");

        // Обновляем информацию о пользователе
        mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addUserInfo")
                        .file(file)
                        .params(requestParams1))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));

        // Проверяем, что данные пользователя успешно обновились в БД
        user = userRepo.findUserById("Test");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getName(), "jane");
        Assertions.assertEquals(user.getId(), "Test");
        Assertions.assertEquals(user.getCountOfMarks(), 0);
        Assertions.assertEquals(user.getSummaryOfMarks(), 0);
        Assertions.assertEquals(user.getAge(), 45);
        Assertions.assertEquals(user.getDescription(), "love cat");
        Assertions.assertEquals(user.getImageUrl(), "/images/image.jpg");
        // Проверяем, что изменились значения на профиле пользователя
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().isOk())
                .andExpect(view().name("authProfilePage"))
                .andDo(print())
                .andReturn();

        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("jane", document.select("h1.display-4.fw-normal.text-center").first().text());
        Assertions.assertEquals("love cat", document.select("p.fs-5.text-muted.text-center").first().text());
        Assertions.assertEquals("/images/image.jpg", document.select("img").first().attributes().get("src"));
        Assertions.assertEquals("Age:", document.select("label.fs-5.text-muted.text-center").get(0).text());
        Assertions.assertEquals("45", document.select("label.fs-5.text-muted.text-center").get(1).text());
        Assertions.assertEquals("Mark:", document.select("label.fs-5.text-muted.text-center").get(2).text());
        Assertions.assertEquals("0", document.select("label.fs-5.text-muted.text-center").get(3).text());
    }
}
