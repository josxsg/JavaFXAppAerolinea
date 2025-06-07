/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.utility;

import javafxappaerolinea.exception.ValidationException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 * @author Dell
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    
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
     * Valida que una fecha sea futura
     * @param date Fecha a validar
     * @param fieldName Nombre del campo
     * @throws ValidationException Si la fecha no es futura
     */
    public static void validateFutureDate(Date date, String fieldName) throws ValidationException {
        if (date == null || date.before(new Date())) {
            throw new ValidationException("El campo " + fieldName + " debe ser una fecha futura");
        }
    }
    
    /**
     * Valida que una fecha sea pasada
     * @param date Fecha a validar
     * @param fieldName Nombre del campo
     * @throws ValidationException Si la fecha no es pasada
     */
    public static void validatePastDate(Date date, String fieldName) throws ValidationException {
        if (date == null || date.after(new Date())) {
            throw new ValidationException("El campo " + fieldName + " debe ser una fecha pasada");
        }
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
}
