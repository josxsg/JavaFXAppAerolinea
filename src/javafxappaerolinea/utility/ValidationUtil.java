package javafxappaerolinea.utility;

import java.time.LocalDate;
import javafxappaerolinea.exception.ValidationException;
import java.util.Date;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    
    public static final String MSG_REQUIRED = "*Campo obligatorio";
    public static final String MSG_POSITIVE = "*Debe ser un número positivo";
    public static final String MSG_NON_NEGATIVE = "*No puede ser negativo";
    public static final String MSG_FUTURE_DATE = "*Debe ser una fecha futura";
    public static final String MSG_PAST_DATE = "*Debe ser una fecha pasada";
    public static final String MSG_INVALID_EMAIL = "*Email inválido";
    public static final String MSG_INVALID_PHONE = "*Teléfono inválido";
    public static final String MSG_INVALID_RANGE = "*Fuera del rango permitido";
    public static final String MSG_INVALID_PASSWORD = "*Contraseña débil";
    public static final String MSG_INVALID_USERNAME = "*Usuario inválido";
    public static final String MSG_INVALID_NUMBER = "*Número inválido";
    

    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("El campo " + fieldName + " no puede estar vacío");
        }
    }
    
 
    public static String validateNotEmptyUI(String value) {
        return (value == null || value.trim().isEmpty()) ? MSG_REQUIRED : null;
    }
    

    public static void validatePositive(Number value, String fieldName) throws ValidationException {
        if (value == null || value.doubleValue() <= 0) {
            throw new ValidationException("El campo " + fieldName + " debe ser un número positivo");
        }
    }
    

    public static String validatePositiveUI(String value) {
        if (value == null || value.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        try {
            double numValue = Double.parseDouble(value);
            return numValue <= 0 ? MSG_POSITIVE : null;
        } catch (NumberFormatException e) {
            return MSG_INVALID_NUMBER;
        }
    }
    
 
    public static void validateNonNegative(Number value, String fieldName) throws ValidationException {
        if (value == null || value.doubleValue() < 0) {
            throw new ValidationException("El campo " + fieldName + " no puede ser negativo");
        }
    }
    
 
    public static String validateNonNegativeUI(String value) {
        if (value == null || value.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        try {
            double numValue = Double.parseDouble(value);
            return numValue < 0 ? MSG_NON_NEGATIVE : null;
        } catch (NumberFormatException e) {
            return MSG_INVALID_NUMBER;
        }
    }
    
  
    public static String validateIntegerUI(String value) {
        if (value == null || value.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        try {
            Integer.parseInt(value);
            return null;
        } catch (NumberFormatException e) {
            return MSG_INVALID_NUMBER;
        }
    }
    
 
    public static String validateDoubleUI(String value) {
        if (value == null || value.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        try {
            Double.parseDouble(value);
            return null;
        } catch (NumberFormatException e) {
            return MSG_INVALID_NUMBER;
        }
    }


    public static String validateDateNotNullUI(LocalDate date) {
        return date == null ? MSG_REQUIRED : null;
    }


    public static String validateAgeRangeUI(LocalDate birthDate, int minAge, int maxAge) {
        if (birthDate == null) {
            return MSG_REQUIRED;
        }

        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(maxAge);
        LocalDate maxDate = now.minusYears(minAge);

        if (birthDate.isBefore(minDate)) {
            return "*Edad máxima permitida: " + maxAge + " años";
        }

        if (birthDate.isAfter(maxDate)) {
            return "*Edad mínima requerida: " + minAge + " años";
        }

        return null;
    }

 
    public static String validatePastDateUI(LocalDate date) {
        if (date == null) {
            return MSG_REQUIRED;
        }

        return date.isAfter(LocalDate.now()) ? MSG_PAST_DATE : null;
    }

 
    public static String validateFutureDateUI(LocalDate date) {
        if (date == null) {
            return MSG_REQUIRED;
        }

        return date.isBefore(LocalDate.now()) ? MSG_FUTURE_DATE : null;
    }
    

    public static void validateEmail(String email, String fieldName) throws ValidationException {
        validateNotEmpty(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("El campo " + fieldName + " debe ser un correo electrónico válido");
        }
    }
    
 
    public static String validateEmailUI(String email) {
        if (email == null || email.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        return EMAIL_PATTERN.matcher(email).matches() ? null : MSG_INVALID_EMAIL;
    }
    

    public static void validatePhone(String phone, String fieldName) throws ValidationException {
        validateNotEmpty(phone, fieldName);
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("El campo " + fieldName + " debe ser un número de teléfono válido");
        }
    }
    

    public static String validatePhoneUI(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        return PHONE_PATTERN.matcher(phone).matches() ? null : MSG_INVALID_PHONE;
    }
    
 
    public static void validateRange(Number value, Number min, Number max, String fieldName) throws ValidationException {
        if (value == null || value.doubleValue() < min.doubleValue() || value.doubleValue() > max.doubleValue()) {
            throw new ValidationException("El campo " + fieldName + " debe estar entre " + min + " y " + max);
        }
    }
    

    public static String validateRangeUI(String value, double min, double max) {
        if (value == null || value.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        try {
            double numValue = Double.parseDouble(value);
            return (numValue < min || numValue > max) ? 
                   "*Debe estar entre " + min + " y " + max : null;
        } catch (NumberFormatException e) {
            return MSG_INVALID_NUMBER;
        }
    }
    

    public static void validatePassword(String password) throws ValidationException {
        validateNotEmpty(password, "contraseña");
        if (password.length() < 8) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("La contraseña debe contener al menos una letra mayúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("La contraseña debe contener al menos una letra minúscula");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new ValidationException("La contraseña debe contener al menos un número");
        }
    }
    
 
    public static String validatePasswordUI(String password) {
        if (password == null || password.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        if (password.length() < 8) {
            return "*Mínimo 8 caracteres";
        }
        
        if (!password.matches(".*[A-Z].*") || 
            !password.matches(".*[a-z].*") || 
            !password.matches(".*[0-9].*")) {
            return "*Debe incluir mayúsculas, minúsculas y números";
        }
        
        return null;
    }
    
 
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    

    public static String validateUsernameUI(String username) {
        if (username == null || username.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        return isValidUsername(username) ? null : "*Debe tener 4-20 caracteres (letras, números, _)";
    }
    
 
    public static boolean isValidPassword(String password) {
        return password != null && 
               password.length() >= 8 && 
               password.matches(".*[A-Z].*") && 
               password.matches(".*[a-z].*") && 
               password.matches(".*[0-9].*");
    }
}