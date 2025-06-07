package javafxappaerolinea.utility;

import java.time.LocalDate;
import javafxappaerolinea.exception.ValidationException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utilidad para validación de datos
 * @author Dell
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    
    // Mensajes de error genéricos
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
    
    /**
     * Valida que un campo no sea nulo o vacío
     * @param value Valor a validar
     * @param fieldName Nombre del campo
     * @throws ValidationException Si el campo es nulo o vacío
     */
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("El campo " + fieldName + " no puede estar vacío");
        }
    }
    
    /**
     * Valida que un campo no sea nulo o vacío (para UI)
     * @param value Valor a validar
     * @return null si es válido, mensaje de error si no lo es
     */
    public static String validateNotEmptyUI(String value) {
        return (value == null || value.trim().isEmpty()) ? MSG_REQUIRED : null;
    }
    
    /**
     * Valida que un campo numérico sea positivo
     * @param value Valor a validar
     * @param fieldName Nombre del campo
     * @throws ValidationException Si el campo no es positivo
     */
    public static void validatePositive(Number value, String fieldName) throws ValidationException {
        if (value == null || value.doubleValue() <= 0) {
            throw new ValidationException("El campo " + fieldName + " debe ser un número positivo");
        }
    }
    
    /**
     * Valida que un campo numérico sea positivo (para UI)
     * @param value Valor a validar
     * @return null si es válido, mensaje de error si no lo es
     */
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
    
    /**
     * Valida que un campo numérico sea no negativo
     * @param value Valor a validar
     * @param fieldName Nombre del campo
     * @throws ValidationException Si el campo es negativo
     */
    public static void validateNonNegative(Number value, String fieldName) throws ValidationException {
        if (value == null || value.doubleValue() < 0) {
            throw new ValidationException("El campo " + fieldName + " no puede ser negativo");
        }
    }
    
    /**
     * Valida que un campo numérico sea no negativo (para UI)
     * @param value Valor a validar
     * @return null si es válido, mensaje de error si no lo es
     */
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
    
    /**
     * Valida que un campo numérico entero sea válido
     * @param value Valor a validar
     * @return null si es válido, mensaje de error si no lo es
     */
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
    
    /**
     * Valida que un campo numérico decimal sea válido
     * @param value Valor a validar
     * @return null si es válido, mensaje de error si no lo es
     */
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

    /**
     * Valida que una fecha no sea nula (para UI)
     * @param date Fecha a validar
     * @return null si es válida, mensaje de error si no lo es
     */
    public static String validateDateNotNullUI(LocalDate date) {
        return date == null ? MSG_REQUIRED : null;
    }

    /**
     * Valida que una fecha esté dentro de un rango de edad (para UI)
     * @param birthDate Fecha de nacimiento a validar
     * @param minAge Edad mínima
     * @param maxAge Edad máxima
     * @return null si es válida, mensaje de error si no lo es
     */
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

    /**
     * Valida que una fecha sea pasada (para UI)
     * @param date Fecha a validar
     * @return null si es válida, mensaje de error si no lo es
     */
    public static String validatePastDateUI(LocalDate date) {
        if (date == null) {
            return MSG_REQUIRED;
        }

        return date.isAfter(LocalDate.now()) ? MSG_PAST_DATE : null;
    }

    /**
     * Valida que una fecha sea futura (para UI)
     * @param date Fecha a validar
     * @return null si es válida, mensaje de error si no lo es
     */
    public static String validateFutureDateUI(LocalDate date) {
        if (date == null) {
            return MSG_REQUIRED;
        }

        return date.isBefore(LocalDate.now()) ? MSG_FUTURE_DATE : null;
    }
    
    /**
     * Valida que un correo electrónico tenga un formato válido
     * @param email Correo electrónico a validar
     * @param fieldName Nombre del campo
     * @throws ValidationException Si el correo electrónico no tiene un formato válido
     */
    public static void validateEmail(String email, String fieldName) throws ValidationException {
        validateNotEmpty(email, fieldName);
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("El campo " + fieldName + " debe ser un correo electrónico válido");
        }
    }
    
    /**
     * Valida que un correo electrónico tenga un formato válido (para UI)
     * @param email Correo electrónico a validar
     * @return null si es válido, mensaje de error si no lo es
     */
    public static String validateEmailUI(String email) {
        if (email == null || email.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        return EMAIL_PATTERN.matcher(email).matches() ? null : MSG_INVALID_EMAIL;
    }
    
    /**
     * Valida que un número de teléfono tenga un formato válido
     * @param phone Número de teléfono a validar
     * @param fieldName Nombre del campo
     * @throws ValidationException Si el número de teléfono no tiene un formato válido
     */
    public static void validatePhone(String phone, String fieldName) throws ValidationException {
        validateNotEmpty(phone, fieldName);
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("El campo " + fieldName + " debe ser un número de teléfono válido");
        }
    }
    
    /**
     * Valida que un número de teléfono tenga un formato válido (para UI)
     * @param phone Número de teléfono a validar
     * @return null si es válido, mensaje de error si no lo es
     */
    public static String validatePhoneUI(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        return PHONE_PATTERN.matcher(phone).matches() ? null : MSG_INVALID_PHONE;
    }
    
    /**
     * Valida que un valor esté dentro de un rango
     * @param value Valor a validar
     * @param min Valor mínimo
     * @param max Valor máximo
     * @param fieldName Nombre del campo
     * @throws ValidationException Si el valor no está dentro del rango
     */
    public static void validateRange(Number value, Number min, Number max, String fieldName) throws ValidationException {
        if (value == null || value.doubleValue() < min.doubleValue() || value.doubleValue() > max.doubleValue()) {
            throw new ValidationException("El campo " + fieldName + " debe estar entre " + min + " y " + max);
        }
    }
    
    /**
     * Valida que un valor esté dentro de un rango (para UI)
     * @param value Valor a validar
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return null si es válido, mensaje de error si no lo es
     */
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
    
    /**
     * Valida que una contraseña sea segura
     * @param password Contraseña a validar
     * @throws ValidationException Si la contraseña no es segura
     */
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
    
    /**
     * Valida que una contraseña sea segura (para UI)
     * @param password Contraseña a validar
     * @return null si es válida, mensaje de error si no lo es
     */
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
    
    /**
     * Valida que un nombre de usuario sea válido
     * @param username Nombre de usuario a validar
     * @return true si es válido, false si no lo es
     */
    public static boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Valida que un nombre de usuario sea válido (para UI)
     * @param username Nombre de usuario a validar
     * @return null si es válido, mensaje de error si no lo es
     */
    public static String validateUsernameUI(String username) {
        if (username == null || username.trim().isEmpty()) {
            return MSG_REQUIRED;
        }
        
        return isValidUsername(username) ? null : "*Debe tener 4-20 caracteres (letras, números, _)";
    }
    
    /**
     * Valida que una contraseña sea segura
     * @param password Contraseña a validar
     * @return true si es válida, false si no lo es
     */
    public static boolean isValidPassword(String password) {
        return password != null && 
               password.length() >= 8 && 
               password.matches(".*[A-Z].*") && 
               password.matches(".*[a-z].*") && 
               password.matches(".*[0-9].*");
    }
}