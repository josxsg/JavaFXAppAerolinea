/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javafxappaerolinea.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Dell
 */
public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    /**
     * Convierte una cadena a una fecha
     * @param dateStr Cadena con formato yyyy-MM-dd
     * @return Fecha
     * @throws ParseException Si la cadena no tiene el formato correcto
     */
    public static Date parseDate(String dateStr) throws ParseException {
        return DATE_FORMAT.parse(dateStr);
    }
    
    /**
     * Convierte una cadena a una fecha y hora
     * @param dateTimeStr Cadena con formato yyyy-MM-dd HH:mm
     * @return Fecha y hora
     * @throws ParseException Si la cadena no tiene el formato correcto
     */
    public static Date parseDateTime(String dateTimeStr) throws ParseException {
        return DATE_TIME_FORMAT.parse(dateTimeStr);
    }
    
    /**
     * Convierte una fecha a una cadena
     * @param date Fecha
     * @return Cadena con formato yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Convierte una fecha y hora a una cadena
     * @param dateTime Fecha y hora
     * @return Cadena con formato yyyy-MM-dd HH:mm
     */
    public static String formatDateTime(Date dateTime) {
        return DATE_TIME_FORMAT.format(dateTime);
    }
    
    /**
     * Calcula la edad a partir de una fecha de nacimiento
     * @param fechaNacimiento Fecha de nacimiento
     * @return Edad en años
     */
    public static int calcularEdad(Date fechaNacimiento) {
        Calendar fechaNac = Calendar.getInstance();
        fechaNac.setTime(fechaNacimiento);
        Calendar fechaActual = Calendar.getInstance();
        
        int edad = fechaActual.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        if (fechaActual.get(Calendar.DAY_OF_YEAR) < fechaNac.get(Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        
        return edad;
    }
    
    /**
     * Calcula la duración entre dos fechas en horas
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return Duración en horas
     */
    public static double calcularDuracion(Date inicio, Date fin) {
        long diff = fin.getTime() - inicio.getTime();
        return diff / (1000.0 * 60 * 60);
    }
    
    /**
     * Suma horas a una fecha
     * @param fecha Fecha base
     * @param horas Horas a sumar
     * @return Nueva fecha
     */
    public static Date sumarHoras(Date fecha, double horas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.MINUTE, (int) (horas * 60));
        return calendar.getTime();
    }
}
