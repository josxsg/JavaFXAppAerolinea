package javafxappaerolinea.model.dao;

import javafxappaerolinea.exception.DuplicateResourceException;
import javafxappaerolinea.exception.ResourceNotFoundException;
import javafxappaerolinea.model.pojo.Customer;
import javafxappaerolinea.utility.JsonUtil; // Necesario para la inicialización directa del archivo de prueba
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class CustomerDAOTest {

    private static final String TEST_FILE_PATH = "data/clientes_test.json";
    private CustomerDAO customerDAO;

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
                "  {\"email\":\"john.doe@example.com\",\"birthDate\":\"1990-01-01\",\"nationality\":\"American\",\"name\":\"John Doe\"},\n" +
                "  {\"email\":\"jane.smith@example.com\",\"birthDate\":\"1985-05-10\",\"nationality\":\"British\",\"name\":\"Jane Smith\"}\n" +
                "]"
            );
        }
        customerDAO = new CustomerDAO(TEST_FILE_PATH);
    }

    @After
    public void tearDown() {
        // Nada específico que limpiar aquí, @Before ya se encarga de re-crear el archivo.
    }

    @Test
    public void testFindAll() throws IOException {
        List<Customer> result = customerDAO.findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testFindByEmail_Existing() throws IOException, ResourceNotFoundException {
        String email = "john.doe@example.com";
        Customer result = customerDAO.findByEmail(email);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals("John Doe", result.getName());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testFindByEmail_NonExisting() throws IOException, ResourceNotFoundException {
        String email = "nonexistent@example.com";
        customerDAO.findByEmail(email);
    }

    @Test
    public void testSave_NewCustomer() throws IOException, DuplicateResourceException {
        Customer newCustomer = new Customer("new.customer@example.com", new Date(120, 6, 20), "Canadian", "New Customer");
        customerDAO.save(newCustomer);

        Customer foundCustomer = null;
        try {
            foundCustomer = customerDAO.findByEmail("new.customer@example.com");
        } catch (ResourceNotFoundException e) {
            fail("El nuevo cliente debería ser encontrado.");
        }
        assertNotNull(foundCustomer);
        assertEquals("New Customer", foundCustomer.getName());
        assertEquals(3, customerDAO.findAll().size());
    }

    @Test(expected = DuplicateResourceException.class)
    public void testSave_DuplicateCustomer() throws IOException, DuplicateResourceException {
        Customer duplicateCustomer = new Customer("john.doe@example.com", new Date(), "German", "Duplicated John");
        customerDAO.save(duplicateCustomer);
    }

    @Test
    public void testUpdate_ExistingCustomer() throws IOException, ResourceNotFoundException {
        Customer updatedCustomer = new Customer("john.doe@example.com", new Date(122, 1, 1), "Spanish", "John Doe Updated");
        customerDAO.update(updatedCustomer);

        Customer foundCustomer = customerDAO.findByEmail("john.doe@example.com");
        assertEquals("John Doe Updated", foundCustomer.getName());
        assertEquals("Spanish", foundCustomer.getNationality());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testUpdate_NonExistingCustomer() throws IOException, ResourceNotFoundException {
        Customer nonExistingCustomer = new Customer("nonexistent@example.com", new Date(), "French", "Non Existent");
        customerDAO.update(nonExistingCustomer);
    }

    @Test
    public void testDelete_ExistingCustomer() throws IOException, ResourceNotFoundException {
        String emailToDelete = "jane.smith@example.com";

        customerDAO.delete(emailToDelete);

        assertEquals(1, customerDAO.findAll().size());
        try {
            customerDAO.findByEmail(emailToDelete);
            fail("El cliente eliminado aún fue encontrado.");
        } catch (ResourceNotFoundException e) {
            assertTrue(e.getMessage().contains("no encontrado"));
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDelete_NonExistingCustomer() throws IOException, ResourceNotFoundException {
        String emailToDelete = "nonexistent@example.com";
        customerDAO.delete(emailToDelete);
    }
}