package vn.xime.key.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.xime.key.infrastructure.persistence.repository.JpaKeyRepository;

@SpringBootTest
class KeyRepositoryTest {

    @Autowired
    private JpaKeyRepository repo;

    @Test
    void testFindCurrentKey() {
        var result = repo.findByServiceNameAndStatusAndIsDeletedFalse(
                "identity-service",
                "CURRENT"
        );

        System.out.println(result);
    }
}