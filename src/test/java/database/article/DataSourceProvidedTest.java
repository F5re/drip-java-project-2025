package database.article;

import com.zaxxer.hikari.HikariDataSource;
import databaseForNews.ConnectToDatabase.DataSourceProvided;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class DataSourceProvidedTest {

    private static final String necessaryFile = "application.properties";
    private static final File testPath =  new File("target/test-classes/" + necessaryFile);

    @BeforeEach
    void resetState() {
        DataSourceProvided.reset();
        if (testPath.exists()) {
            testPath.delete();
        }
    }

    @Test
    void testProductionDatasourceSuccess() throws Exception {
        writePropertiesFile("""
            db.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
            db.username=sa
            db.password=
            db.poolSize=5
            """);

        DataSource ds = DataSourceProvided.getDataSource();
        assertNotNull(ds);
        assertTrue(ds instanceof HikariDataSource);

        try (Connection conn = ds.getConnection()) {
            assertFalse(conn.isClosed());
        }
    }

    @Test
    void testMissingPropertiesThrowsException() throws IOException {
        writePropertiesFile("""
            # отсутствуют db.url, db.username, db.password
            db.poolSize=3
            """);

        RuntimeException ex = assertThrows(RuntimeException.class, DataSourceProvided::getDataSource);
        assertTrue(ex.getMessage().contains("не хваетает информации"));
    }

    @Test
    void testInvalidPoolSizeThrowsException() throws IOException {
        writePropertiesFile("""
            db.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
            db.username=sa
            db.password=
            db.poolSize=notANumber
            """);

        RuntimeException ex = assertThrows(RuntimeException.class, DataSourceProvided::getDataSource);
        assertTrue(ex.getMessage().contains("Неверное значение db.poolSize"));
    }

    @Test
    void testSetTestDataSourceOverridesProduction() throws SQLException {
        HikariDataSource mockDs = new HikariDataSource();
        DataSourceProvided.setTestDataSource(mockDs);

        DataSource result = DataSourceProvided.getDataSource();
        assertSame(mockDs, result);
    }

    private void writePropertiesFile(String content) throws IOException {
        File dir = testPath.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (FileWriter writer = new FileWriter(testPath)) {
            writer.write(content);
        }
    }
}