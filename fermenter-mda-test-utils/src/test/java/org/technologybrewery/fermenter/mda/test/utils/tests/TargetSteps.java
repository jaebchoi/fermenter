package org.technologybrewery.fermenter.mda.test.utils.tests;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.technologybrewery.fermenter.mda.GenerateSourcesHelper;
import org.technologybrewery.fermenter.mda.element.Target;
import org.technologybrewery.fermenter.mda.test.utils.FermenterMDATestUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TargetSteps {
    private File targetFile;
    private Map<String, Target> loadedTargets = new HashMap<>();
    @After("@target")
    public void cleanUp() {
        targetFile = null;
        loadedTargets.clear();
    }

    @Given("targets are loaded from {string}")
    public void targets_loaded(String targetPath) {
        targetFile = new File(targetPath);
        assertTrue(targetFile.exists(), "targets.json does not exist!");
    }

    @When("targets are read")
    public void targets_are_read() throws IOException {
        InputStream targetStream = new FileInputStream(targetFile);
        loadedTargets = GenerateSourcesHelper.loadTargets(targetStream, loadedTargets);
        assertNotNull(loadedTargets, "Could not read targets.json file!");
    }

    @Then("a valid target {string} is available")
    public void targets_contain_specified_target(String expectedTargetName) {
        Target target = loadedTargets.get(expectedTargetName);
        assertNotNull(target, "Could not find target specified");
    }

    @Then("an invalid target {string} is not available")
    public void targets_does_not_contain_specified_target(String expectedTargetName) {
        Target target = loadedTargets.get(expectedTargetName);
        assertNull(target, "Found the invalid target specified");
    }

    @Then("a valid target {string} is available and valid template file exists")
    public void targets_contains_template_file(String expectedTargetName) {
        Target target = loadedTargets.get(expectedTargetName);
        assertNotNull(target, "Could not find the target!");
        String baseDirectory = "src/test/resources/";
        assertTrue(FermenterMDATestUtil.isTemplateFileExist(target, baseDirectory), "Template File Does not Exist!");
    }

    @Then("a valid target {string} is available and invalid template file does not exist")
    public void targets_does_not_contains_template_file(String expectedTargetName) {
        Target target = loadedTargets.get(expectedTargetName);
        assertNotNull(target, "Could not find the target!");
        String baseDirectory = "src/test/resources/";
        assertFalse(FermenterMDATestUtil.isTemplateFileExist(target, baseDirectory), "Invalid Template File Does not Exist!");
    }

    @Then("a valid target {string} is available and valid generator file exists")
    public void target_contains_generator_file(String expectedTargetName) {
        Target target = loadedTargets.get(expectedTargetName);
        assertNotNull(target, "Could not find the target!");
        assertTrue(FermenterMDATestUtil.isGeneratorOnClassPath(target), "Generator File Does not Exist!");
    }

    @Then("a valid target {string} is available and invalid generator file does not exist")
    public void target_does_not_contain_generator_file(String expectedTargetName) {
        Target target = loadedTargets.get(expectedTargetName);
        assertNotNull(target, "Could not find the target!");
        assertFalse(FermenterMDATestUtil.isGeneratorOnClassPath(target), "Invalid Generator File Exist!");
    }

}
