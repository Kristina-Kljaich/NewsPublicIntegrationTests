package ru.karpov.NewsPublic.IntegrationTest;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import ru.karpov.NewsPublic.NewsPublicApplication;
import ru.karpov.NewsPublic.repos.markRepo;
import ru.karpov.NewsPublic.repos.newsRepo;
import ru.karpov.NewsPublic.repos.subscribeRepo;
import ru.karpov.NewsPublic.repos.userRepo;

@SpringBootTest(
        classes = NewsPublicApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BaseTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected userRepo userRepo;

    @Autowired
    protected newsRepo newsRepo;

    @Autowired
    protected subscribeRepo subscribeRepo;

    @Autowired
    protected markRepo markRepo;

    // После каждого теста очищаем БД
    @AfterEach
    public void cleanUserRepo() {
        userRepo.deleteAll();
        newsRepo.deleteAll();
        subscribeRepo.deleteAll();
        markRepo.deleteAll();
    }
}
