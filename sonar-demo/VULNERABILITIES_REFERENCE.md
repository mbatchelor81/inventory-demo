# Intentional Vulnerabilities Reference

This document describes the exact vulnerabilities that were introduced for demo purposes and how to reintroduce them.

## SonarQube Scan Results

**Quality Gate Status:** ERROR

**Failed Conditions:**
- `new_reliability_rating`: ERROR (4 vs threshold 1)
- `new_security_rating`: ERROR (5 vs threshold 1)
- `new_coverage`: ERROR (3.2% vs threshold 80%)
- `new_security_hotspots_reviewed`: ERROR (0% vs threshold 100%)

---

## Vulnerability #1: SQL Injection (BLOCKER)

**File:** `backend/src/main/java/com/example/inventory_service_demo/service/ProductService.java`  
**Line:** 78  
**SonarQube Rule:** `javasecurity:S3649`  
**Severity:** BLOCKER  
**Message:** "Change this code to not construct SQL queries directly from user-controlled data."

### Vulnerable Code:
```java
// INTENTIONAL VULNERABILITY: SQL Injection via string concatenation
@SuppressWarnings("unchecked")
public List<Product> searchProducts(String searchTerm) {
    // Vulnerable: User input directly concatenated into SQL query
    String sql = "SELECT * FROM product WHERE name LIKE '%" + searchTerm + "%'";
    return entityManager.createNativeQuery(sql, Product.class).getResultList();
}
```

### Fixed Code:
```java
// Fixed: Using parameterized query to prevent SQL injection
@SuppressWarnings("unchecked")
public List<Product> searchProducts(String searchTerm) {
    String sql = "SELECT * FROM product WHERE name LIKE :searchTerm";
    return entityManager.createNativeQuery(sql, Product.class)
            .setParameter("searchTerm", "%" + searchTerm + "%")
            .getResultList();
}
```

### How to Reintroduce:
Replace the parameterized query with direct string concatenation in the SQL statement.

---

## Vulnerability #2: Insecure Random Number Generation (CRITICAL)

**File:** `backend/src/main/java/com/example/inventory_service_demo/service/ProductService.java`  
**Line:** 104  
**SonarQube Rule:** `java:S2119`  
**Severity:** CRITICAL  
**Message:** "Save and re-use this 'Random'."

### Vulnerable Code:
```java
import java.util.Random;

// In class body - NO field declaration

// INTENTIONAL VULNERABILITY #5: Insecure Random - Using predictable Random
@SuppressWarnings("java:S2245")
public String generateProductCode() {
    // Vulnerable: java.util.Random is predictable and not cryptographically secure
    Random random = new Random();
    int code = random.nextInt(999999);
    return String.format("PROD-%06d", code);
}
```

### Fixed Code:
```java
import java.security.SecureRandom;

// In class body:
private final SecureRandom secureRandom = new SecureRandom();

// Fixed: Using SecureRandom as a reusable instance for cryptographically secure random numbers
public String generateProductCode() {
    int code = secureRandom.nextInt(999999);
    return String.format("PROD-%06d", code);
}
```

### How to Reintroduce:
1. Change import from `java.security.SecureRandom` to `java.util.Random`
2. Remove the `secureRandom` field declaration
3. Create a new `Random()` instance inside the method
4. Add the `@SuppressWarnings("java:S2245")` annotation

---

## Vulnerability #3: Generic Exception (MAJOR)

**File:** `backend/src/main/java/com/example/inventory_service_demo/service/ProductService.java`  
**Line:** 96  
**SonarQube Rule:** `java:S112`  
**Severity:** MAJOR  
**Message:** "Replace generic exceptions with specific library exceptions or a custom exception."

### Vulnerable Code:
```java
// INTENTIONAL VULNERABILITY #2: Weak Cryptography - Using MD5 for hashing
@SuppressWarnings("java:S4790")
public String generateProductHash(String productData) {
    try {
        // Vulnerable: MD5 is cryptographically broken and should not be used
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(productData.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException("MD5 algorithm not found", e);
    }
}
```

