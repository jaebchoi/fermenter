package org.technologybrewery.fermenter.mda.test.utils.tests;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/specifications",
    plugin = {"json:target/cucumber-reports/cucumber.json"}
)
public class TestRunner {
}
