package ru.karpov.NewsPublic.IntegrationTest;

import ru.karpov.NewsPublic.models.userInfo;
import ru.karpov.NewsPublic.repos.userRepo;

public class Utils {
    public static void createNewUser(userRepo userRepo, String id) {
        userInfo newUser = new userInfo();
        newUser.setId(id);
        newUser.setAge(34);
        newUser.setName("Test");
        newUser.setDescription("Test");
        newUser.setCountOfMarks(0);
        newUser.setSummaryOfMarks(0);
        userRepo.save(newUser);
    }
}