### Fixed Code:
```java
// Create custom exception class:
// File: backend/src/main/java/com/example/inventory_service_demo/exception/HashGenerationException.java
package com.example.inventory_service_demo.exception;

public class HashGenerationException extends RuntimeException {
    public HashGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// In ProductService.java:
import com.example.inventory_service_demo.exception.HashGenerationException;

// In method:
} catch (NoSuchAlgorithmException e) {
    throw new HashGenerationException("MD5 algorithm not found", e);
}
```

### How to Reintroduce:
1. Delete the `HashGenerationException.java` file
2. Remove the import for `HashGenerationException`
3. Replace `throw new HashGenerationException(...)` with `throw new RuntimeException(...)`

---

## Vulnerability #4: Unused Import (MINOR)

**File:** `backend/src/main/java/com/example/inventory_service_demo/controller/ProductController.java`  
**Line:** 15  
**SonarQube Rule:** `java:S1128`  
**Severity:** MINOR  
**Message:** "Remove this unused import 'java.nio.file.Paths'."

### Vulnerable Code:
```java
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;  // <-- Unused import
```

### Fixed Code:
```java
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
```

### How to Reintroduce:
Add `import java.nio.file.Paths;` after the `Files` import.

---

## Vulnerability #5: Empty Test Method (CRITICAL)

**File:** `backend/src/test/java/com/example/inventory_service_demo/InventoryServiceDemoApplicationTests.java`  
**Line:** 10  
**SonarQube Rule:** `java:S1186`  
**Severity:** CRITICAL  
**Message:** "Add a nested comment explaining why this method is empty, throw an UnsupportedOperationException or complete the implementation."

### Vulnerable Code:
```java
package com.example.inventory_service_demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryServiceDemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
```

### Fixed Code:
```java
package com.example.inventory_service_demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class InventoryServiceDemoApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Verify that the Spring application context loads successfully
		assertNotNull(applicationContext, "Application context should not be null");
	}

}
```

### How to Reintroduce:
1. Remove the `@Autowired` field and imports
2. Remove the `assertNotNull` statement and its import
3. Leave the `contextLoads()` method body empty

---

## Additional Vulnerabilities (Not Flagged by SonarQube)

These vulnerabilities exist in the code but were suppressed or not detected:

### Path Traversal Vulnerability
**File:** `ProductController.java` (lines 88-104)
```java
// INTENTIONAL VULNERABILITY #3: Path Traversal - Unsafe file access
@SuppressWarnings("java:S2083")
@GetMapping("/export/{filename}")
public ResponseEntity<String> exportProductData(@PathVariable String filename) {
    try {
        // Vulnerable: User-controlled filename without validation allows directory traversal
        String filePath = "/tmp/exports/" + filename;
        File file = new File(filePath);
        String content = new String(Files.readAllBytes(file.toPath()));
        return ResponseEntity.ok(content);
    } catch (IOException e) {
        // INTENTIONAL VULNERABILITY #4: Information Disclosure - Exposing internal details
        // Vulnerable: Exposing full exception details and internal file paths to users
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error reading file: " + e.getMessage() + 
                      "\nStack trace: " + e.getStackTrace()[0].toString());
    }
}
```

### Weak Cryptography (MD5)
**File:** `ProductService.java` (lines 82-98)
- Uses MD5 hashing which is cryptographically broken
- Suppressed with `@SuppressWarnings("java:S4790")`

---

## Quick Reintroduction Checklist

1. ✅ **SQL Injection**: Replace parameterized query with string concatenation
2. ✅ **Insecure Random**: Use `new Random()` inside method instead of `SecureRandom` field
3. ✅ **Generic Exception**: Delete `HashGenerationException.java` and use `RuntimeException`
4. ✅ **Unused Import**: Add `import java.nio.file.Paths;` to `ProductController.java`
5. ✅ **Empty Test**: Remove test body content and assertions

---

## Notes

- All vulnerabilities were marked as `INTENTIONAL` with author `masonbatchelor81@gmail.com`
- Created dates range from 2025-06-12 to 2025-10-16
- These vulnerabilities are for **demonstration purposes only**
- Never deploy code with these vulnerabilities to production
