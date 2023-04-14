package ru.karpov.NewsPublic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(
        classes = NewsPublicApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc

class KeycloakUserTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mvc;

    @WithMockUser("spring")
    @Test
    public void authWithCorrectUser() throws Exception {
        MvcResult res =mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString(), "http://localhost:" + port + "/");
        Elements els = document.select("a[href=/logout].me-3.py-2.text-dark.text-decoration-none");
        Assertions.assertEquals(1, els.size());
        els = els.get(0).getElementsContainingText("Logout");
        Assertions.assertEquals(1, els.size());
        els = document.select("a[href=/authProfilePage].me-3.py-2.text-dark.text-decoration-none");
        Assertions.assertEquals(1, els.size());
        els = els.get(0).getElementsContainingText("Profile");
        Assertions.assertEquals(1, els.size());
    }

    @Test
    public void authWithAnonymousUser() throws Exception {
        MvcResult res =mvc.perform(get("http://localhost:" + port + "/"))
                .andExpect(status().isOk())
                .andExpect(view().name("mainPage"))
                .andReturn();
        Document document = Jsoup.parse(res.getResponse().getContentAsString(), "http://localhost:" + port + "/");
        Elements els = document.select("a[href=/login].me-3.py-2.text-dark.text-decoration-none");
        Assertions.assertEquals(0, els.size());
        els = document.select("a[href=/authProfilePage].me-3.py-2.text-dark.text-decoration-none");
        Assertions.assertEquals(1, els.size());
        els = els.get(0).getElementsContainingText("LogIn");
        Assertions.assertEquals(1, els.size());
    }
}

