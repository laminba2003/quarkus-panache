package com.quarkus.training.controller;

import com.quarkus.training.BaseTestClass;
import com.quarkus.training.domain.Person;
import com.quarkus.training.service.PersonService;
import io.quarkus.panache.common.Page;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.restdocs.ManualRestDocumentation;
import java.util.Collections;
import java.util.List;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@QuarkusTest
@TestHTTPEndpoint(value = PersonController.class)
@QuarkusTestResource(OidcWiremockTestResource.class)
class PersonControllerTest extends BaseTestClass {

    @InjectMock
    PersonService personService;

    RequestSpecification specification;
    ManualRestDocumentation restDocumentation;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        restDocumentation = new ManualRestDocumentation("target/snippets/persons");
        specification = new RequestSpecBuilder().
                addFilter(documentationConfiguration(restDocumentation)).build();
        restDocumentation.beforeTest(getClass(), testInfo.getTestMethod().get().getName());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    public void tearDown() {
        this.restDocumentation.afterTest();
    }

    @Test
    void testGetPersons() {
        List<Person> persons = Collections.singletonList(getPerson());
        given(personService.getPersons(any(Page.class))).willReturn(persons);
        RestAssured.given(specification).auth().oauth2(getToken())
                .queryParam("page", 1)
                .queryParam("size", 5)
                .accept(ContentType.JSON)
                .filter(document("getPersons"))
                .when().get()
                .then()
                .contentType(ContentType.JSON)
                .statusCode(OK.getStatusCode())
                .body("id[0]", equalTo((persons.get(0).getId().intValue())))
                .body("firstName[0]", equalTo(persons.get(0).getFirstName()))
                .body("lastName[0]", equalTo(persons.get(0).getLastName()))
                .body("country[0].name", equalTo(persons.get(0).getCountry().getName()))
                .body("country[0].capital", equalTo(persons.get(0).getCountry().getCapital()))
                .body("country[0].population", equalTo(persons.get(0).getCountry().getPopulation()));
    }

    @Test
    void testGetPerson() {
        Person person = getPerson();
        given(personService.getPerson(person.getId())).willReturn(person);
        RestAssured.given(specification).auth().oauth2(getToken())
                .accept(ContentType.JSON)
                .pathParam("id", person.getId())
                .filter(document("getPerson"))
                .when().get("{id}")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(OK.getStatusCode())
                .body("id", equalTo((person.getId().intValue())))
                .body("firstName", equalTo(person.getFirstName()))
                .body("lastName", equalTo(person.getLastName()))
                .body("country.name", equalTo(person.getCountry().getName()))
                .body("country.capital", equalTo(person.getCountry().getCapital()))
                .body("country.population", equalTo(person.getCountry().getPopulation()));
    }

    @Test
    void testCreatePerson() {
        Person person = getPerson();
        given(personService.createPerson(person)).willReturn(person);
        RestAssured.given(specification).auth().oauth2(getToken())
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(person)
                .filter(document("createPerson"))
                .when().post()
                .then()
                .contentType(ContentType.JSON)
                .statusCode(CREATED.getStatusCode())
                .body("id", equalTo((person.getId().intValue())))
                .body("firstName", equalTo(person.getFirstName()))
                .body("lastName", equalTo(person.getLastName()))
                .body("country.name", equalTo(person.getCountry().getName()))
                .body("country.capital", equalTo(person.getCountry().getCapital()))
                .body("country.population", equalTo(person.getCountry().getPopulation()));
    }

    @Test
    void testUpdatePerson() {
        Person person = getPerson();
        given(personService.updatePerson(person.getId(), person)).willReturn(person);
        RestAssured.given(specification).auth().oauth2(getToken())
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(person)
                .pathParam("id", person.getId())
                .filter(document("updatePerson"))
                .when().put("{id}")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(OK.getStatusCode())
                .body("id", equalTo((person.getId().intValue())))
                .body("firstName", equalTo(person.getFirstName()))
                .body("lastName", equalTo(person.getLastName()))
                .body("country.name", equalTo(person.getCountry().getName()))
                .body("country.capital", equalTo(person.getCountry().getCapital()))
                .body("country.population", equalTo(person.getCountry().getPopulation()));
    }

    @Test
    void testDeletePerson() {
        Person person = getPerson();
        doNothing().when(personService).deletePerson(person.getId());
        RestAssured.given(specification).auth().oauth2(getToken())
                .pathParam("id", person.getId())
                .filter(document("deletePerson"))
                .when().delete("{id}")
                .then()
                .statusCode(OK.getStatusCode());
    }

}