package com.quarkus.training.service;

import com.quarkus.training.BaseTestClass;
import com.quarkus.training.domain.Country;
import com.quarkus.training.entity.CountryEntity;
import com.quarkus.training.exception.EntityNotFoundException;
import com.quarkus.training.exception.RequestException;
import com.quarkus.training.mapping.CountryMapper;
import com.quarkus.training.repository.CountryRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@QuarkusTest
class CountryServiceTest extends BaseTestClass {

    @Inject
    CountryService countryService;

    @InjectMock
    CountryRepository countryRepository;
    
    @Inject
    CountryMapper countryMapper;

    @Test
    void testGetCountries() {
        List<Country> countries = Collections.singletonList(getCountry());
        given(countryRepository.listAll()).
                willReturn(countries.stream().map(countryMapper::fromCountry).collect(Collectors.toList()));
        List<Country> result = countryService.getCountries();
        verify(countryRepository).listAll();
        assertThat(result.size()).isEqualTo(countries.size());
        assertThat(result.get(0)).isEqualTo(countries.get(0));
    }


    @Test
    void testGetCountry() {
        // test country exists
        Country country = getCountry();
        given(countryRepository.findByNameIgnoreCase(country.getName())).
                willReturn(Optional.of(countryMapper.fromCountry(country)));
        Country result = countryService.getCountry(country.getName());
        verify(countryRepository).findByNameIgnoreCase(country.getName());
        assertThat(country).isEqualTo(result);

        // test country does not exists
        reset(countryRepository);
        given(countryRepository.findByNameIgnoreCase(country.getName()))
                .willReturn(Optional.empty());
        assertThatThrownBy(() -> countryService.getCountry(country.getName()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("country not found with name = %s", country.getName()));

    }


    @Test
    void testCreateCountry() {
        // test country does not exists
        Country country = getCountry();
        Country result = countryService.createCountry(country);
        verify(countryRepository).persist(any(CountryEntity.class));
        assertThat(country).isEqualTo(result);

        // test country exists
        reset(countryRepository);
        given(countryRepository.findByNameIgnoreCase(country.getName()))
                .willReturn(Optional.of(countryMapper.fromCountry(country)));
        assertThatThrownBy(() -> countryService.createCountry(country))
                .isInstanceOf(RequestException.class)
                .hasMessageContaining(String.format("the country with name %s is already created", country.getName()));
    }


    void testUpdateCountry() {
        // test country exists
        Country country = getCountry();
        given(countryRepository.findByNameIgnoreCase(country.getName()))
                .willReturn(Optional.of(countryMapper.fromCountry(country)));
        Country result = countryService.updateCountry(country.getName(), country);
        verify(countryRepository).persist(any(CountryEntity.class));
        assertThat(country).isEqualTo(result);

        // test country does not exists
        reset(countryRepository);
        assertThatThrownBy(() -> countryService.updateCountry(country.getName(), country))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(String.format("country not found with name = %s", country.getName()));

    }


    void testDeleteCountry() {
        // test country can be deleted
        String name = getCountry().getName();
        countryService.deleteCountry(name);
        verify(countryRepository).deleteById(name);

        // test country cannot be deleted
        reset(countryRepository);
        doThrow(RuntimeException.class).when(countryRepository).deleteById(name);
        assertThatThrownBy(() -> countryService.deleteCountry(name))
                .isInstanceOf(RequestException.class)
                .hasMessageContaining(String.format("the country with name %s cannot be deleted", name))
                .hasFieldOrPropertyWithValue("status", Response.Status.CONFLICT);

    }
}