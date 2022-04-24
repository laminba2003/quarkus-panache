package com.quarkus.training.repository;

import com.quarkus.training.MySQLResource;
import com.quarkus.training.entity.PersonEntity;
import io.quarkus.panache.common.Page;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@QuarkusTestResource(MySQLResource.class)

class PersonRepositoryTest {

    @Inject
    PersonRepository personRepository;

    @Test
    void testFindAll() {
        List<PersonEntity> entities = personRepository.findAll(Page.of(0,2));
        assertThat(entities.size()).isEqualTo(2);
    }
}