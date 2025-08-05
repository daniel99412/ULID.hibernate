package com.dpardo.ulid.hibernate;

import com.dpardo.ulid.ULID;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * ULIDIdGenerator is a custom identifier generator for Hibernate that generates ULIDs (Universally Unique Lexicographically Sortable Identifiers).
 * This generator supports different data representations, including ULID, String, and byte[].
 */
public class ULIDIdGenerator implements IdentifierGenerator {

    private ULIDTypeDescriptor.ValueTransformer valueTransformer;

    /**
     * Configures the ULID identifier generator based on the expected return type.
     *
     * @param type          The Hibernate type of the identifier.
     * @param parameters    Configuration properties for the generator.
     * @param serviceRegistry The Hibernate service registry.
     * @throws MappingException If the return type is unsupported.
     */
    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) throws MappingException {
        if (ULID.class.isAssignableFrom(type.getReturnedClass())) {
            valueTransformer = ULIDTypeDescriptor.PassThroughTransformer.INSTANCE;
        } else if (String.class.isAssignableFrom(type.getReturnedClass())) {
            valueTransformer = ULIDTypeDescriptor.StringTransformer.INSTANCE;
        } else if (byte[].class.isAssignableFrom(type.getReturnedClass())) {
            valueTransformer = ULIDTypeDescriptor.ToBytesTransformer.INSTANCE;
        } else {
            throw new HibernateException("Unanticipated return type [" + type.getReturnedClass().getName() + "] for ULID conversion");
        }
    }

    /**
     * Generates a new ULID identifier.
     *
     * @param session The Hibernate session.
     * @param object  The entity for which the identifier is being generated.
     * @return The generated ULID in the appropriate format (ULID, String, or byte[]).
     * @throws HibernateException If an error occurs during ID generation.
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        // Check if the entity already has an identifier assigned
        Object id = session.getEntityPersister(null, object)
                .getClassMetadata().getIdentifier(object, session);

        if (id != null) {
            return (Serializable) id;
        }

        // Generate a new ULID and transform it to the appropriate format
        ULID val = ULID.getUlid();
        return valueTransformer.transform(val);
    }
}
