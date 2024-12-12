package com.continuum.cms.validator;


import com.continuum.cms.dao.VendorUserDao;
import com.continuum.cms.model.request.UserPostRequest;
import com.continuum.cms.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
public class UserValidatorTest {

    @Mock
    private VendorUserDao vendorUserDao;

    @Mock
    private ValidationUtil validationUtil;

    @InjectMocks
    private UserValidator userValidator;

    private UserPostRequest userPostRequest;

    @BeforeEach
    public void setUp() {
        userPostRequest = UserPostRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .build();
    }

      /*
    @Test
    void testValidateCreateUserRequestSuccess() {
        userPostRequest.setFirstName("");

        assertThrows(ValidationException.class, () -> {
            userValidator.validateCreateUserRequest(userPostRequest);
        });

    }

 @Test
    void testValidateCreateUserRequestFailure() {
        when(validationUtil.validateNotEmpty(userPostRequest.getFirstName(), "First Name")).thenReturn(false);

        assertThrows(ValidationException.class, () -> {
            userValidator.validateCreateUserRequest(userPostRequest);
        });
    }

    @Test
    void testValidateVendorAdminCreationRequestSuccess() {
        Vendor vendor = Vendor.builder().id("123").build();
        when(vendorUserDao.getUsersByVendorId("123")).thenReturn(Collections.emptyList());

        userValidator.validateVendorAdminCreationRequest(vendor);
    }

    @Test
    void testValidateVendorAdminCreationRequest_MaxLimitReached() {
        Vendor vendor = Vendor.builder().id("123").build();
        when(vendorUserDao.getUsersByVendorId("123")).thenReturn(Arrays.asList(new User(), new User()));

        assertThrows(ValidationException.class, () -> {
            userValidator.validateVendorAdminCreationRequest(vendor);
        });
    }

    @Test
    void testValidateVendorAdminCreationRequest_InactiveVendor() {
        Vendor vendor = Vendor.builder().id("123").status(Status.INACTIVE).build();

        assertThrows(ValidationException.class, () -> {
            userValidator.validateVendorAdminCreationRequest(vendor);
        });
    }

    @Test
    void testValidateVendorAdminCreationRequest_BlockedVendor() {
        Vendor vendor = Vendor.builder().id("123").status(Status.BLOCKED).build();

        assertThrows(ValidationException.class, () -> {
            userValidator.validateVendorAdminCreationRequest(vendor);
        });
    }*/
}
