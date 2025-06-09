package databaseForNews.ConnectToDatabase;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceProvided {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceProvided.class);
    private static DataSource productionDataSource;
    private static DataSource testDataSource;

    public static DataSource getDataSource() {
        if (testDataSource != null) {
            logger.debug("Возвращаем тестовый DataSource.");
            return testDataSource;
        }
        if (productionDataSource == null) {
            logger.info("Тестовый DataSource не задан, делаем productionDataSource через HikariCP");
            productionDataSource = createProductionDataSource();
            logger.info("Production DataSource успешно создан.");
        } else {
            logger.debug("Возвращаем ранее созданный productionDataSource.");
        }
        return productionDataSource;
    }

    public static void reset() {
        productionDataSource = null;
        testDataSource = null;
    }

    public static void setTestDataSource(DataSource ds) {
        logger.debug("Устанавливаем testDataSource: {}", ds);
        testDataSource = ds;
    }

    private static DataSource createProductionDataSource() {
        Properties props = loadProperties("application.properties");
        HikariConfig config = buildHikariConfig(props);
        return new HikariDataSource(config);
    }

    private static Properties loadProperties(String resourceName) {
        Properties props = new Properties();
        logger.debug("Пытаемся загрузить информацию с файла: {}", resourceName);

        try (InputStream is = DataSourceProvided.class
                .getClassLoader()
                .getResourceAsStream(resourceName))
        {
            if (is == null) {
                logger.error("Файл {} не найден", resourceName);
                throw new RuntimeException("Не найден файл " + resourceName);
            }
            props.load(is);
            logger.debug("Файл {} успешно загружен.", resourceName);
        } catch (Exception e) {
            logger.error("Ошибка при загрузке файла свойств {}: {}", resourceName, e.getMessage());
            throw new RuntimeException("Ошибка при загрузке файла свойств: " + resourceName, e);
        }
        return props;
    }

    private static HikariConfig buildHikariConfig(Properties props) {
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String password = props.getProperty("db.password");
        String poolSize = props.getProperty("db.poolSize");

        logger.debug("Собираем HikariConfig: url={}, user={}, poolSize={}", url, user, poolSize);

        if (url == null || user == null || password == null) {
            String message= "не хваетает информации в файле";
            logger.error(message);
            throw new RuntimeException(message);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);

        if (poolSize != null) {
            try {
                int pools = Integer.parseInt(poolSize);
                config.setMaximumPoolSize(pools);
                logger.debug("Установлен размер пула: {}", pools);
            } catch (NumberFormatException ex) {
                String mistake = "Неверное значение db.poolSize: " + poolSize;
                logger.error(mistake, ex);
                throw new RuntimeException(mistake, ex);
            }
        }
        return config;
    }
}
