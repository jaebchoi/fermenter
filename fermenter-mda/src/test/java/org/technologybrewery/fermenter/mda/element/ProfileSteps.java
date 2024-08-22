package org.technologybrewery.fermenter.mda.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ProfileSteps {

    private ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, Target> targetMap = new HashMap<>();
    private Map<String, ExpandedProfile> profileMap = new HashMap<>();
    private File profileFile;
    private GenerationException encounteredException;
    private ExpandedProfile profile;

    @After("@profile")
    public void cleanUp() {
        targetMap.clear();
        profileMap.clear();
        profileFile = null;
        encounteredException = null;
        profile = null;
    }

    @Given("the following targets")
    public void the_following_targets(List<Target> targets) {
        for (Target t : targets) {
            targetMap.put(t.getName(), t);
        }
    }

    @Given("a profile described by {string}, \"{listOfStrings}\", \"{listOfStrings}\"")
    public void a_profile_described_by(String name, List<String> directlyReferencedTargets,
            List<String> directlyIncludedProfiles) throws Throwable {

        Profile p = new Profile();
        if (StringUtils.isNotBlank(name)) {
            p.setName(name);
        }

        List<TargetReference> targetReferences = new ArrayList<>();
        for (String targetName : directlyReferencedTargets) {
            TargetReference targetReference = new TargetReference();
            targetReference.setName(targetName);
            targetReferences.add(targetReference);
        }
        p.setTargetReferences(targetReferences);

        List<ProfileReference> profileReferences = new ArrayList<>();
        for (String profileName : directlyIncludedProfiles) {
            ProfileReference profileReference = new ProfileReference();
            profileReference.setName(profileName);
            profileReferences.add(profileReference);
        }
        p.setProfileReferences(profileReferences);

        if (profileReferences.isEmpty()) {
            // add this to the expanded profiles so it can be dereferenced later for profile inclusion testing:
            profileMap.put(p.getName(), new ExpandedProfile(p));
        }

        assertEquals(directlyReferencedTargets.size(), p.getTargetReferences().size());

        profileFile = new File(FileUtils.getTempDirectory(), name + "-profile.json");
        objectMapper.writeValue(profileFile, p);
        assertTrue(profileFile.exists(), "Profile not written to file!");
    }

    @When("profiles are read")
    public void profiles_are_read() {
        encounteredException = null;

        try {
            Profile profileMetadata = JsonUtils.readAndValidateJson(profileFile, Profile.class);
            assertNotNull(profileMetadata, "Could not read profile file!");

            profile = new ExpandedProfile(profileMetadata);
            profileMap.put(profile.getName(), profile);
            profile.dereference(profileMap, targetMap);

        } catch (GenerationException e) {
            encounteredException = e;
        }
    }

    @Then("a valid profile is available as {string} that contains {int} targets")
    public void a_valid_profile_is_available_as_that_contains_targets(String expectedName, int expectedTargetCount) {
        if (encounteredException != null) {
            throw encounteredException;
        }
        assertEquals(expectedName, profile.getName(), "Profile name was not matching!");
        assertEquals(expectedTargetCount, profile.getTargets().size(), "Profile did not have the expected number of targets!");
    }

    @Then("a valid EMPTY profile is available as {string}")
    public void a_valid_EMPTY_profile_is_available_as(String expectedName) {
        a_valid_profile_is_available_as_that_contains_targets(expectedName, 0);
    }

    @Then("the generator throws an exception about invalid profile metadata")
    public void the_generator_throws_an_exception_about_invalid_profile_metadata() {
        assertNotNull(encounteredException, "A GenerationException should have been thrown!");
    }


}
