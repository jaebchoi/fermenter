package org.technologybrewery.fermenter.mda.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TypeSteps {

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected File typeFile;
    protected Type type;
    protected GenerationException encounteredException;

    @Given("a type described by {string}, {string}, {string}")
    public void a_type_described_by(String name, String fullyQualifiedImplementation, String shortImplementation) throws Throwable {
        Type newType = new Type();
        if (StringUtils.isNotBlank(name)) {
            newType.setName(name);
        }
        if (StringUtils.isNotBlank(fullyQualifiedImplementation)) {
            newType.setFullyQualifiedImplementation(fullyQualifiedImplementation);
        }
        if (StringUtils.isNotBlank(shortImplementation)) {
            newType.setShortImplementation(shortImplementation);
        }

        typeFile = new File(FileUtils.getTempDirectory(), name + "-types.json");
        objectMapper.writeValue(typeFile, newType);
        assertTrue(typeFile.exists(), "Type not written to file!");

    }

    @When("types are read")
    public void types_are_read() {
        encounteredException = null;

        try {
            type = JsonUtils.readAndValidateJson(typeFile, Type.class);
            assertNotNull(type, "Could not read target file!");

        } catch (GenerationException e) {
            encounteredException = e;
        }

    }

    @Then("a valid type is available can be looked up name {string}")
    public void a_valid_type_is_available_can_be_looked_up_name(String expectedName) {
        assertEquals(expectedName, type.getName(), "Unexpected name encountered!");
    }
    

    @Then("the generator throws an exception about invalid type metadata")
    public void the_generator_throws_an_exception_about_invalid_type_metadata() {
        assertNotNull(encounteredException, "A GenerationException should have been thrown!");
    }    

}
