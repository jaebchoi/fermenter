package org.technologybrewery.fermenter.mda.element;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.technologybrewery.fermenter.mda.generator.GenerationException;
import org.technologybrewery.fermenter.mda.metamodel.DefaultModelInstanceRepository;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceRepositoryManager;
import org.technologybrewery.fermenter.mda.metamodel.ModelInstanceUrl;
import org.technologybrewery.fermenter.mda.metamodel.ModelRepositoryConfiguration;
import org.technologybrewery.fermenter.mda.metamodel.element.EntityElement;
import org.technologybrewery.fermenter.mda.util.MessageTracker;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractEntitySteps {

    private static final Logger logger = LoggerFactory.getLogger(EntitySteps.class);

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected File entitiesDirectory = new File("target/temp-metadata", "entities");    
    protected MessageTracker messageTracker = MessageTracker.getInstance();

    protected String currentBasePackage;
    protected File entityFile;  
    protected GenerationException encounteredException;
    protected DefaultModelInstanceRepository metadataRepo;
    
    protected void cleanUp() throws IOException {
        messageTracker.clear();
        currentBasePackage = null;
        
        if (entitiesDirectory.exists()) {
            FileUtils.forceDelete(entitiesDirectory);
        }
    }
 
    protected EntityElement createEntityElement(EntityElement entity)
            throws IOException {
        
        entitiesDirectory.mkdirs();
        entityFile = new File(entitiesDirectory, entity.getName() + ".json");
        objectMapper.writeValue(entityFile, entity);
        logger.debug(objectMapper.writeValueAsString(entity));
        assertTrue(entityFile.exists(), "Entities not written to file!");

        currentBasePackage = entity.getPackage();

        return entity;
    }

    protected EntityElement createBaseEntity(String name, String packageName, String documentation) {
        EntityElement entity = new EntityElement();
        if (StringUtils.isNotBlank(name)) {
            entity.setName(name);
        }
        entity.setPackage(packageName);
        entity.setDocumentation(documentation);
        return entity;
    }

    protected void readEntities() {
        encounteredException = null;
    
    	try {
    		ModelRepositoryConfiguration config = new ModelRepositoryConfiguration();
    		config.setArtifactId("fermenter-mda");
    		config.setBasePackage(currentBasePackage);
    		Map<String, ModelInstanceUrl> metadataUrlMap = config.getMetamodelInstanceLocations();
    		metadataUrlMap.put("fermenter-mda",
    				new ModelInstanceUrl("fermenter-mda", entitiesDirectory.getParentFile().toURI().toString()));
    
    		metadataRepo = new DefaultModelInstanceRepository(config);
    		ModelInstanceRepositoryManager.setRepository(metadataRepo);
    		metadataRepo.load();
    		metadataRepo.validate();
    
    	} catch (GenerationException e) {
    		encounteredException = e;
    	}
    }

}
