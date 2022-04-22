package com.quarkus.training.repository;

import com.quarkus.training.entity.PersonEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Stream;

@ApplicationScoped
public class PersonRepository implements PanacheRepository<PersonEntity> {

    public Stream<PersonEntity> findAll(Page page) {
        return findAll().page(page).stream();
    }

}