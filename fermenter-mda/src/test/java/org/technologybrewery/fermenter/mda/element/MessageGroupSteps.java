package org.technologybrewery.fermenter.mda.element;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.aeonbits.owner.KrauseningConfigFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.DefaultModelInstanceRepository;
import org.technologybrewery.fermenter.mda.metamodel.MetamodelConfig;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceUrl;
import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;
import org.technologybrewery.fermenter.mda.metamodel.element.Message;
import org.technologybrewery.fermenter.mda.metamodel.element.MessageElement;
import org.technologybrewery.fermenter.mda.metamodel.element.MessageGroup;
import org.technologybrewery.fermenter.mda.metamodel.element.MessageGroupElement;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageGroupSteps {

    private static final MetamodelConfig config = KrauseningConfigFactory.create(MetamodelConfig.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private File messageGroupsDirectory = new File("target/temp-metadata", config.getMessageGroupsRelativePath());

    private String currentBasePackage;

    private File messageGroupFile;
    protected GenerationException encounteredException;
    protected DefaultModelInstanceRepository metamodelRepo;

    @Before("@messageGroup")
    public void setUp() {
        CommonSteps.commonSetUp();
    }

    @After("@messageGroup")
    public void cleanUp() throws Exception {
        CommonSteps.commonCleanUp();
        CommonSteps.clearMessageTracker();
    }

    @Given("a message group named {string} in {string} and the messages:")
    public void a_message_group_named_in_and_the_messages(String name, String packageValue,
            List<MessageElement> messages) throws Throwable {
        MessageGroupElement messageGroup = new MessageGroupElement();
        messageGroup.setName(name);
        messageGroup.setPackage(packageValue);
        messageGroup.getMessages().addAll(messages);

        loadMessageGroup(name, packageValue, messageGroup);
    }

    @Given("a message group named {string} in {string} and a least one valid message")
    public void a_message_group_named_in_and_a_least_one_valid_message(String name, String packageValue)
            throws Throwable {
        MessageGroupElement messageGroup = new MessageGroupElement();
        messageGroup.setName(name);
        messageGroup.setPackage(packageValue);

        loadMessageGroup(name, packageValue, messageGroup);
    }

    @When("message groups are read")
    public void message_groups_are_read() {
        try {
            ModelRepositoryConfiguration config = new ModelRepositoryConfiguration();
            config.setArtifactId("fermenter-mda");
            config.setBasePackage(currentBasePackage);
            Map<String, ModelInstanceUrl> metadataUrlMap = config.getMetamodelInstanceLocations();
            metadataUrlMap.put("fermenter-mda",
                    new ModelInstanceUrl("fermenter-mda", messageGroupsDirectory.getParentFile().toURI().toString()));

            metamodelRepo = new DefaultModelInstanceRepository(config);
            metamodelRepo.load();
            metamodelRepo.validate();

        } catch (GenerationException e) {
            encounteredException = e;
        }
    }

    @Then("a message group is returned for the name {string} in {string} and the messages:")
    public void a_message_group_is_returned_for_the_name_in_and_the_messages(String expectedName,
            String expectedPackageValue, List<MessageElement> expectedMessages) {
        MessageGroup messageGroup = metamodelRepo.getMessageGroup(expectedPackageValue, expectedName);
        assertNotNull(messageGroup, "Could not find expected message group!");

        Map<String, Message> messagesByKey = Maps.uniqueIndex(messageGroup.getMessages(), Message::getName);

        for (Message expectedMessage : expectedMessages) {
            Message foundMessage = messagesByKey.get(expectedMessage.getName());
            assertNotNull(foundMessage, "Could not find expected message!");
            assertEquals(expectedMessage.getText(), foundMessage.getText(), "Unexpected message text encountered!");
        }

    }

    @Then("the generator throws an exception about a invalid message group")
    public void the_generator_throws_an_exception_about_a_invalid_message_group() {
        assertNotNull(encounteredException, "Expected to have encountered an error!");
    }

    protected void loadMessageGroup(String name, String packageValue, MessageGroupElement messageGroup)
            throws IOException {

        messageGroupFile = new File(messageGroupsDirectory, name + ".json");
        objectMapper.writeValue(messageGroupFile, messageGroup);
        assertTrue(messageGroupFile.exists(), "Message group not written to file!");

        currentBasePackage = packageValue;
    }

    @DataTableType
    public MessageElement messageElementInputEntry(Map<String, String> entry) {
        MessageElement input = new MessageElement();
        input.setName(entry.get("name"));
        input.setText(entry.get("text"));

        return input;
    }

}
