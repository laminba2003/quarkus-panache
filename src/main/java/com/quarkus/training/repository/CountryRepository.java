package com.quarkus.training.repository;

import com.quarkus.training.entity.CountryEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class CountryRepository implements PanacheRepositoryBase<CountryEntity, String> {

    public Optional<CountryEntity> findByNameIgnoreCase(String name) {
        return this.find("lower(name) =?1", name).firstResultOptional();
    }

}
