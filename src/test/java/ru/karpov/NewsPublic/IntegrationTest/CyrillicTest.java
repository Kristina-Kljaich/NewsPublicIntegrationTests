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
import ru.karpov.NewsPublic.models.Categories;
import ru.karpov.NewsPublic.models.News;
import ru.karpov.NewsPublic.models.userInfo;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CyrillicTest extends BaseTest {
    @Test
    @WithMockUser("Артем")
    public void cyrillicPost() throws Exception {
        Utils.createNewUser(userRepo, "Артем");
        // Переходим на страницу добавления поста
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/addNewsPage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addNewsPage"))
                .andReturn();
        // Проверяем страницу поста на валидность
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("/addNews", document.select("form.needs-validation").get(0).attr("action"));
        Assertions.assertEquals("Title", document.select("label.form-label").get(0).text());
        Assertions.assertEquals("Text", document.select("label.form-label").get(1).text());
        Assertions.assertEquals("Category", document.select("label.form-label").get(2).text());
        Assertions.assertEquals(1, document.select("input#photo").size());
        // Проверяем, что раньше такой статьи не было в БД
        Assertions.assertEquals(0, newsRepo.findAll().size());

        // Создаем Mock файл картинки
        final byte[] random = RandomStringUtils.random(5).getBytes();
        final MockMultipartFile file = new MockMultipartFile("photo", "image.jpg", null, random);

        // Заполняем прочие аттрибуты
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("Title", "Тест1");
        requestParams.add("text", "что-то на русском");
        requestParams.add("category", "Sport");

        // Добавляем статью
        mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addNews")
                        .file(file)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));
        List<News> news = newsRepo.findAll();
        Assertions.assertEquals(1, news.size());
        Assertions.assertEquals("Тест1", news.get(0).getName());
        Assertions.assertEquals("что-то на русском", news.get(0).getText());
        Assertions.assertEquals("Артем", news.get(0).getAuthorName());
        Assertions.assertEquals(Categories.Sport, news.get(0).getCategory());
    }

    @Test
    @WithMockUser("Артем")
    public void cyrillicProfile() throws Exception {
        // Заходим в профиль пользователя, который не добавил о себе информацию, проверяем, что перед нами страница addUserInfoPage
        MvcResult res = mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().isOk())
                .andExpect(view().name("addUserInfoPage"))
                .andReturn();

        // Проверяем наполнение страницы добавления информации
        Document document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Username", document.select("label.form-label").get(0).text());
        Assertions.assertEquals("Age", document.select("label.form-label").get(1).text());
        Assertions.assertEquals("Description", document.select("label.form-label").get(2).text());
        Assertions.assertEquals("Photo", document.select("label.form-label").get(3).text());
        Assertions.assertEquals("Add info", document.select("button").text());

        // Создаем Mock файл картинки
        final byte[] random = RandomStringUtils.random(5).getBytes();
        final MockMultipartFile file = new MockMultipartFile("image", "image.jpg", null, random);

        // Заполняем прочие аттрибуты
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("description", "что-то на русском");
        requestParams.add("username", "Артем");
        requestParams.add("age", "30");

        // Отправляем запрос на добавление информации о пользователе и проверяем, что нас переправит на главную страницу
        mvc.perform(MockMvcRequestBuilders.fileUpload("http://localhost:" + port + "/addUserInfo")
                        .file(file)
                        .params(requestParams))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"));

        // Проверяем, что пользователь успешно добавлен
        userInfo user = userRepo.findUserById("Артем");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(user.getName(), "Артем");
        Assertions.assertEquals(user.getId(), "Артем");
        Assertions.assertEquals(user.getCountOfMarks(), 0);
        Assertions.assertEquals(user.getSummaryOfMarks(), 0);
        Assertions.assertEquals(user.getAge(), 30);
        Assertions.assertEquals(user.getDescription(), "что-то на русском");
        Assertions.assertEquals(user.getImageUrl(), "/images/image.jpg");

        // Проверяем, что изменились значения на профиле пользователя
        res = mvc.perform(get("http://localhost:" + port + "/authProfilePage"))
                .andExpect(status().isOk())
                .andExpect(view().name("authProfilePage"))
                .andReturn();

        document = Jsoup.parse(res.getResponse().getContentAsString());
        Assertions.assertEquals("Артем", document.select("h1.display-4.fw-normal.text-center").first().text());
        Assertions.assertEquals("что-то на русском", document.select("p.fs-5.text-muted.text-center").first().text());
        Assertions.assertEquals("/images/image.jpg", document.select("img").first().attributes().get("src"));
        Assertions.assertEquals("Age:", document.select("label.fs-5.text-muted.text-center").get(0).text());
        Assertions.assertEquals("30", document.select("label.fs-5.text-muted.text-center").get(1).text());
        Assertions.assertEquals("Mark:", document.select("label.fs-5.text-muted.text-center").get(2).text());
        Assertions.assertEquals("0", document.select("label.fs-5.text-muted.text-center").get(3).text());
    }
}
