package com.quarkus.training.service;

import com.quarkus.training.domain.Country;
import com.quarkus.training.exception.EntityNotFoundException;
import com.quarkus.training.exception.RequestException;
import com.quarkus.training.mapping.CountryMapper;
import com.quarkus.training.repository.CountryRepository;
import lombok.AllArgsConstructor;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
@AllArgsConstructor
public class CountryService {

    CountryRepository countryRepository;

    CountryMapper countryMapper;

    public List<Country> getCountries() {
        return StreamSupport.stream(countryRepository.findAll().spliterator(), false)
                .map(countryMapper::toCountry)
                .collect(Collectors.toList());
    }

    public Country getCountry(String name) {
        return countryMapper.toCountry(countryRepository.findByNameIgnoreCase(name).orElseThrow(() ->
                new EntityNotFoundException(String.format("country not found with name = %s", name)))
        );
    }

    public Country createCountry(Country country) {
        countryRepository.findByNameIgnoreCase(country.getName())
                .ifPresent(entity -> {
                    throw new RequestException(String.format("the country with name %s is already created", entity.getName()),
                            Response.Status.CONFLICT);
                });
        return countryMapper.toCountry(countryRepository.save(countryMapper.fromCountry(country)));
    }

    public Country updateCountry(String name, Country country) {
        return countryRepository.findByNameIgnoreCase(name)
                .map(entity -> {
                    country.setName(name);
                    return countryMapper.toCountry(countryRepository.save(countryMapper.fromCountry(country)));
                }).orElseThrow(() -> new EntityNotFoundException(String.format("country not found with name = %s", name)));
    }

    public void deleteCountry(String name) {
        if(countryRepository.existsById(name)) {
            try {
                countryRepository.deleteById(name);
            } catch (Exception e) {
                throw new RequestException(String.format("the country with name %s cannot be deleted", name),
                        Response.Status.CONFLICT);
            }
        }
    }

}