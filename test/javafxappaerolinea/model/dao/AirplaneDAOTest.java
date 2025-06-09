package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.model.pojo.Airplane;
import javafxappaerolinea.utility.JsonUtil; 
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class AirplaneDAOTest {

    private static final String TEST_FILE_PATH = "data/aviones_test.json";
    private AirplaneDAO airplaneDAO;

    private static final Airline AERO_MEXICO = new Airline(1, "Mexico City", "Aeromexico", "Contact AM", "111");
    private static final Airline VIVA_AEROBUS = new Airline(2, "Monterrey", "Viva Aerobus", "Contact VA", "222");

    @BeforeClass
    public static void setUpClass() throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
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
                "  {\"capacity\":180,\"age\":5,\"status\":true,\"registration\":\"XA-ABC\",\"model\":\"A320\",\"weight\":50000.0,\"airline\":{\"identificationNumber\":1,\"address\":\"Mexico City\",\"name\":\"Aeromexico\",\"contactPerson\":\"Contact AM\",\"phoneNumber\":\"111\"}},\n" +
                "  {\"capacity\":150,\"age\":10,\"status\":false,\"registration\":\"XA-DEF\",\"model\":\"B737\",\"weight\":45000.0,\"airline\":{\"identificationNumber\":1,\"address\":\"Mexico City\",\"name\":\"Aeromexico\",\"contactPerson\":\"Contact AM\",\"phoneNumber\":\"111\"}},\n" +
                "  {\"capacity\":220,\"age\":3,\"status\":true,\"registration\":\"XB-GHI\",\"model\":\"E190\",\"weight\":30000.0,\"airline\":{\"identificationNumber\":2,\"address\":\"Monterrey\",\"name\":\"Viva Aerobus\",\"contactPerson\":\"Contact VA\",\"phoneNumber\":\"222\"}}\n" +
                "]"
            );
        }
        airplaneDAO = new AirplaneDAO(TEST_FILE_PATH);
    }


    @Test
    public void testFindAll() throws IOException {
        List<Airplane> result = airplaneDAO.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testFindByRegistration_Existing() throws IOException, ResourceNotFoundException {
        String registration = "XA-ABC";
        Airplane result = airplaneDAO.findByRegistration(registration);
        assertNotNull(result);
        assertEquals(registration, result.getRegistration());
        assertEquals("A320", result.getModel());
        assertTrue(result.isStatus());
        assertEquals(AERO_MEXICO.getIdentificationNumber(), result.getAirline().getIdentificationNumber());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testFindByRegistration_NonExisting() throws IOException, ResourceNotFoundException {
        String registration = "NON-EXISTENT";
        airplaneDAO.findByRegistration(registration);
    }

    @Test
    public void testFindByAirline_Existing() throws IOException {
        int airlineId = AERO_MEXICO.getIdentificationNumber(); 
        List<Airplane> result = airplaneDAO.findByAirline(airlineId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getAirline().getIdentificationNumber() == airlineId));
        assertTrue(result.stream().anyMatch(a -> a.getRegistration().equals("XA-ABC")));
        assertTrue(result.stream().anyMatch(a -> a.getRegistration().equals("XA-DEF")));
    }

    @Test
    public void testFindByAirline_NonExisting() throws IOException {
        int airlineId = 99; 
        List<Airplane> result = airplaneDAO.findByAirline(airlineId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindByAirline_NoAirlineAssigned() throws IOException {
        Airplane noAirlinePlane = new Airplane(100, 1, true, "ZZ-XYZ", "NoAirlineModel", 1000.0, null);
        List<Airplane> currentAirplanes = airplaneDAO.findAll(); 
        currentAirplanes.add(noAirlinePlane); 
        new JsonUtil<>(TEST_FILE_PATH, Airplane.class).saveAll(currentAirplanes); 

        List<Airplane> result = airplaneDAO.findByAirline(AERO_MEXICO.getIdentificationNumber());
        assertNotNull(result);
        assertEquals(2, result.size()); 
        assertTrue(result.stream().noneMatch(a -> a.getRegistration().equals("ZZ-XYZ"))); 
    }


    @Test
    public void testSave_NewAirplane() throws IOException, DuplicateResourceException {
        Airplane newAirplane = new Airplane(120, 2, true, "ZZ-NEW", "C172", 1200.0, VIVA_AEROBUS);
        airplaneDAO.save(newAirplane);

        Airplane foundAirplane = null;
        try {
            foundAirplane = airplaneDAO.findByRegistration("ZZ-NEW");
        } catch (ResourceNotFoundException e) {
            fail("El nuevo avión debería ser encontrado.");
        }
        assertNotNull(foundAirplane);
        assertEquals("ZZ-NEW", foundAirplane.getRegistration());
        assertEquals(4, airplaneDAO.findAll().size()); 
    }

    @Test(expected = DuplicateResourceException.class)
    public void testSave_DuplicateAirplane() throws IOException, DuplicateResourceException {
        Airplane duplicateAirplane = new Airplane(180, 5, true, "XA-ABC", "A320-Duplicated", 50000.0, AERO_MEXICO);
        airplaneDAO.save(duplicateAirplane);
    }

    @Test
    public void testUpdate_ExistingAirplane() throws IOException, ResourceNotFoundException {
        Airplane updatedAirplane = new Airplane(190, 6, false, "XA-ABC", "A320-Updated", 55000.0, AERO_MEXICO);
        airplaneDAO.update(updatedAirplane);

        Airplane foundAirplane = airplaneDAO.findByRegistration("XA-ABC");
        assertEquals("A320-Updated", foundAirplane.getModel());
        assertEquals(190, foundAirplane.getCapacity());
        assertFalse(foundAirplane.isStatus()); 
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdate_NonExistingAirplane() throws IOException, ResourceNotFoundException {
        Airplane nonExistingAirplane = new Airplane(100, 1, true, "NON-EXISTENT", "TestModel", 1000.0, AERO_MEXICO);
        airplaneDAO.update(nonExistingAirplane);
    }

    @Test
    public void testDelete_ExistingAirplane() throws IOException, ResourceNotFoundException {
        String registrationToDelete = "XA-DEF"; 

        airplaneDAO.delete(registrationToDelete);

        assertEquals(2, airplaneDAO.findAll().size()); 
        try {
            airplaneDAO.findByRegistration(registrationToDelete);
            fail("El avión eliminado aún fue encontrado."); 
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("no encontrado"));
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDelete_NonExistingAirplane() throws IOException, ResourceNotFoundException {
        String registrationToDelete = "NON-EXISTENT";
        airplaneDAO.delete(registrationToDelete);
    }
}