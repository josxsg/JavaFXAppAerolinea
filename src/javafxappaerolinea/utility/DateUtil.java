package javafxappaerolinea.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    

    public static Date parseDate(String dateStr) throws ParseException {
        return DATE_FORMAT.parse(dateStr);
    }
    

    public static Date parseDateTime(String dateTimeStr) throws ParseException {
        return DATE_TIME_FORMAT.parse(dateTimeStr);
    }
    

    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatDateTime(Date dateTime) {
        return DATE_TIME_FORMAT.format(dateTime);
    }
    

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
    

    public static double calcularDuracion(Date inicio, Date fin) {
        long diff = fin.getTime() - inicio.getTime();
        return diff / (1000.0 * 60 * 60);
    }
    

    public static Date sumarHoras(Date fecha, double horas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.MINUTE, (int) (horas * 60));
        return calendar.getTime();
    }
}
