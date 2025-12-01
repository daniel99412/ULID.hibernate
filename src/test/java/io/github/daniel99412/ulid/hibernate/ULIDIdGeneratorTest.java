package io.github.daniel99412.ulid.hibernate;

import io.github.daniel99412.ulid.ULID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.Serializable;
import java.util.Properties;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ULIDIdGeneratorTest {

    private ULIDIdGenerator generator;

    @Mock
    private SharedSessionContractImplementor session;
    @Mock
    private EntityPersister entityPersister;
    @Mock
    private Type type;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        generator = new ULIDIdGenerator();

        // Mock configure method behavior
        when(type.getReturnedClass()).thenReturn((Class) ULID.class);
        generator.configure(type, new Properties(), null);
    }

    @Test
    void testGenerateWhenIdAlreadyAssigned() {
        Object entity = new Object();
        ULID existingId = ULID.getUlid();

        when(session.getEntityPersister(null, entity)).thenReturn(entityPersister);
        when(entityPersister.getIdentifier(entity, session)).thenReturn(existingId);

        Serializable generatedId = generator.generate(session, entity);

        assertEquals(existingId, generatedId);
        verify(session, times(1)).getEntityPersister(null, entity);
        verify(entityPersister, times(1)).getIdentifier(entity, session);
    }

    @Test
    void testGenerateNewUlid() {
        Object entity = new Object();
        ULID newUlid = ULID.getUlid(); // A specific ULID for this test

        when(session.getEntityPersister(null, entity)).thenReturn(entityPersister);
        when(entityPersister.getIdentifier(entity, session)).thenReturn(null); // No existing ID

        // Mock the supplier to return our specific ULID
        Supplier<ULID> mockSupplier = () -> newUlid;
        generator.setUlidSupplier(mockSupplier);

        Serializable generatedId = generator.generate(session, entity);

        assertEquals(newUlid, generatedId);
        verify(session, times(1)).getEntityPersister(null, entity);
        verify(entityPersister, times(1)).getIdentifier(entity, session);
    }

    @Test
    void testConfigureWithStringType() {
        when(type.getReturnedClass()).thenReturn((Class) String.class);
        assertDoesNotThrow(() -> generator.configure(type, new Properties(), null));
    }

    @Test
    void testConfigureWithByteArrayType() {
        when(type.getReturnedClass()).thenReturn((Class) byte[].class);
        assertDoesNotThrow(() -> generator.configure(type, new Properties(), null));
    }

    @Test
    void testConfigureWithUnsupportedType() {
        when(type.getReturnedClass()).thenReturn((Class) Integer.class);
        assertThrows(HibernateException.class, () -> generator.configure(type, new Properties(), null));
    }
}
