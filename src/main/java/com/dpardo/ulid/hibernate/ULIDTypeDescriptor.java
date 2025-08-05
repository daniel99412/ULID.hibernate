package com.dpardo.ulid.hibernate;

import com.dpardo.ulid.ULID;
import org.hibernate.internal.util.BytesHelper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractJavaType;

import java.io.Serializable;

/**
 * Hibernate type descriptor for ULID (Universally Unique Lexicographically Sortable Identifier).
 * This class defines how ULIDs are converted to and from different types (String, bytes, etc.)
 * for persistence in a Hibernate-compatible way.
 */
public class ULIDTypeDescriptor extends AbstractJavaType<ULID> {
    /** Singleton instance of ULIDTypeDescriptor */
    public static final ULIDTypeDescriptor INSTANCE = new ULIDTypeDescriptor();

    /**
     * Default constructor that specifies ULID as the target Java type.
     */
    public ULIDTypeDescriptor() {
        super(ULID.class);
    }

    /**
     * Converts a ULID object to its String representation.
     *
     * @param value ULID instance
     * @return String representation of the ULID, or null if the input is null
     */
    @Override
    public String toString(ULID value) {
        return value == null ? null : value.toString();
    }

    /**
     * Converts a CharSequence to a ULID object.
     *
     * @param sequence CharSequence representing a ULID
     * @return ULID instance, or null if input is null
     */
    @Override
    public ULID fromString(CharSequence sequence) {
        return sequence == null ? null : ULID.fromString(sequence.toString());
    }

    /**
     * Converts a ULID object to another type (e.g., String) for persistence.
     *
     * @param value ULID instance
     * @param type Target class type
     * @param options Wrapper options for conversion
     * @return Converted value of type T, or null if input is null
     * @throws IllegalArgumentException if the type is unsupported
     */
    @Override
    public <T> T unwrap(ULID value, Class<T> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (T) value.toString();
        }
        throw unknownUnwrap(type);
    }

    /**
     * Converts a given value into a ULID instance.
     *
     * @param value Object to be converted
     * @param options Wrapper options for conversion
     * @return ULID instance
     * @throws IllegalArgumentException if input type is unsupported
     */
    @Override
    public <T> ULID wrap(T value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return ULID.fromString((String) value);
        }
        throw unknownWrap(value.getClass());
    }

    /**
     * Interface for transforming ULID values into other serializable formats.
     */
    public interface ValueTransformer {
        Serializable transform(ULID ulid);

        ULID parse(Object value);
    }

    /**
     * Transformer that passes ULID objects through without modification.
     */
    public static class PassThroughTransformer implements ValueTransformer {
        public static final PassThroughTransformer INSTANCE = new PassThroughTransformer();

        public ULID transform(ULID ulid) {
            return ulid;
        }

        public ULID parse(Object value) {
            return (ULID) value;
        }
    }

    /**
     * Transformer that converts ULID to and from String representation.
     */
    public static class StringTransformer implements ValueTransformer {
        public static final StringTransformer INSTANCE = new StringTransformer();

        public String transform(ULID ulid) {
            return ulid.toString();
        }

        public ULID parse(Object value) {
            return ULID.fromString((String) value);
        }
    }

    /**
     * Transformer that converts ULID to and from byte array representation.
     */
    public static class ToBytesTransformer implements ValueTransformer {
        public static final ToBytesTransformer INSTANCE = new ToBytesTransformer();

        public byte[] transform(ULID ulid) {
            byte[] bytes = new byte[16];
            BytesHelper.fromLong(ulid.getMostSignificantBits(), bytes, 0);
            BytesHelper.fromLong(ulid.getLeastSignificantBits(), bytes, 8);
            return bytes;
        }

        public ULID parse(Object value) {
            byte[] bytea = (byte[]) value;
            return ULID.of(BytesHelper.asLong(bytea, 0), BytesHelper.asLong(bytea, 8));
        }
    }
}
