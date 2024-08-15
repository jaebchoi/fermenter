package org.technologybrewery.fermenter.mda.notification;

import com.google.common.collect.Sets;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.maven.execution.MavenSession;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.technologybrewery.fermenter.mda.MojoTestCaseWrapper;
import org.technologybrewery.fermenter.mda.generator.AbstractGenerator;
import org.technologybrewery.fermenter.mda.generator.GenerationContext;
import org.technologybrewery.fermenter.mda.generator.TestGenerator;
import org.technologybrewery.fermenter.mda.generator.TestMultipleNotificationGenerator;
import org.technologybrewery.fermenter.mda.generator.TestSpecificNotificationGenerator;
import org.technologybrewery.fermenter.mda.generator.TestSuppressedNotificationGenerator;
import org.technologybrewery.fermenter.mda.reporting.StatisticsService;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateNotificationSteps {

    public static final String APPLE_RECORDS = "apple records";
    private List<TestGenerator> generators;
    private StatisticsService statisticsService;
    private VelocityEngine engine;
    private MojoTestCaseWrapper testCase;

    @Before("@createNotifications")
    public void setup() throws Exception {
        generators = new ArrayList<>();
        testCase = new MojoTestCaseWrapper();
        testCase.configurePluginTestHarness();
        MavenSession session = testCase.newMavenSession();
        statisticsService = new StatisticsService(session);

        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        engine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        engine.init();
    }

    @After("@createNotifications")
    public void cleanUp() throws Exception {
        for (TestGenerator eachGenerator : generators) {
            Files.deleteIfExists(eachGenerator.getTemplatePath());
            Files.deleteIfExists(eachGenerator.getOutputPath());
        }

        NotificationCollector.cleanup();

        testCase.tearDownPluginTestHarness();
    }

    @Given("a configuration that triggers {int} notification for manual action to be taken in {int}")
    public void a_configuration_that_triggers_notification_for_manual_action_to_be_taken_in(int notificationsPerFile, int numberOfFiles) {
        for (int i = 0; i < numberOfFiles; i++) {
            TestMultipleNotificationGenerator generator = new TestMultipleNotificationGenerator(numberOfFiles, notificationsPerFile);
            generators.add(generator);
        }
    }

    @Given("a configuration that triggers a notification for manual action with a {string}, {string}, and programmatic value")
    public void a_configuration_that_triggers_a_notification_for_manual_action_with_a_and_programmatic_value(String key, String items) {
        String[] itemArray = items.split(",");
        Set<String> itemSet = Sets.newHashSet(itemArray);

        TestSpecificNotificationGenerator generator = new TestSpecificNotificationGenerator(key, itemSet, APPLE_RECORDS);
        generators.add(generator);
    }

    @Given("a notification key to suppress")
    public void a_notification_key_to_suppress() throws Throwable {
        MavenSession session = testCase.newMavenSession();
        TestSuppressedNotificationGenerator generator = new TestSuppressedNotificationGenerator(List.of("test-message-id"), session);
        generators.add(generator);
    }

    @When("the MDA plugin runs")
    public void the_MDA_plugin_runs() {
        GenerationContext context = new GenerationContext();
        context.setEngine(engine);
        context.setStatisticsService(statisticsService);
        for (AbstractGenerator eachGenerator : generators) {
            eachGenerator.generate(context);
        }
    }

    @Then("{int} are registered for output for {int} files")
    public void are_registered_for_output_for_files(int notificationsPerFile, int numberOfFiles) {
        Map<String, Map<String, Notification>> notificationsByFilenameMap = getNotificationByFilename();
        assertEquals(numberOfFiles, notificationsByFilenameMap.size(), "Unexpected number of notification files found!");

        for (Map<String, Notification> notificationMap : notificationsByFilenameMap.values()) {
            assertEquals(notificationsPerFile, notificationMap.size(), "Unexpected number of notifications found!");

            for (Notification notification : notificationMap.values()) {
                assertTrue(notification.getNotificationAsString().contains(notification.getKey()),
                    String.format("Unexpected output in notification message: %s", notification.getNotificationAsString()));
            }
        }
    }

    @Then("the resulting message contains the {string}, {string}, and programmatic value")
    public void the_resulting_message_contains_the_and_programmatic_value(String expectedKey, String expectedItems) {
        Map<String, Map<String, Notification>> notificationsByFilenameMap = getNotificationByFilename();

        Map<String, Notification> notificationMap = notificationsByFilenameMap.values().iterator().next();
        assertNotNull(notificationMap, "Notifications should have been found!");

        Notification notification = notificationMap.values().iterator().next();
        assertNotNull(notification, "Notification should have been found!");

        String notificationMessage = notification.getNotificationAsString();

        assertTrue(notificationMessage.contains(expectedKey), "Key was not found in message!");
        for (String expectedInsert : expectedItems.split(",")) {
            assertTrue(notificationMessage.contains(expectedInsert), "Item was not found in message!");
        }
        assertTrue(notificationMessage.contains(APPLE_RECORDS), "Programmatic value was not found in message!");

    }

    @Then("the notification indicated by the key is not shown")
    public void the_notification_indicated_by_the_key_is_not_shown() {
        Map<String, Map<String, Notification>> notificationsByFilenameMap = getNotificationByFilename();
        notificationsByFilenameMap.values();
        assertTrue(notificationsByFilenameMap.isEmpty());
    }

    private static Map<String, Map<String, Notification>> getNotificationByFilename() {
        Map<String, Map<String, Notification>> notificationsByFilenameMap = NotificationCollector.getNotifications();
        assertNotNull(notificationsByFilenameMap, "Notifications map was not found!");
        return notificationsByFilenameMap;
    }

}
