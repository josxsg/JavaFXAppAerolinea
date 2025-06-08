package javafxappaerolinea.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExportUtilTest {

    private static final String TEST_OUTPUT_DIR = "test_output";
    private static final String TEST_FILE_PREFIX = "export_test_";

    public static class BaseTestPojo {
        private String baseField;
        public BaseTestPojo(String baseField) { this.baseField = baseField; }
        public String getBaseField() { return baseField; }
        public void setBaseField(String baseField) { this.baseField = baseField; }
    }

    public static class TestPojo extends BaseTestPojo {
        private String name;
        private int id;
        private double value;
        private boolean active;
        private Date date;

        public TestPojo(String baseField, String name, int id, double value, boolean active, Date date) {
            super(baseField);
            this.name = name;
            this.id = id;
            this.value = value;
            this.active = active;
            this.date = date;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }
    }

    private List<TestPojo> testData;

    @BeforeClass
    public static void setUpClass() throws IOException {
        Files.createDirectories(Paths.get(TEST_OUTPUT_DIR));
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Files.walk(Paths.get(TEST_OUTPUT_DIR))
             .sorted(java.util.Comparator.reverseOrder())
             .map(java.nio.file.Path::toFile)
             .forEach(File::delete);
    }

    @Before
    public void setUp() {
        testData = new ArrayList<>();
        testData.add(new TestPojo("Base1", "Item A", 1, 10.5, true, new Date(123, 0, 1)));
        testData.add(new TestPojo("Base2", "Item B", 2, 20.0, false, new Date(122, 5, 15)));
        testData.add(new TestPojo("Base3", "Item C", 3, 30.75, true, new Date(124, 11, 31)));
    }

    @After
    public void tearDown() throws IOException {
        // Nada que limpiar aquí, @AfterClass ya maneja la limpieza del directorio
    }

    @Test
    public void testExportToCSV_DefaultFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "default.csv";
        ExportUtil.exportToCSV(testData, filePath);
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToCSV_DefaultFields_EmptyData() throws IOException {
        ExportUtil.exportToCSV(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty.csv");
    }

    @Test
    public void testExportToCSV_OrderedFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "ordered.csv";
        List<String> columnOrder = Arrays.asList("id", "name", "date", "baseField");
        ExportUtil.exportToCSV(testData, filePath, columnOrder);
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToCSV_OrderedFields_EmptyData() throws IOException {
        List<String> columnOrder = Arrays.asList("id", "name");
        ExportUtil.exportToCSV(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty_ordered.csv", columnOrder);
    }

    @Test
    public void testExportToXLS_DefaultFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "default.xls";
        ExportUtil.exportToXLS(testData, filePath, "TestSheet");
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToXLS_DefaultFields_EmptyData() throws IOException {
        ExportUtil.exportToXLS(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty.xls", "TestSheet");
    }

    @Test
    public void testExportToXLS_OrderedFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "ordered.xls";
        List<String> columnOrder = Arrays.asList("name", "id", "active", "date", "baseField");
        ExportUtil.exportToXLS(testData, filePath, "TestSheet", columnOrder);
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToXLS_OrderedFields_EmptyData() throws IOException {
        List<String> columnOrder = Arrays.asList("name", "id");
        ExportUtil.exportToXLS(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty_ordered.xls", "TestSheet", columnOrder);
    }

    @Test
    public void testExportToXLSX_DefaultFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "default.xlsx";
        ExportUtil.exportToXLSX(testData, filePath, "TestSheetX");
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToXLSX_DefaultFields_EmptyData() throws IOException {
        ExportUtil.exportToXLSX(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty.xlsx", "TestSheetX");
    }

    @Test
    public void testExportToXLSX_OrderedFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "ordered.xlsx";
        List<String> columnOrder = Arrays.asList("id", "name", "value", "active", "date", "baseField");
        ExportUtil.exportToXLSX(testData, filePath, "TestSheetX", columnOrder);
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToXLSX_OrderedFields_EmptyData() throws IOException {
        List<String> columnOrder = Arrays.asList("id", "name");
        ExportUtil.exportToXLSX(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty_ordered.xlsx", "TestSheetX", columnOrder);
    }

    @Test
    public void testExportToPDF_DefaultFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "default.pdf";
        ExportUtil.exportToPDF(testData, filePath, "PDF Report");
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToPDF_DefaultFields_EmptyData() throws IOException {
        ExportUtil.exportToPDF(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty.pdf", "PDF Report");
    }

    @Test
    public void testExportToPDF_OrderedFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "ordered.pdf";
        List<String> columnOrder = Arrays.asList("name", "active", "value", "date", "id", "baseField");
        ExportUtil.exportToPDF(testData, filePath, "PDF Report", columnOrder);
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToPDF_OrderedFields_EmptyData() throws IOException {
        List<String> columnOrder = Arrays.asList("name", "id");
        ExportUtil.exportToPDF(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty_ordered.pdf", "PDF Report", columnOrder);
    }

    @Test
    public void testExportToJSON_DefaultFields() throws IOException {
        String filePath = TEST_OUTPUT_DIR + File.separator + TEST_FILE_PREFIX + "default.json";
        ExportUtil.exportToJSON(testData, filePath);
        assertTrue(Files.exists(Paths.get(filePath)));
        assertTrue(Files.size(Paths.get(filePath)) > 0);

        String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
        assertTrue(jsonContent.contains("\"name\": \"Item A\""));
        assertTrue(jsonContent.contains("\"id\": 2"));
        assertTrue(jsonContent.contains("\"active\": true"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExportToJSON_DefaultFields_EmptyData() throws IOException {
        ExportUtil.exportToJSON(new ArrayList<TestPojo>(), TEST_OUTPUT_DIR + File.separator + "empty.json");
    }
}