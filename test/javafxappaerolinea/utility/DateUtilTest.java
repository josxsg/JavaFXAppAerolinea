package javafxappaerolinea.utility;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore; // Importa la anotación @Ignore

public class DateUtilTest {

    public DateUtilTest() {
    }

    @Test
    public void testParseDate_ValidFormat() throws ParseException {
        String dateStr = "2023-10-26";
        Date result = DateUtil.parseDate(dateStr);
        assertNotNull(result);

        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(Calendar.OCTOBER, cal.get(Calendar.MONTH));
        assertEquals(26, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));
    }

    @Test(expected = ParseException.class)
    public void testParseDate_InvalidFormat() throws ParseException {
        String dateStr = "26/10/2023";
        DateUtil.parseDate(dateStr);
    }

    @Test(expected = NullPointerException.class)
    public void testParseDate_NullString() throws ParseException {
        String dateStr = null;
        DateUtil.parseDate(dateStr);
    }

    @Test
    public void testParseDateTime_ValidFormat() throws ParseException {
        String dateTimeStr = "2023-10-26 15:30";
        Date result = DateUtil.parseDateTime(dateTimeStr);
        assertNotNull(result);

        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(Calendar.OCTOBER, cal.get(Calendar.MONTH));
        assertEquals(26, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(15, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, cal.get(Calendar.MINUTE));
    }
    
    @Test(expected = NullPointerException.class)
    public void testParseDateTime_NullString() throws ParseException {
        String dateTimeStr = null;
        DateUtil.parseDateTime(dateTimeStr);
    }

    @Test
    public void testFormatDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.NOVEMBER, 5, 10, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date date = cal.getTime();
        String expResult = "2023-11-05";
        String result = DateUtil.formatDate(date);
        assertEquals(expResult, result);
    }

    @Test
    public void testFormatDate_NullDate() {
        Date date = null;
        try {
            DateUtil.formatDate(date);
            fail("Se esperaba NullPointerException para fecha nula");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testFormatDateTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.FEBRUARY, 29, 23, 59, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date dateTime = cal.getTime();
        String expResult = "2024-02-29 23:59";
        String result = DateUtil.formatDateTime(dateTime);
        assertEquals(expResult, result);
    }

    @Test
    public void testFormatDateTime_NullDate() {
        Date dateTime = null;
        try {
            DateUtil.formatDateTime(dateTime);
            fail("Se esperaba NullPointerException para fecha y hora nula");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testCalcularEdad_TodayBirthday() throws ParseException {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.set(currentYear - 30, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Date fechaNacimiento = cal.getTime();

        int expResult = 30;
        int result = DateUtil.calcularEdad(fechaNacimiento);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCalcularEdad_PastBirthday() throws ParseException {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.set(currentYear - 30, Calendar.JANUARY, 1);
        Date fechaNacimiento = cal.getTime();

        int expResult = 30;
        int result = DateUtil.calcularEdad(fechaNacimiento);
        assertEquals(expResult, result);
    }

    @Test
    public void testCalcularEdad_FutureBirthday() throws ParseException {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        cal.set(currentYear - 30, Calendar.DECEMBER, 31);
        Date fechaNacimiento = cal.getTime();

        int expResult = 29;
        int result = DateUtil.calcularEdad(fechaNacimiento);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCalcularEdad_ZeroAge() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        Date fechaNacimiento = cal.getTime();
        
        int expResult = 0;
        int result = DateUtil.calcularEdad(fechaNacimiento);
        assertEquals(expResult, result);
    }

    @Test
    public void testCalcularEdad_EdgeCaseLeapYear() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.set(2000, Calendar.FEBRUARY, 29);
        Date fechaNacimiento = cal.getTime();

        Calendar currentCal = Calendar.getInstance();
        int expectedAge = currentCal.get(Calendar.YEAR) - 2000;
        if (currentCal.get(Calendar.DAY_OF_YEAR) < cal.get(Calendar.DAY_OF_YEAR)) {
            expectedAge--;
        }
        
        int result = DateUtil.calcularEdad(fechaNacimiento);
        assertEquals(expectedAge, result);
    }

    @Test
    public void testCalcularDuracion_Positive() throws ParseException {
        Date inicio = DateUtil.parseDateTime("2023-01-01 10:00");
        Date fin = DateUtil.parseDateTime("2023-01-01 12:30");

        double expResult = 2.5;
        double result = DateUtil.calcularDuracion(inicio, fin);
        assertEquals(expResult, result, 0.001);
    }

    @Test
    public void testCalcularDuracion_Zero() throws ParseException {
        Date inicio = DateUtil.parseDateTime("2023-01-01 10:00");
        Date fin = DateUtil.parseDateTime("2023-01-01 10:00");

        double expResult = 0.0;
        double result = DateUtil.calcularDuracion(inicio, fin);
        assertEquals(expResult, result, 0.001);
    }

    @Test
    public void testCalcularDuracion_Negative() throws ParseException {
        Date inicio = DateUtil.parseDateTime("2023-01-01 12:00");
        Date fin = DateUtil.parseDateTime("2023-01-01 10:00");

        double expResult = -2.0;
        double result = DateUtil.calcularDuracion(inicio, fin);
        assertEquals(expResult, result, 0.001);
    }
    
    @Test
    public void testCalcularDuracion_AcrossMidnight() throws ParseException {
        Date inicio = DateUtil.parseDateTime("2023-01-01 22:00");
        Date fin = DateUtil.parseDateTime("2023-01-02 02:00");

        double expResult = 4.0;
        double result = DateUtil.calcularDuracion(inicio, fin);
        assertEquals(expResult, result, 0.001);
    }

    @Test
    public void testSumarHoras_PositiveHours() throws ParseException {
        Date fecha = DateUtil.parseDateTime("2023-01-01 10:00");
        double horas = 3.5;

        Date result = DateUtil.sumarHoras(fecha, horas);
        assertNotNull(result);

        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, cal.get(Calendar.MINUTE));
    }

    @Test
    public void testSumarHoras_NegativeHours() throws ParseException {
        Date fecha = DateUtil.parseDateTime("2023-01-01 10:00");
        double horas = -2.0;

        Date result = DateUtil.sumarHoras(fecha, horas);
        assertNotNull(result);

        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
        assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
    }

    @Test
    public void testSumarHoras_ZeroHours() throws ParseException {
        Date fecha = DateUtil.parseDateTime("2023-01-01 10:00");
        double horas = 0.0;

        Date result = DateUtil.sumarHoras(fecha, horas);
        assertNotNull(result);

        assertEquals(fecha.getTime(), result.getTime());
    }
    
    @Test
    public void testSumarHoras_CrossingDays() throws ParseException {
        Date fecha = DateUtil.parseDateTime("2023-01-01 22:00");
        double horas = 4.0;

        Date result = DateUtil.sumarHoras(fecha, horas);
        assertNotNull(result);

        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        assertEquals(2023, cal.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, cal.get(Calendar.MONTH));
        assertEquals(2, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(2, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
    }
}