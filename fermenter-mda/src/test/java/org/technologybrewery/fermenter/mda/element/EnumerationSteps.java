package org.technologybrewery.fermenter.mda.element;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.DefaultModelInstanceRepository;
import org.technologybrewery.fermenter.mda.metamodel.MetamodelConfig;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceUrl;
import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;
import org.technologybrewery.fermenter.mda.metamodel.element.Enum;
import org.technologybrewery.fermenter.mda.metamodel.element.EnumElement;
import org.technologybrewery.fermenter.mda.metamodel.element.Enumeration;
import org.technologybrewery.fermenter.mda.metamodel.element.EnumerationElement;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnumerationSteps {
    
    private static final MetamodelConfig config = KrauseningConfigFactory.create(MetamodelConfig.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private File enumerationsDirectory = new File("target/temp-metadata", config.getEnumerationsRelativePath());

    private String currentBasePackage;
    
    private File enumerationFile;
    private Enumeration loadedEnumeration;
    protected GenerationException encounteredException;
    protected DefaultModelInstanceRepository metadataRepo;

    @Before("@enumeration")
    public void setUp() {
        CommonSteps.performCommonBeforeTasks();
    }

    @After("@enumeration")
    public void cleanUp() throws Throwable {
        loadedEnumeration = null;
        currentBasePackage = null;
        CommonSteps.performCommonAfterTasks();
    }

    @Given("an enumeration named {string} in {string} and enum constants \"{listOfStrings}\"")
    public void an_enumeration_named_in_and_enum_constants(String name, String packageValue, List<String> listOfStrings)
            throws Throwable {
        createEnumerations(name, packageValue, listOfStrings, null);
    }

    private void createEnumerations(String name, String packageValue, List<String> constantNames,
            List<Integer> constantValues) throws IOException {
        EnumerationElement enumeration = new EnumerationElement();
        enumeration.setName(name);
        enumeration.setPackage(packageValue);

        int index = 0;
        for (String constant : constantNames) {
            EnumElement newEnumConstant = new EnumElement();
            newEnumConstant.setName(constant);
            if (constantValues != null) {
                newEnumConstant.setValue(constantValues.get(index));
            }
            enumeration.addEnums(newEnumConstant);

            index++;
        }

        enumerationsDirectory.mkdirs();
        enumerationFile = new File(enumerationsDirectory, name + ".json");
        objectMapper.writeValue(enumerationFile, enumeration);
        assertTrue(enumerationFile.exists(), "Enumeration not written to file!");
        
        currentBasePackage = packageValue;
    }

    @Given("an enumeration named {string} in {string} and enum constant names \"{listOfStrings}\" and values \"{listOfIntegers}\"")
    public void an_enumeration_named_in_and_enum_constant_names_and_values(String name, String packageValue,
            List<String> constantNames, List<Integer> constantValues) throws Throwable {
        createEnumerations(name, packageValue, constantNames, constantValues);
    }
    
    @Given("an enumeration named {string} in {string}")
    public void an_enumeration_named_in(String name, String fileName) throws Throwable {        
    	final String localPackage = "default.package";
    	EnumerationElement enumeration = new EnumerationElement();
        enumeration.setName(name);
        enumeration.setPackage(localPackage);

        EnumElement newEnumConstant = new EnumElement();
        newEnumConstant.setName(RandomStringUtils.randomAlphabetic(3));
        enumeration.addEnums(newEnumConstant);

        enumerationsDirectory.mkdirs();
        enumerationFile = new File(enumerationsDirectory, fileName);
        objectMapper.writeValue(enumerationFile, enumeration);
        assertTrue(enumerationFile.exists(), "Enumeration not written to file!");
        
        currentBasePackage = localPackage;
    	
    }


    @When("enumerations are read")
    public void enumerations_are_read() {
        encounteredException = null;

        try {
            ModelRepositoryConfiguration config = new ModelRepositoryConfiguration();
            config.setArtifactId("fermenter-mda");
            config.setBasePackage(currentBasePackage);
            Map<String, ModelInstanceUrl> metadataUrlMap = config.getMetamodelInstanceLocations();
            metadataUrlMap.put("fermenter-mda", new ModelInstanceUrl("fermenter-mda", enumerationsDirectory.getParentFile().toURI().toString()));

            metadataRepo = new DefaultModelInstanceRepository(config);
            metadataRepo.load();
            metadataRepo.validate();

        } catch (GenerationException e) {
            encounteredException = e;
        }
    }

    @Then("an enumeration metamodel instance is returned for the name {string} in {string} with the enum constants \"{listOfStrings}\"")
    public void an_enumeration_metamodel_instance_is_returned_for_the_name_in_with_the_enum_constants(String name,
            String packageName, List<String> constants) {
        validateLoadedConstants(name, packageName, constants, null);
    }

    @Then("NO enumeration metamodel instance is returned for the name {string} in {string}")
    public void no_enumeration_metamodel_instance_is_returned_for_the_name_in(String name, String packageName) {
        if (encounteredException != null) {
            throw encounteredException;
        }

        Map<String, Enumeration> packageEnumerations = metadataRepo.getEnumerations(packageName);
        loadedEnumeration = (packageEnumerations != null) ? packageEnumerations.get(name) : null;
        assertNull(loadedEnumeration);
    }

    @Then("an enumeration metamodel instance is returned for the name {string} in {string} with the enum constants \"{listOfStrings}\" and matching values \"{listOfIntegers}\"")
    public void an_enumeration_metamodel_instance_is_returned_for_the_name_in_with_the_enum_constants_and_matching_values(
            String name, String packageName, List<String> constantNames, List<Integer> constantValues) {
        validateLoadedConstants(name, packageName, constantNames, constantValues);
    }

    @Then("the enumeration is of type {string}")
    public void the_enumeration_is_of_type(String enumerationType) {
        if ("named".equalsIgnoreCase(enumerationType)) {
            assertTrue(loadedEnumeration.isNamed(), "Should have been a named enumeration!");
            assertFalse(loadedEnumeration.isValued(), "Should have been a named enumeration!");
            
        } else {
            assertFalse(loadedEnumeration.isNamed(), "Should have been a valued enumeration!");
            assertTrue(loadedEnumeration.isValued(), "Should have been a valued enumeration!");
            
        }
    }
    
    @Then("an error is returned")
    public void an_error_is_returned() {
    	assertNotNull(encounteredException, "Expected at least on error!");
    	
    }

    private void validateLoadedConstants(String name, String packageName, List<String> constantNames,
            List<Integer> constantValues) {
        if (encounteredException != null) {
            throw encounteredException;
        }

        loadedEnumeration = metadataRepo.getEnumerations(packageName).get(name);
        assertEquals(name, loadedEnumeration.getName(), "Unexpected enumeration name!");
        assertEquals(packageName, loadedEnumeration.getPackage(), "Unexpected enumeration package!");

        List<Enum> loadedConstants = loadedEnumeration.getEnums();
        assertEquals(constantNames.size(), loadedConstants.size(), "Did not find the expected number of enum constants!");
        Map<String, Enum> loadedConstantMap = loadedConstants.stream().collect(Collectors.toMap(Enum::getName, x -> x));

        int index = 0;
        for (String constant : constantNames) {
            Enum loadedConstantInstance = loadedConstantMap.get(constant);
            assertNotNull(loadedConstantMap.get(constant), "Could not find enum constant " + constant + "!");
            if (constantValues != null) {
                assertEquals(constantValues.get(index), loadedConstantInstance.getValue(), "Constant value unexpected!");
            }

            index++;
        }
    }

}
