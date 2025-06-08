package javafxappaerolinea.utility;

import javafxappaerolinea.model.pojo.Airline;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class JsonUtilTest {

    private static final String TEST_FILE_PATH = "data/test_persistence.json";
    private JsonUtil<Airline> jsonUtil;

    @BeforeClass
    public static void setUpClass() throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
    }

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        jsonUtil = new JsonUtil<>(TEST_FILE_PATH, Airline.class);
    }

    @Test
    public void testSaveAllAndLoadAll_EmptyList() throws IOException {
        List<Airline> emptyList = new ArrayList<>();
        jsonUtil.saveAll(emptyList);

        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertTrue(loadedList.isEmpty());
    }

    @Test
    public void testSaveAllAndLoadAll_WithData() throws IOException {
        List<Airline> airlines = new ArrayList<>();
        airlines.add(new Airline(1, "Address1", "Airline A", "Contact A", "111"));
        airlines.add(new Airline(2, "Address2", "Airline B", "Contact B", "222"));
        jsonUtil.saveAll(airlines);

        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertEquals(2, loadedList.size());
        assertEquals("Airline A", loadedList.get(0).getName());
        assertEquals("Airline B", loadedList.get(1).getName());
    }

    @Test
    public void testLoadAll_NonExistentFile() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertTrue(loadedList.isEmpty());
    }

    @Test
    public void testSaveSingleObject() throws IOException {
        Airline newAirline = new Airline(3, "Address3", "Airline C", "Contact C", "333");
        jsonUtil.save(newAirline);

        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertEquals(1, loadedList.size());
        assertEquals("Airline C", loadedList.get(0).getName());
    }

    @Test
    public void testUpdateObject_Existing() throws IOException {
        List<Airline> initialAirlines = new ArrayList<>();
        initialAirlines.add(new Airline(1, "Address1", "Airline A", "Contact A", "111"));
        jsonUtil.saveAll(initialAirlines);

        Airline updatedAirline = new Airline(1, "Updated Address", "Updated Airline A", "New Contact", "999");
        jsonUtil.update(updatedAirline, a -> a.getIdentificationNumber() == 1);

        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertEquals(1, loadedList.size());
        assertEquals("Updated Airline A", loadedList.get(0).getName());
        assertEquals("Updated Address", loadedList.get(0).getAddress());
    }

    @Test
    public void testUpdateObject_NonExisting() throws IOException {
        List<Airline> initialAirlines = new ArrayList<>();
        initialAirlines.add(new Airline(1, "Address1", "Airline A", "Contact A", "111"));
        jsonUtil.saveAll(initialAirlines);

        Airline nonExistingAirline = new Airline(99, "Non Existent", "N/A", "N/A", "000");
        jsonUtil.update(nonExistingAirline, a -> a.getIdentificationNumber() == 99);

        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertEquals(1, loadedList.size());
        assertEquals("Airline A", loadedList.get(0).getName()); // Asegurarse de que no cambió
    }

    @Test
    public void testDeleteObject_Existing() throws IOException {
        List<Airline> initialAirlines = new ArrayList<>();
        initialAirlines.add(new Airline(1, "Address1", "Airline A", "Contact A", "111"));
        initialAirlines.add(new Airline(2, "Address2", "Airline B", "Contact B", "222"));
        jsonUtil.saveAll(initialAirlines);

        jsonUtil.delete(a -> a.getIdentificationNumber() == 1);

        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertEquals(1, loadedList.size());
        assertEquals("Airline B", loadedList.get(0).getName());
    }

    @Test
    public void testDeleteObject_NonExisting() throws IOException {
        List<Airline> initialAirlines = new ArrayList<>();
        initialAirlines.add(new Airline(1, "Address1", "Airline A", "Contact A", "111"));
        jsonUtil.saveAll(initialAirlines);

        jsonUtil.delete(a -> a.getIdentificationNumber() == 99);

        List<Airline> loadedList = jsonUtil.loadAll();
        assertNotNull(loadedList);
        assertEquals(1, loadedList.size()); // Asegurarse de que no cambió
        assertEquals("Airline A", loadedList.get(0).getName());
    }



    @Test(expected = IOException.class)
    public void testLoadAll_CorruptedFile() throws IOException {
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write("invalid json { \"key\": \"value\", \"bad\" }");
        }
        jsonUtil.loadAll();
    }
}