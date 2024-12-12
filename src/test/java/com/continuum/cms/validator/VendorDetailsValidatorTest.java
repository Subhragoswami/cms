package com.continuum.cms.validator;

import com.continuum.cms.util.VendorDetailsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class VendorDetailsValidatorTest {

    @Mock

    @InjectMocks
    private VendorDetailsValidator vendorDetailsValidator;
    private VendorDetailsRequest vendorDetailsRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void testVendorValidationPhoneNumberWithoutTenDigit() {
//        vendorDetailsRequest = new VendorDetailsRequest();
//        VendorRequest vendorRequest = vendorDetailsRequest.createVendorRequest();
//        vendorRequest.getContactDetails().setPhoneNumber("123456789L");
//        ValidationException exception = assertThrows(ValidationException.class, () -> vendorDetailsValidator.requestValidation(vendorRequest));
//        List<ErrorDto> errorMessages = exception.getErrorMessages();
//        assertEquals(1, errorMessages.size());
//    }
//
//    @Test
//    void testVendorValidationPhoneNumberWithTenDigit() {
//        vendorDetailsRequest = new VendorDetailsRequest();
//        VendorRequest vendorRequest = vendorDetailsRequest.createVendorRequest();
//        vendorRequest.getContactDetails().setPhoneNumber("1234567891L");
//        assertDoesNotThrow( () -> vendorDetailsValidator.requestValidation(vendorRequest));
//    }
//
//    @Test
//    void testVendorValidationWrongEmailFormat() {
//        vendorDetailsRequest = new VendorDetailsRequest();
//        VendorRequest vendorRequest = vendorDetailsRequest.createVendorRequest();
//        vendorRequest.getContactDetails().setEmail("test.gmail.com");
//        ValidationException exception = assertThrows(ValidationException.class, () -> vendorDetailsValidator.requestValidation(vendorRequest));
//        List<ErrorDto> errorMessages = exception.getErrorMessages();
//        assertEquals(1, errorMessages.size());
//    }
//
//    @Test
//    void testVendorValidationWithEmail() {
//        vendorDetailsRequest = new VendorDetailsRequest();
//        VendorRequest vendorRequest = vendorDetailsRequest.createVendorRequest();
//        vendorRequest.getContactDetails().setEmail("test@gmail.com");
//        assertDoesNotThrow( () -> vendorDetailsValidator.requestValidation(vendorRequest));
//    }
}
