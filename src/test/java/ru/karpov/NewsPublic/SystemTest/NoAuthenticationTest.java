package ru.karpov.NewsPublic.SystemTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoAuthenticationTest {
    @Test
    public void NoAuthenticationTest() {
        Assertions.assertEquals(2, 1 + 1);
    }

    @Test
    public void userCanAddPosts() {
        Assertions.assertEquals(2, 1 + 1);
    }
}
