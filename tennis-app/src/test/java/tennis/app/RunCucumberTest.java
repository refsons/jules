package tennis.app;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features") // Tells Cucumber where to find feature files
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary") // Standard Cucumber plugins
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "tennis.app") // Tells Cucumber where to find step definitions
public class RunCucumberTest {
    // This class remains empty, serving as an entry point for JUnit Platform.
}
