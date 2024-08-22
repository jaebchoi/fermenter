package org.technologybrewery.fermenter.mda.element;

import java.io.File;

import org.aeonbits.owner.KrauseningConfigFactory;
import org.apache.commons.io.FileUtils;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.MetamodelConfig;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

/**
 * NB: This class is an artifact from Cucumber 1.x. In Cucumber 7.x, they no longer get picked up because no feature
 * file directly triggers them. As such, they have been left here and directly called from the appropriate step classes
 * that Cucumber wires up based on available feature files.
 */
public final class CommonSteps {
    private static final MetamodelConfig config = KrauseningConfigFactory.create(MetamodelConfig.class);
    
    private static final File enumerationDirectory = new File("target/temp-metadata", config.getEnumerationsRelativePath());
    private static final  File servicesDirectory = new File("target/temp-metadata", config.getServicesRelativePath());
    private static final File entitiesDirectory = new File("target/temp-metadata", config.getEntitiesRelativePath());
    private static final File typeDictionaryDirectory = new File("target/temp-metadata", config.getDictionaryTypesRelativePath());
    private static final File messageGroupDirectory = new File("target/temp-metadata", config.getMessageGroupsRelativePath());
    private static final File rulesDirectory = new File("target/temp-metadata", config.getRulesRelativePath());

    private CommonSteps() {
        // prevent instantiation
    }

    public static void performCommonBeforeTasks() {
        clearMessageTracker();
        commonSetUp();
    }

    public static void performCommonAfterTasks() {
        try {
            commonCleanUp();
        } catch (Exception e) {
            throw new GenerationException("Problem cleaning up after testing!", e);
        }

    }

    /**
     * Clears out the message tracker (a singleton) before each scenario.
     */
    public static void clearMessageTracker() {
        MessageTracker messageTracker = MessageTracker.getInstance();
        messageTracker.clear();
    }
        

    public static void commonSetUp() {
        // make all the directories so we don't get a bunch of warnings:
        enumerationDirectory.mkdirs();
        servicesDirectory.mkdirs();
        entitiesDirectory.mkdirs();
        typeDictionaryDirectory.mkdirs();
        messageGroupDirectory.mkdirs();
        rulesDirectory.mkdirs();
    }
    
    /**
     * Clears out the message tracker (a singleton) before each scenario.
     */
    public static void commonCleanUp() throws Exception {
        FileUtils.deleteDirectory(enumerationDirectory);
        FileUtils.deleteDirectory(servicesDirectory);
        FileUtils.deleteDirectory(entitiesDirectory);
        FileUtils.deleteDirectory(typeDictionaryDirectory);
        FileUtils.deleteDirectory(messageGroupDirectory);
        FileUtils.deleteDirectory(rulesDirectory);
    }
    
}
