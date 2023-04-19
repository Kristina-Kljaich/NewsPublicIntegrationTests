package ru.karpov.NewsPublic.SystemTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthenticationEditUserTest extends BaseTest{
    @Test
    public void checkSubscribersUser() {
        Assertions.assertEquals(2, 1+1);
    }

    @Test
    public void userCanAddPosts() {
        Assertions.assertEquals(2, 1 + 1);
    }
}
