package io.github.daniel99412.ulid.hibernate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import io.github.daniel99412.ulid.ULID;

@Entity
public class TestEntity {

    @Id
    @GeneratedValue(generator = "ulid-generator")
    @GenericGenerator(name = "ulid-generator", type = ULIDIdGenerator.class)
    private ULID id;

    private String name;

    public TestEntity() {
    }

    public TestEntity(String name) {
        this.name = name;
    }

    public ULID getId() {
        return id;
    }

    public void setId(ULID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
