package org.technologybrewery.fermenter.mda.element;

import java.util.List;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageTrackerSteps {

    @Before("@messageTracker")
    public void before() {
        CommonSteps.clearMessageTracker();
    }

    @After("@messageTracker")
    public void after() {
        CommonSteps.clearMessageTracker();
    }


    private MessageTracker messageTracker = MessageTracker.getInstance();
    
    @Given("multiple error messages \"{listOfStrings}\"")
    public void multiple_error_messages(List<String> errors) {
        for (String error : errors) {
            messageTracker.addErrorMessage(error);
        }
    }

    @When("the message tracker is asked for messages")
    public void the_message_tracker_is_asked_for_messages() {
    }

    @Then("the tracker reports that errors were encountered")
    public void the_tracker_reports_that_errors_were_encountered() {
        assertTrue(messageTracker.hasErrors(), "Expected errors to have been tracked!");
    }

    @Given("multiple warning messages \"{listOfStrings}\"")
    public void multiple_warning_messages(List<String> warnings) {
        for (String warning : warnings) {
            messageTracker.addWarningMessage(warning);
        }
    }

    @Then("the tracker reports that no errors were encountered")
    public void the_tracker_reports_that_no_errors_were_encountered() {
        assertFalse(messageTracker.hasErrors(), "Expected NO errors to have been tracked!");
    }

}
