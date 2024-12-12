package com.continuum.cms.service;

import com.continuum.cms.dao.FileDao;
import com.continuum.cms.dao.UserDao;
import com.continuum.cms.dao.VendorUserDao;
import com.continuum.cms.entity.User;
import com.continuum.cms.exceptions.CMSException;
import com.continuum.cms.model.request.UserDetailsUpdateRequest;
import com.continuum.cms.model.response.ResponseDto;
import com.continuum.cms.model.response.UserDetailsResponse;
import com.continuum.cms.repository.FilesRepository;
import com.continuum.cms.repository.UserRepository;
import com.continuum.cms.repository.VendorUserRepository;
import com.continuum.cms.util.UserBuilder;
import com.continuum.cms.util.enums.Status;
import com.continuum.cms.validator.FileUploadValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class UserServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private FileDao fileDao;
    @Mock
    private VendorUserDao vendorUserDao;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private FileUploadValidator fileUploadValidator;
    @Mock
    private FilesRepository filesRepository;
    @Mock
    private VendorUserRepository vendorUserRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;
    @InjectMocks
    private UserService userService;
    private UserDetailsUpdateRequest userDetailsUpdateRequest;
    private final User user = UserBuilder.buildDefaultUser("userName", "password", Status.INACTIVE.getName());

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetUserByUserName() {

        Mockito.when(userDao.getByUserName(Mockito.anyString())).thenReturn(Optional.of(user));

        Optional<User> userOptional = userService.getUserByUserName(user.getUsername());

        assert (userOptional.isPresent());
        assertEquals("userName", userOptional.get().getUsername());
        assertNotEquals("user", userOptional.get().getUsername());

        Mockito.verify(userDao, Mockito.times(1)).getByUserName(Mockito.anyString());

    }

    @Test
    void testGetUserDetails_UserNotFound() {
        String userName = "non_existing_user";
        when(userDao.getUserDetails(userName)).thenReturn(Optional.empty());

        assertThrows(CMSException.class, () -> userService.getUserDetails(userName));
        verify(userDao, times(1)).getUserDetails(userName);
    }

    @Test
    void testGetUserDetails() {
        String userName = "testUser";
        User user = new User();
        user.setUserName(userName);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        when(userDao.getUserDetails(userName)).thenReturn(Optional.of(user));
        when(mapper.convertValue(any(), eq(UserDetailsResponse.class))).thenReturn(userDetailsResponse);
        ResponseDto<UserDetailsResponse> responseDto = userService.getUserDetails(userName);
        assertEquals(0, responseDto.getStatus());
        assertEquals(1, responseDto.getData().size());
    }

    @Test
    void testGetUserDetails_UserNotExist() {
        String userName = "nonExistentUser";
        when(userDao.getUserDetails(userName)).thenReturn(Optional.empty());
        assertThrows(CMSException.class, () -> userService.getUserDetails(userName));
        verify(userDao, times(1)).getUserDetails(userName);
        verify(vendorUserDao, never()).getUserByUserId(any(UUID.class));
    }
}


