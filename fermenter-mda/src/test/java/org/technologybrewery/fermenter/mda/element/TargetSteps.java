package org.technologybrewery.fermenter.mda.element;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TargetSteps {

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected File targetFile;
    protected Target target;
    protected GenerationException encounteredException;

    @Given("a target described by {string}, {string}, {string}, {string}, {string}, {string}")
    public void a_target_described_by(String name, String generator, String templateName, String outputFile,
            String overwritable, String artifactType) throws Throwable {
        Target newTarget = new Target();
        if (StringUtils.isNotBlank(name)) {
            newTarget.setName(name);
        }
        if (StringUtils.isNotBlank(generator)) {
            newTarget.setGenerator(generator);
        }
        if (StringUtils.isNotBlank(templateName)) {
            newTarget.setTemplateName(templateName);
        }
        if (StringUtils.isNotBlank(outputFile)) {
            newTarget.setOutputFile(outputFile);
        }
        if (StringUtils.isNotBlank(overwritable)) {
            newTarget.setOverwritable(Boolean.parseBoolean(overwritable));
        }
        if (StringUtils.isNotBlank(artifactType)) {
            newTarget.setArtifactType(artifactType);
        }

        targetFile = new File(FileUtils.getTempDirectory(), templateName + "-target.json");
        objectMapper.writeValue(targetFile, newTarget);
        assertTrue(targetFile.exists(), "Target not written to file!");

    }

    @Given("a target described with without an artifact type value")
    public void a_target_described_with_without_an_artifact_type_value() throws Throwable {
        a_target_described_by("testArtifactTypeDefaulting", "o.b.c.f.FooGenerator", "template.java.vm", "SomeFile.Java",
                Boolean.TRUE.toString(), null);
    }

    @When("targets are read")
    public void targets_are_read() {
        encounteredException = null;

        try {
            target = JsonUtils.readAndValidateJson(targetFile, Target.class);
            assertNotNull(target, "Could not read target file!");

        } catch (GenerationException e) {
            encounteredException = e;
        }

    }

    @Then("a valid target is available can be looked up name {string}")
    public void a_valid_target_is_available_can_be_looked_up_name(String expectedName) {
        assertEquals(expectedName, target.getName());
    }

    @Then("the generator throws an exception about invalid metadata")
    public void the_generator_throws_an_exception_about_invalid_metadata() {
        assertNotNull(encounteredException, "A GenerationException should have been thrown!");
    }

    @Then("a valid target is available and has an artifact type of {string}")
    public void a_valid_target_is_available_and_has_an_artifact_type_of(String expectedArtifactType) {
        assertEquals(expectedArtifactType, target.getArtifactType());
    }

    @DataTableType
    public Target targetEntry(Map<String, String> entry) {
        Target input = new Target();
        input.setName(entry.get("name"));
        input.setGenerator(entry.get("generator"));
        input.setTemplateName(entry.get("templateName"));
        input.setOutputFile(entry.get("outputFile"));
        input.setOverwritable(Boolean.parseBoolean(entry.get("overwritable")));

        return input;
    }

}
