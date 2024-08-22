package org.technologybrewery.fermenter.mda.element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import io.cucumber.java.DataTableType;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.ModelContext;
import org.technologybrewery.fermenter.mda.metamodel.element.Entity;
import org.technologybrewery.fermenter.mda.metamodel.element.EntityElement;
import org.technologybrewery.fermenter.mda.metamodel.element.ReferenceElement;
import org.technologybrewery.fermenter.mda.metamodel.element.RelationElement;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class OrderedEntitiesSteps extends AbstractEntitySteps {

    private static final String TEST_PACKAGE_NAME = "entity.ordering";

    private List<String> orderedEntityNames;

    @After("@orderedEntities")
    public void cleanUp() throws IOException {
        super.cleanUp();
        
        if (orderedEntityNames != null) {
            orderedEntityNames.clear();
        }

    }

    @Given("the following entities:")
    public void the_following_entities(List<EntityTestInfo> entityInfos) throws Throwable {
        createEntities(entityInfos);
    }

    @Given("the following entities and their references:")
    public void the_following_entities_and_their_references(List<EntityTestInfo> entityInfos) throws Throwable {
        createEntities(entityInfos);
    }
    
    @Given("the following entities and their relations:")
    public void the_following_entities_and_their_relations(List<EntityTestInfo> entityInfos) throws Throwable {
        createEntities(entityInfos);
    }    

    @When("the entities are loaded")
    public void the_entities_are_loaded() {
        this.readEntities();

        Set<Entity> entitiesByDependency = metadataRepo.getEntitiesByDependencyOrder(ModelContext.LOCAL.toString());

        orderedEntityNames = new ArrayList<>();
        for (Entity entity : entitiesByDependency) {
            orderedEntityNames.add(entity.getName());
        }

    }

    @Then("the values are listed in the following order:")
    public void the_values_are_listed_in_the_following_order(List<String> expectedOrder) {
        for (String expectedString : expectedOrder) {
            int expectedLocation = expectedOrder.indexOf(expectedString);
            int actualLocation = orderedEntityNames.indexOf(expectedString);
            assertEquals(expectedLocation, actualLocation, "Order not expected for value '" + expectedString + "'!");
        }
    }

    @Then("{string} is a precursor of {string}")
    public void is_a_precursor_of(String precursor, String value) {
        int indexOfPrecursor = orderedEntityNames.indexOf(precursor);
        int indexOfValue = orderedEntityNames.indexOf(value);

        assertTrue(indexOfPrecursor < indexOfValue, "precursor is NOT before the value as expected!");

    }

    protected void createEntities(List<EntityTestInfo> entityInfos) throws IOException {
        for (EntityTestInfo entityInfo : entityInfos) {
            EntityElement entity = new EntityElement();
            entity.setName(entityInfo.entityName);
            entity.setPackage(TEST_PACKAGE_NAME);

            addReferencesToEntity(entityInfo, entity);
            addRelationsToEntity(entityInfo, entity);

            createEntityElement(entity);
        }
    }

    protected void addReferencesToEntity(EntityTestInfo entityInfo, EntityElement entity) {
        if (StringUtils.isNotBlank(entityInfo.references)) {
            List<String> references = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(entityInfo.references, ",");
            while (st.hasMoreTokens()) {
                references.add(st.nextToken().trim());
            }
            
            for (String referenceName : references) {
                ReferenceElement reference = new ReferenceElement();
                reference.setName(referenceName);
                reference.setType(referenceName);
                reference.setPackage(TEST_PACKAGE_NAME);
                entity.addReference(reference);
            }
        }
    }
    
    protected void addRelationsToEntity(EntityTestInfo entityInfo, EntityElement entity) {
        if (StringUtils.isNotBlank(entityInfo.relations)) {
            List<String> relations = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(entityInfo.relations, ",");
            while (st.hasMoreTokens()) {
                relations.add(st.nextToken().trim());
            }
            
            for (String referenceName : relations) {
                RelationElement relation = new RelationElement();
                relation.setType(referenceName);
                relation.setPackage(TEST_PACKAGE_NAME);
                entity.addRelation(relation);
            }
        }
    }    

    public static class EntityTestInfo {
        public String entityName;
        public String references;
        public String relations;
    }

    @DataTableType
    public EntityTestInfo relationInputEntry(Map<String, String> entry) {
        EntityTestInfo input = new EntityTestInfo();
        input.entityName = entry.get("entityName");
        input.references = entry.get("references");
        input.relations = entry.get("relations");

        return input;
    }

}
