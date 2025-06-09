package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Administrative;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Assistant;
import javafxappaerolinea.model.pojo.Pilot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmployeeDAOTest {

    private static final String TEST_FILE_PATH = "data/empleados_test.json";
    private EmployeeDAO employeeDAO;

    private static final Airline TEST_AIRLINE = new Airline(100, "Test Address", "Test Airline", "Test Contact", "555-1234");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static Administrative ADMIN_ONE;
    private static Pilot PILOT_ONE;
    private static Assistant ASSISTANT_ONE;

    @BeforeClass
    public static void setUpClass() throws IOException, ParseException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }

        ADMIN_ONE = new Administrative("ADM001", "Admin One", "Addr A1", dateFormat.parse("1990-01-01"), "M", 50000.0, "adminone", "pass1", "HR", 8);
        PILOT_ONE = new Pilot("PIL001", "Pilot One", "Addr P1", dateFormat.parse("1985-05-15"), "M", 70000.0, "pilotone", "pass2", 10, "pilot1@test.com", 1000.0, "Commercial", TEST_AIRLINE);
        ASSISTANT_ONE = new Assistant("AST001", "Asst One", "Addr AS1", dateFormat.parse("1992-11-20"), "F", 30000.0, "asstone", "pass3", "asst1@test.com", 500, 3, TEST_AIRLINE);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
    }

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write(
                "[\n" +
                "  {\"id\":\"ADM001\",\"name\":\"Admin One\",\"address\":\"Addr A1\",\"birthDate\":\"1990-01-01\",\"gender\":\"M\",\"salary\":50000.0,\"username\":\"adminone\",\"password\":\"pass1\",\"type\":\"Administrative\",\"department\":\"HR\",\"workHours\":8},\n" +
                "  {\"id\":\"PIL001\",\"name\":\"Pilot One\",\"address\":\"Addr P1\",\"birthDate\":\"1985-05-15\",\"gender\":\"M\",\"salary\":70000.0,\"username\":\"pilotone\",\"password\":\"pass2\",\"type\":\"Pilot\",\"yearsExperience\":10,\"email\":\"pilot1@test.com\",\"flightHours\":1000.0,\"licenseType\":\"Commercial\",\"airline\":{\"identificationNumber\":100,\"address\":\"Test Address\",\"name\":\"Test Airline\",\"contactPerson\":\"Test Contact\",\"phoneNumber\":\"555-1234\"}},\n" +
                "  {\"id\":\"AST001\",\"name\":\"Asst One\",\"address\":\"Addr AS1\",\"birthDate\":\"1992-11-20\",\"gender\":\"F\",\"salary\":30000.0,\"username\":\"asstone\",\"password\":\"pass3\",\"type\":\"Assistant\",\"email\":\"asst1@test.com\",\"assistanceHours\":500,\"numberOfLanguages\":3,\"airline\":{\"identificationNumber\":100,\"address\":\"Test Address\",\"name\":\"Test Airline\",\"contactPerson\":\"Test Contact\",\"phoneNumber\":\"555-1234\"}}\n" +
                "]"
            );
        }
        employeeDAO = new EmployeeDAO(TEST_FILE_PATH);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testFindById_NonExisting() throws IOException, ResourceNotFoundException {
        employeeDAO.findById("NONEXISTENT");
    }
    
    @Test(expected = ResourceNotFoundException.class)
    public void testFindByUsername_NonExisting() throws IOException, ResourceNotFoundException {
        employeeDAO.findByUsername("nonexistentUser");
    }

    
    @Test(expected = ResourceNotFoundException.class)
    public void testUpdate_NonExistingEmployee() throws IOException, ResourceNotFoundException {
        Pilot nonExistingPilot = new Pilot("PIL999", "Non Existent", "N/A", new Date(), "M", 0.0, "nonexistent", "x", 0, "n@e.com", 0.0, "N/A", null);
        employeeDAO.update(nonExistingPilot);
    }

    
    @Test(expected = ResourceNotFoundException.class)
    public void testDelete_NonExistingEmployee() throws IOException, ResourceNotFoundException {
        employeeDAO.delete("NONEXISTENT");
    }
}