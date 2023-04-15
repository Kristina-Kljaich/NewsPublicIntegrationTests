package ru.karpov.NewsPublic.IntegrationTest;

import ru.karpov.NewsPublic.models.News;
import ru.karpov.NewsPublic.models.userInfo;
import ru.karpov.NewsPublic.repos.newsRepo;
import ru.karpov.NewsPublic.repos.userRepo;

import java.time.Instant;
import java.util.Date;

public class Utils {
    public static void createNewUser(userRepo userRepo, String id) {
        if (userRepo.findUserById(id) == null) {
            userInfo newUser = new userInfo();
            newUser.setId(id);
            newUser.setAge(34);
            newUser.setName(id);
            newUser.setDescription("Test");
            newUser.setCountOfMarks(0);
            newUser.setSummaryOfMarks(0);
            userRepo.save(newUser);
        }
    }

    public static void createNewNews(userRepo userRepo, newsRepo newsRepo, String name, String category, String userId) {
        News news = new News();
        createNewUser(userRepo, userId);
        news.setName(name);
        news.setCategory(category);
        news.setText("Bla");
        news.setDate(Date.from(Instant.now()));
        news.setAuthorName(userId);
        newsRepo.save(news);
    }
}
