package org.technologybrewery.fermenter.mda.test.utils.tests;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.tuple.Pair;
import org.technologybrewery.fermenter.mda.element.ExpandedProfile;
import org.technologybrewery.fermenter.mda.element.Target;
import org.technologybrewery.fermenter.mda.test.utils.FermenterMDATestUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileSteps {
    private File profilesFile;
    private File targetsFile;
    private Map<String, ExpandedProfile> loadedProfiles = new HashMap<>();
    private Map<String, Target> loadedTargets = new HashMap<>();

    @After("@profile")
    public void cleanUp() {
        profilesFile = null;
        loadedProfiles.clear();
    }

    @Given("profiles are loaded from {string} and targets are loaded from {string}")
    public void profiles_targets_loaded(String profilePath, String targetPath) throws Throwable {
        profilesFile = new File(profilePath);
        assertTrue(profilesFile.exists(), "profiles.json file not found!");
        targetsFile = new File(targetPath);
        assertTrue(targetsFile.exists(), "targets.json file not found!");
    }

    @When("profiles and targets are read")
    public void profiles_are_read() throws IOException {
        Pair<Map<String, ExpandedProfile>, Map<String, Target>> profilesAndTarget = FermenterMDATestUtil.readProfilesAndTargetFile(profilesFile, targetsFile);
        loadedProfiles = profilesAndTarget.getLeft();
        loadedTargets = profilesAndTarget.getRight();

        assertNotNull(loadedProfiles, "Could not read profile file!");
        assertNotNull(loadedTargets, "Could not read targets file!");

    }

    @Then("a valid profile {string} is available and target references \"{listOfStrings}\" are contained")
    public void profile_contains_target_references(String expectedProfileName, List<String> targetReferences) {
        ExpandedProfile profile = loadedProfiles.get(expectedProfileName);
        assertNotNull(profile, "Could not find the profile!");
        List<String> missingTargets = FermenterMDATestUtil.retriveMissingTargets(profile,targetReferences);
        assertTrue(missingTargets.isEmpty(), "Could not find all target references specified. Missing Target References are: " + missingTargets);
    }

    @Then("a valid profile {string} is available and target reference is empty")
    public void profile_have_empty_target(String expectedProfileName) {
        ExpandedProfile profile = loadedProfiles.get(expectedProfileName);
        assertNotNull(profile, "Could not find the profile!");
        assertTrue(profile.getTargets().isEmpty(), "Expected empty target reference but found target references");
    }

    @Then("a valid profile {string} is available and other profile references \"{listOfStrings}\" are contained")
    public void profile_contains_profile_references(String expectedProfileName, List<String> profileReferences) {
        ExpandedProfile profile = loadedProfiles.get(expectedProfileName);
        assertNotNull(profile, "Could not find the profile!");
        List<String> missingProfiles = FermenterMDATestUtil.retrieveMissingProfiles(profile, profileReferences);
        assertTrue(missingProfiles.isEmpty(), "Could not find all profile references specified. Missing Profile References are : " + missingProfiles);
    }

    @Then("a valid profile {string} is available and other profile reference is empty")
    public void profile_have_empty_profile_ref(String expectedProfileName) {
        ExpandedProfile profile = loadedProfiles.get(expectedProfileName);
        assertNotNull(profile, "Could not find the profile!");
        assertTrue(profile.getReferencedProfiles().isEmpty(), "Expected empty profile reference but found profile references");
    }

    @Then("a valid profile {string} is available and profile is not deprecated")
    public void profile_not_deprecated(String expectedProfileName) {
        ExpandedProfile profile = loadedProfiles.get(expectedProfileName);
        assertNotNull(profile, "Could not find the profile!");
        assertFalse(profile.isDeprecated(), "Profile is deprecated");
    }

    @Then("a valid profile {string} is available and profile is deprecated")
    public void profile_deprecated(String expectedProfileName) {
        ExpandedProfile profile = loadedProfiles.get(expectedProfileName);
        assertNotNull(profile, "Could not find the profile!");
        assertTrue(profile.isDeprecated(), "Profile is not deprecated");
    }

}
