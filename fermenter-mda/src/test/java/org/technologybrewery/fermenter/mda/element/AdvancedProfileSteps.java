package org.technologybrewery.fermenter.mda.element;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.util.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AdvancedProfileSteps {

    private static final Logger logger = LoggerFactory.getLogger(AdvancedProfileSteps.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private ArrayList<String> foundProfiles = new ArrayList<>();
    private File familyFile;
    private File profileFile;

    @After("@advancedProfile")
    public void cleanUp() {
        foundProfiles.clear();
        familyFile = null;
        profileFile = null;
    }

    @Given("the following profiles and related targets:")
    public void the_following_profiles_and_related_targets(List<FamilyInput> inputs) throws Throwable {
        String familyName;
        String profileName;

        // need to call this to instantiate the singleton object mapper
        JsonUtils.getObjectMapper();

        for (FamilyInput input : inputs) {
            profileName = input.getProfileName();
            familyName = input.getFamily();

            Family family = new Family();
            family.setName(familyName);
            ArrayList<ProfileReference> profileRefs = new ArrayList<>();
            ProfileReference ref = new ProfileReference();
            ref.setName(profileName);
            profileRefs.add(ref);
            family.setProfileReferences(profileRefs);
            familyFile = new File(FileUtils.getTempDirectory(), familyName + "-family.json");
            objectMapper.writeValue(familyFile, family);
            logger.info("Wrote to {}", familyFile.getAbsolutePath());
            assertTrue(familyFile.exists(), "Family not written to file!");

            Profile profile = new Profile();
            profile.setName(profileName);
            profileFile = new File(FileUtils.getTempDirectory(), profileName + "-profile.json");
            objectMapper.writeValue(profileFile, profile);
        }

    }

    @When("implementations for {string} are requested")
    public void implementations_for_are_requested(String requestedFamily) throws Throwable {
        String fileName = requestedFamily + "-family.json";
        File temp = new File(FileUtils.getTempDirectory(), fileName);
        logger.info("file to read is {}", temp);
        Family family = JsonUtils.readAndValidateJson(temp, Family.class);
        assertNotNull(family, "Could not read family file!");

        for (ProfileReference profile : family.getProfileReferences()) {
            foundProfiles.add(profile.getName());
        }
    }

    @Then("the following {string} are return")
    public void the_following_are_return(String expectedProfiles) throws Throwable {
        String[] expectedArr = expectedProfiles.split(",");
        assertEquals(expectedArr.length, foundProfiles.size());
        for (String found : foundProfiles) {
            assertTrue(expectedProfiles.contains(found));
        }
    }


}
