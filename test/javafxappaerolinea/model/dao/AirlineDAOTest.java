package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Airline;
import javafxappaerolinea.utility.JsonUtil;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class AirlineDAOTest {

    private static final String TEST_FILE_PATH = "data/aerolineas_test.json";
    private AirlineDAO airlineDAO;

    @BeforeClass
    public static void setUpClass() throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }

    @AfterClass
    public static void tearDownClass() {
        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Before
    public void setUp() throws IOException {
        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write(
                "[\n" +
                "  {\"identificationNumber\": 1, \"name\": \"Aeromexico\", \"address\": \"Mexico City\", \"contactPerson\": \"Contact1\", \"phoneNumber\": \"111\"},\n" +
                "  {\"identificationNumber\": 2, \"name\": \"VivaAerobus\", \"address\": \"Monterrey\", \"contactPerson\": \"Contact2\", \"phoneNumber\": \"222\"},\n" +
                "  {\"identificationNumber\": 3, \"name\": \"Volavis\", \"address\": \"Mexico City\", \"contactPerson\": \"Contact3\", \"phoneNumber\": \"333\"}\n" +
                "]"
            );
        }
        airlineDAO = new AirlineDAO(TEST_FILE_PATH);
    }

    @Test
    public void testFindAll() throws IOException {
        List<Airline> result = airlineDAO.findAll();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testFindById_Existing() throws IOException, ResourceNotFoundException {
        int id = 1;
        Airline result = airlineDAO.findById(id);
        assertNotNull(result);
        assertEquals(id, result.getIdentificationNumber());
        assertEquals("Aeromexico", result.getName());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testFindById_NonExisting() throws IOException, ResourceNotFoundException {
        int id = 99;
        airlineDAO.findById(id);
    }

    @Test
    public void testSave_NewAirline() throws IOException, DuplicateResourceException {
        Airline newAirline = new Airline(4, "Address4", "Air Canada", "Contact4", "444");
        airlineDAO.save(newAirline);

        Airline foundAirline = null;
        try {
            foundAirline = airlineDAO.findById(4);
        } catch (ResourceNotFoundException e) {
            fail("La nueva aerolínea debería ser encontrada.");
        }
        assertNotNull(foundAirline);
        assertEquals(4, foundAirline.getIdentificationNumber());
        assertEquals(4, airlineDAO.findAll().size());
    }

    @Test(expected = DuplicateResourceException.class)
    public void testSave_DuplicateAirline() throws IOException, DuplicateResourceException {
        Airline duplicateAirline = new Airline(1, "Address Duplicate", "Aeromexico Duplicate", "Contact Duplicate", "111");
        airlineDAO.save(duplicateAirline);
    }

    @Test
    public void testUpdate_ExistingAirline() throws IOException, ResourceNotFoundException {
        Airline updatedAirline = new Airline(1, "New Address", "Aeromexico Updated", "New Contact", "111-updated");
        airlineDAO.update(updatedAirline);

        Airline foundAirline = airlineDAO.findById(1);
        assertEquals("Aeromexico Updated", foundAirline.getName());
        assertEquals("New Address", foundAirline.getAddress());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdate_NonExistingAirline() throws IOException, ResourceNotFoundException {
        Airline nonExistingAirline = new Airline(99, "Address N/A", "Non Existent", "Contact N/A", "000");
        airlineDAO.update(nonExistingAirline);
    }

    @Test
    public void testDelete_ExistingAirline() throws IOException, ResourceNotFoundException {
        int idToDelete = 2; // VivaAerobus

        airlineDAO.delete(idToDelete);

        assertEquals(2, airlineDAO.findAll().size());
        try {
            airlineDAO.findById(idToDelete);
            fail("La aerolínea eliminada aún fue encontrada.");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("no encontrada"));
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDelete_NonExistingAirline() throws IOException, ResourceNotFoundException {
        int idToDelete = 99;
        airlineDAO.delete(idToDelete);
    }

}