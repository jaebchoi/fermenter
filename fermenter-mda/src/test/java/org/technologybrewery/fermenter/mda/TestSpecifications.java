package org.technologybrewery.fermenter.mda;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Work around. Surefire does not use JUnits Test Engine discovery
 * functionality. Alternatively execute the
 * org.junit.platform.console.ConsoleLauncher with the maven-antrun-plugin.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("specifications")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.technologybrewery.fermenter")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "json:target/cucumber-reports/cucumber.json")
public class TestSpecifications {

}
