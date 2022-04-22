package com.quarkus.training.service;

import com.quarkus.training.config.MessageSource;
import com.quarkus.training.domain.Person;
import com.quarkus.training.entity.PersonEntity;
import com.quarkus.training.exception.EntityNotFoundException;
import com.quarkus.training.mapping.PersonMapper;
import com.quarkus.training.repository.CountryRepository;
import com.quarkus.training.repository.PersonRepository;
import io.quarkus.panache.common.Page;
import lombok.AllArgsConstructor;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@AllArgsConstructor
public class PersonService {

    PersonRepository personRepository;

    CountryRepository countryRepository;

    PersonMapper personMapper;

    MessageSource messageSource;

    public List<Person> getPersons(Page page) {
        return personRepository.findAll(page).map(personMapper::toPerson).collect(Collectors.toList());
    }

    public Person getPerson(Long id) {
        return personMapper.toPerson(personRepository.findByIdOptional(id).orElseThrow(() ->
                new EntityNotFoundException(messageSource.getMessage("person.notfound", id))));
    }

    public Person createPerson(Person person) {
        countryRepository.findByNameIgnoreCase(person.getCountry().getName()).orElseThrow(() ->
                new EntityNotFoundException(messageSource.getMessage("country.notfound", person.getCountry().getName())));
        person.setId(null);
        PersonEntity personEntity = personMapper.fromPerson(person);
        personRepository.persist(personEntity);
        return personMapper.toPerson(personEntity);
    }

    public Person updatePerson(Long id, Person person) {
        return personRepository.findByIdOptional(id)
                .map(entity -> {
                    countryRepository.findByNameIgnoreCase(person.getCountry().getName()).orElseThrow(() ->
                            new EntityNotFoundException(messageSource.getMessage("country.notfound", person.getCountry().getName())));
                    person.setId(id);
                    PersonEntity personEntity = personMapper.fromPerson(person);
                    personRepository.persist(personEntity);
                    return personMapper.toPerson(personEntity);
                }).orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage("person.notfound", id)));
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

}