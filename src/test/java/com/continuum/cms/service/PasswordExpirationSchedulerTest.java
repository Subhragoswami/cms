package com.continuum.cms.service;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.util.DateUtil;
import com.continuum.cms.util.UserBuilder;
import com.continuum.cms.util.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

public class PasswordExpirationSchedulerTest {
    @Mock
    private UserDao userDao;

    @Mock
    private DateUtil dateUtil;

    @InjectMocks
    private PasswordExpirationScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(dateUtil.getPasswordExpiryTime()).thenReturn(new Date());
    }
     User user = UserBuilder.buildDefaultUser("userName", "Password", Status.INACTIVE.getName());
    @Test
    void processExpiredUserTest() {
        List<User> expiredUsers = new ArrayList<>();
        User user1 = new User();
        user1.setUserName("userName");
        user1.setPassword("password");
        user1.setLastPasswordUpdated(new Date());
        User user2 = new User();
        user2.setUserName("userName1");
        user2.setPassword("password1");
        user2.setLastPasswordUpdated(new Date());
        expiredUsers.add(user1);
        expiredUsers.add(user2);
        Mockito.doNothing().when(userDao).updateUserStatusInBatch(any());
        scheduler.processExpiredUsers(expiredUsers);
        Mockito.verify(userDao, Mockito.times(1)).updateUserStatusInBatch(expiredUsers);
    }

    @Test
    public void testCheckPasswordExpiryNoExpiredUsers() {
        when(userDao.getExpiredUser(any())).thenReturn(new ArrayList<>());
        scheduler.checkPasswordExpiry();
        verify(userDao).getExpiredUser(any());
    }

    @Test
    public void testCheckPasswordExpiryExpiredUsers() {

        List<User> expiredUsers = new ArrayList<>();
        user.setLastPasswordUpdated(new Date());
        expiredUsers.add(user);

        when(userDao.getExpiredUser(any())).thenReturn(expiredUsers);

        scheduler.checkPasswordExpiry();

        verify(userDao).getExpiredUser(any());
        verify(userDao).updateUserStatusInBatch(expiredUsers);
    }

    @Test
    public void testProcessExpiredUserSuccess() {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user);

        doNothing().when(userDao).updateUserStatusInBatch(users);
        scheduler.processExpiredUsers(users);
        verify(userDao).updateUserStatusInBatch(users);
    }

}
