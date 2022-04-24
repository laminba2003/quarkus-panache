package com.quarkus.training.repository;

import com.quarkus.training.MySQLResource;
import com.quarkus.training.entity.CountryEntity;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(MySQLResource.class)
class CountryRepositoryTest {

    @Inject
    CountryRepository countryRepository;

    @Test
    void testfindByNameIgnoreCase() {
        Optional<CountryEntity> entity = countryRepository.findByNameIgnoreCase("france");
        assertThat(entity.get()).isNotNull();
        assertThat(entity.get().getName()).isEqualTo("France");
    }
}