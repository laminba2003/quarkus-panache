package com.quarkus.training.service;

import com.quarkus.training.config.MessageSource;
import com.quarkus.training.domain.Country;
import com.quarkus.training.entity.CountryEntity;
import com.quarkus.training.exception.EntityNotFoundException;
import com.quarkus.training.exception.RequestException;
import com.quarkus.training.mapping.CountryMapper;
import com.quarkus.training.repository.CountryRepository;
import lombok.AllArgsConstructor;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;


@ApplicationScoped
@AllArgsConstructor
public class CountryService {

    CountryRepository countryRepository;

    CountryMapper countryMapper;

    MessageSource messageSource;

    public List<Country> getCountries() {
        return countryRepository.listAll().stream()
                .map(countryMapper::toCountry)
                .collect(Collectors.toList());
    }

    public Country getCountry(String name) {
        return countryMapper.toCountry(countryRepository.findByNameIgnoreCase(name).orElseThrow(() ->
                new EntityNotFoundException(messageSource.getMessage("country.notfound", name))));
    }

    @Transactional
    public Country createCountry(Country country) {
        countryRepository.findByNameIgnoreCase(country.getName())
                .ifPresent(entity -> {
                    throw new RequestException(messageSource.getMessage("country.exists", country.getName()),
                            Response.Status.CONFLICT);
                });
        CountryEntity countryEntity = countryMapper.fromCountry(country);
        countryRepository.persist(countryEntity);
        return countryMapper.toCountry(countryEntity);
    }

    @Transactional
    public Country updateCountry(String name, Country country) {
        return countryRepository.findByNameIgnoreCase(name)
                .map(entity -> {
                    country.setName(name);
                    CountryEntity countryEntity = countryMapper.fromCountry(country);
                    countryRepository.persist(countryEntity);
                    return countryMapper.toCountry(countryEntity);
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("country.notfound", name)));
    }

    @Transactional
    public void deleteCountry(String name) {
        try {
            countryRepository.deleteById(name);
        } catch (Exception e) {
            throw new RequestException(messageSource.getMessage("country.errordeletion", name),
                    Response.Status.CONFLICT);
        }
    }

}