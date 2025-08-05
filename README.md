# 🏛️ ULID Hibernate Integration

## ✨ Overview

This library provides **Hibernate support** for using [ULID](https://github.com/daniel99412/ULID) (Universally Unique Lexicographically Sortable Identifier) as entity identifiers in your Java applications.

It acts as an **optional Hibernate add-on** for the [`ulid`](https://github.com/daniel99412/ULID) core library, allowing you to keep your domain model clean while still using ULIDs in your persistence layer.

---

## 🧩 Features

- 🧬 ULID generator for Hibernate entities
- 🔄 Custom `UserType` / `TypeDescriptor` for proper ULID mapping
- 🧼 Clean separation of concerns (infrastructure-only library)
- 🧱 Compatible with JPA & Hibernate ORM

---

## 📦 Installation

First, add the core ULID library:

```gradle
dependencies {
    implementation 'com.dpardo.ulid:ulid:1.0.0'
}
```

Then, add the Hibernate integration module:

```gradle
dependencies {
    implementation 'com.dpardo.ulid:ulid-hibernate:1.0.0'
}
```

Or for Maven:

```xml
<dependency>
    <groupId>com.dpardo.ulid</groupId>
    <artifactId>ulid-hibernate</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

## 🏗️ Hibernate Integration Example

### ✅ Entity Example

```java
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.dpardo.ulid.ULID;

@Entity
public class ULIDEntity {

    @Id
    @GeneratedValue(generator = "ulid")
    @GenericGenerator(
        name = "ulid",
        strategy = "com.dpardo.ulid.hibernate.ULIDIdGenerator"
    )
    private ULID id;

    // Other fields...
}
```

### ⚙️ Custom Type Mapping (Optional)

If you use `@Basic` or native Hibernate mappings, you can also register the custom `TypeDescriptor`.

---

## 📚 Related Projects

- 📦 [`ulid`](https://github.com/daniel99412/ULID): Core Java ULID library (generation, parsing, validation)

---

## 📜 License

This project is licensed under the **GNU General Public License v3 (GPL-3.0)**.

---

> 💡 Designed to keep your **domain model clean** and your **persistence layer powerful**.
