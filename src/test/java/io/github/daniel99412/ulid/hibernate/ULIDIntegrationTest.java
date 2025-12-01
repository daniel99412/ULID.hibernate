package io.github.daniel99412.ulid.hibernate;

import io.github.daniel99412.ulid.ULID;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.*;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ULIDIntegrationTest {

    private static SessionFactory sessionFactory;

    @BeforeAll
    static void setup() {
        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(TestEntity.class);

            // Load properties explicitly
            Properties properties = new Properties();
            try (InputStream is = ULIDIntegrationTest.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
                if (is == null) {
                    throw new RuntimeException("hibernate.properties not found in classpath");
                }
                properties.load(is);
            }
            configuration.setProperties(properties);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Test
    void testUlidGeneration() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        TestEntity entity = new TestEntity("Test Name");
        assertNull(entity.getId(), "ID should be null before persisting");

        session.persist(entity);
        session.flush(); // Forces the ID generation

        assertNotNull(entity.getId(), "ID should be generated after persisting");

        ULID generatedUlid = entity.getId();
        assertNotNull(generatedUlid);

        session.getTransaction().commit();
        session.close();

        // Verify retrieval
        Session newSession = sessionFactory.openSession();
        TestEntity retrievedEntity = newSession.find(TestEntity.class, generatedUlid);
        assertNotNull(retrievedEntity, "Entity should be retrieved by its ULID");
        assertEquals("Test Name", retrievedEntity.getName());
        assertEquals(generatedUlid, retrievedEntity.getId());
        newSession.close();
    }

    @Test
    void testUlidOrdering() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        TestEntity entity1 = new TestEntity("First");
        TestEntity entity2 = new TestEntity("Second");

        session.persist(entity1);
        try {
            Thread.sleep(10); // Ensure a slight time difference for ULID generation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        session.persist(entity2);
        session.flush();

        assertNotNull(entity1.getId());
        assertNotNull(entity2.getId());

        assertTrue(entity1.getId().compareTo(entity2.getId()) < 0, "Earlier ULID should be lexicographically smaller");

        session.getTransaction().commit();
        session.close();
    }
}
