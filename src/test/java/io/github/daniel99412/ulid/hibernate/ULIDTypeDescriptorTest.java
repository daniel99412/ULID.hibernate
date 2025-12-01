package io.github.daniel99412.ulid.hibernate;

import io.github.daniel99412.ulid.ULID;
import org.hibernate.type.descriptor.WrapperOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ULIDTypeDescriptorTest {

    private ULIDTypeDescriptor descriptor;
    @Mock
    private WrapperOptions wrapperOptions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        descriptor = ULIDTypeDescriptor.INSTANCE;
    }

    @Test
    void testToString() {
        ULID ulid = ULID.getUlid();
        assertEquals(ulid.toString(), descriptor.toString(ulid));
        assertNull(descriptor.toString(null));
    }

    @Test
    void testFromString() {
        ULID ulid = ULID.getUlid();
        assertEquals(ulid, descriptor.fromString(ulid.toString()));
        assertNull(descriptor.fromString(null));
    }

    @Test
    void testUnwrapULIDToString() {
        ULID ulid = ULID.getUlid();
        assertEquals(ulid.toString(), descriptor.unwrap(ulid, String.class, wrapperOptions));
    }

    @Test
    void testUnwrapNullULID() {
        assertNull(descriptor.unwrap(null, String.class, wrapperOptions));
    }

    @Test
    void testUnwrapUnsupportedType() {
        ULID ulid = ULID.getUlid();
        assertThrows(IllegalArgumentException.class, () -> descriptor.unwrap(ulid, Integer.class, wrapperOptions));
    }

    @Test
    void testWrapStringIntoULID() {
        ULID ulid = ULID.getUlid();
        assertEquals(ulid, descriptor.wrap(ulid.toString(), wrapperOptions));
    }

    @Test
    void testWrapNullValue() {
        assertNull(descriptor.wrap(null, wrapperOptions));
    }

    @Test
    void testWrapUnsupportedType() {
        assertThrows(IllegalArgumentException.class, () -> descriptor.wrap(123, wrapperOptions));
    }

    // Test ValueTransformers
    @Test
    void testPassThroughTransformer() {
        ULID ulid = ULID.getUlid();
        assertEquals(ulid, ULIDTypeDescriptor.PassThroughTransformer.INSTANCE.transform(ulid));
        assertEquals(ulid, ULIDTypeDescriptor.PassThroughTransformer.INSTANCE.parse(ulid));
    }

    @Test
    void testStringTransformer() {
        ULID ulid = ULID.getUlid();
        assertEquals(ulid.toString(), ULIDTypeDescriptor.StringTransformer.INSTANCE.transform(ulid));
        assertEquals(ulid, ULIDTypeDescriptor.StringTransformer.INSTANCE.parse(ulid.toString()));
    }

    @Test
    void testToBytesTransformer() {
        ULID ulid = ULID.getUlid();
        byte[] bytes = ULIDTypeDescriptor.ToBytesTransformer.INSTANCE.transform(ulid);
        assertArrayEquals(ulid.toBytes(), bytes); // Assuming ULID has a toBytes method for comparison
        assertEquals(ulid, ULIDTypeDescriptor.ToBytesTransformer.INSTANCE.parse(bytes));
    }
}
