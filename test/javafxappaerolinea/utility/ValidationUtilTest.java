package javafxappaerolinea.utility;

import java.time.LocalDate;
import javafxappaerolinea.exception.ValidationException;
import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationUtilTest {

    public ValidationUtilTest() {
    }

    @Test
    public void testValidateNotEmptyUI_Valid() {
        assertNull(ValidationUtil.validateNotEmptyUI("test"));
    }

    @Test
    public void testValidateNotEmptyUI_Null() {
        assertEquals(ValidationUtil.MSG_REQUIRED, ValidationUtil.validateNotEmptyUI(null));
    }

    @Test
    public void testValidatePositiveUI_Valid() {
        assertNull(ValidationUtil.validatePositiveUI("10"));
    }

    @Test
    public void testValidatePositiveUI_ZeroOrNegative() {
        assertEquals(ValidationUtil.MSG_POSITIVE, ValidationUtil.validatePositiveUI("0"));
    }

    @Test
    public void testValidateNonNegativeUI_Negative() {
        assertEquals(ValidationUtil.MSG_NON_NEGATIVE, ValidationUtil.validateNonNegativeUI("-1"));
    }

    @Test
    public void testValidateIntegerUI_Invalid() {
        assertEquals(ValidationUtil.MSG_INVALID_NUMBER, ValidationUtil.validateIntegerUI("12.3"));
    }

    @Test
    public void testValidateDateNotNullUI_Null() {
        assertEquals(ValidationUtil.MSG_REQUIRED, ValidationUtil.validateDateNotNullUI(null));
    }

    @Test
    public void testValidateAgeRangeUI_Valid() {
        LocalDate birthDate = LocalDate.now().minusYears(30);
        assertNull(ValidationUtil.validateAgeRangeUI(birthDate, 18, 65));
    }

    @Test
    public void testValidateAgeRangeUI_TooYoung() {
        LocalDate birthDate = LocalDate.now().minusYears(10);
        assertEquals("*Edad mínima requerida: 18 años", ValidationUtil.validateAgeRangeUI(birthDate, 18, 65));
    }

    @Test
    public void testValidatePastDateUI_Future() {
        assertEquals(ValidationUtil.MSG_PAST_DATE, ValidationUtil.validatePastDateUI(LocalDate.now().plusDays(1)));
    }

    @Test
    public void testValidateEmailUI_Valid() {
        assertNull(ValidationUtil.validateEmailUI("test@example.com"));
    }

    @Test
    public void testValidateEmailUI_InvalidFormat() {
        assertEquals(ValidationUtil.MSG_INVALID_EMAIL, ValidationUtil.validateEmailUI("invalid-email"));
    }

    @Test
    public void testValidatePhoneUI_InvalidFormat() {
        assertEquals(ValidationUtil.MSG_INVALID_PHONE, ValidationUtil.validatePhoneUI("abc"));
    }

    @Test
    public void testValidatePasswordUI_Valid() {
        assertNull(ValidationUtil.validatePasswordUI("Password123"));
    }

    @Test
    public void testValidatePasswordUI_TooShort() {
        assertEquals("*Mínimo 8 caracteres", ValidationUtil.validatePasswordUI("Pass123"));
    }

    @Test
    public void testValidateUsernameUI_Valid() {
        assertNull(ValidationUtil.validateUsernameUI("username123"));
    }

    @Test
    public void testValidateUsernameUI_Invalid() {
        assertEquals("*Debe tener 4-20 caracteres (letras, números, _)", ValidationUtil.validateUsernameUI("abc"));
    }
}