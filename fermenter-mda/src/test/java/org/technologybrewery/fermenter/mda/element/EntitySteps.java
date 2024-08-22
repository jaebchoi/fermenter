package org.technologybrewery.fermenter.mda.element;

import io.cucumber.java.After;
import io.cucumber.java.DataTableType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.technologybrewery.fermenter.mda.metamodel.element.Entity;
import org.technologybrewery.fermenter.mda.metamodel.element.EntityElement;
import org.technologybrewery.fermenter.mda.metamodel.element.Field;
import org.technologybrewery.fermenter.mda.metamodel.element.FieldElement;
import org.technologybrewery.fermenter.mda.metamodel.element.Parent;
import org.technologybrewery.fermenter.mda.metamodel.element.ParentElement;
import org.technologybrewery.fermenter.mda.metamodel.element.Reference;
import org.technologybrewery.fermenter.mda.metamodel.element.ReferenceElement;
import org.technologybrewery.fermenter.mda.metamodel.element.Relation;
import org.technologybrewery.fermenter.mda.metamodel.element.RelationElement;
import org.technologybrewery.fermenter.mda.metamodel.element.Validation;
import org.technologybrewery.fermenter.mda.metamodel.element.ValidationElement;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntitySteps extends AbstractEntitySteps {

    private Entity loadedEntity;

    @After("@entity")
    public void cleanUp() throws IOException {
        super.cleanUp();

        loadedEntity = null;

    }

    @Given("an entity named {string} in {string}")
    public void an_entity_named_in(String name, String packageName) throws Throwable {
        EntityElement entity = createBaseEntity(name, packageName, null);
        createEntityElement(entity);
    }

    @Given("an entity named {string} in {string} with documentation {string}")
    public void an_entity_named_in(String name, String packageName, String documentation) throws Throwable {
        EntityElement entity = createBaseEntity(name, packageName, documentation);
        createEntityElement(entity);
    }

    @Given("an entity name {string} in {string} with parent {string} and inheritance strategy {string}")
    public void an_entity_name_in_with_parent_and_inheritance_strategy(String name, String packageName, String parent,
                                                                       String inheritanceStrategy) throws Throwable {

        EntityElement entity = createBaseEntity(name, packageName, null);
        ParentElement parentElement = new ParentElement();
        parentElement.setPackage(packageName);
        parentElement.setType(parent);
        if (StringUtils.isNotBlank(inheritanceStrategy)) {
            parentElement.setInheritanceStrategy(inheritanceStrategy);
        }
        entity.setParent(parentElement);
        createEntityElement(entity);
    }

    @Given("an entity named {string} in {string} with table {string}")
    public void an_entity_named_in_with_table(String name, String packageName, String table) throws Throwable {
        EntityElement entity = createBaseEntity(name, packageName, null);
        entity.setTable(table);
        createEntityElement(entity);
    }

    @Given("an entity named {string} in {string} with transient flag \"{booleanValue}\"")
    public void an_entity_named_in_with_transient_flag(String name, String packageName, Boolean transientEntity)
        throws Throwable {
        EntityElement entity = createBaseEntity(name, packageName, null);
        entity.setTransient(transientEntity);
        createEntityElement(entity);
    }

    @Given("an entity named {string} in {string} with an identifier:")
    public void an_entity_named_in_with_an_identifier(String name, String packageName, List<FieldInput> identifier)
        throws Throwable {

        FieldInput id = identifier.iterator().next();
        createEntityWithIdentifier(name, packageName, id);
    }

    @Given("an entity named {string} in {string} with a field:")
    public void an_entity_named_in_with_a_field(String name, String packageName, List<FieldInput> fields)
        throws Throwable {
        FieldInput field = fields.iterator().next();

        EntityElement entity = createBaseEntity(name, packageName, null);
        FieldElement entityField = createField(field);
        entity.addField(entityField);

        // add defaults:
        FieldElement defaultId = createDefaultIdentifier();
        entity.setIdentifier(defaultId);

        createEntityElement(entity);
    }

    @Given("an entity named {string} in {string} with a {string} that has no transient value set")
    public void an_entity_named_in_with_a_that_has_no_transient_value_set(String name, String packageName, String fieldName) throws Throwable {
        FieldInput field = new FieldInput();
        field.name = fieldName;

        EntityElement entity = createBaseEntity(name, packageName, null);
        FieldElement entityField = createField(field);
        entity.addField(entityField);

        // add defaults:
        FieldElement defaultId = createDefaultIdentifier();
        entity.setIdentifier(defaultId);

        createEntityElement(entity);
    }

    @Given("an entity named {string} in {string} with a reference:")
    public void an_entity_named_in_with_a_reference(String name, String packageName, List<ReferenceInput> references)
        throws Throwable {
        ReferenceInput reference = references.iterator().next();

        EntityElement entity = createBaseEntity(name, packageName, null);

        ReferenceElement entityReference = new ReferenceElement();
        entityReference.setName(reference.referenceName);
        entityReference.setPackage(reference.referencePackage);
        entityReference.setType(reference.type);
        entityReference.setLocalColumn(reference.localColumn);
        entityReference.setDocumentation(reference.documentation);
        entityReference.setRequired(reference.required);

        entity.addReference(entityReference);

        createEntityElement(entity);
    }

    @Given("an entity named {string} in {string} with a relation:")
    public void an_entity_named_in_with_a_relation(String name, String packageName, List<RelationInput> relations)
        throws Throwable {
        RelationInput relation = relations.iterator().next();

        createEntityWithRelation(name, packageName, relation);
    }

    @Given("an entity named {string} in {string} with a valid relation that does not specify multiplicity")
    public void an_entity_named_in_with_a_valid_relation_that_does_not_specify_multiplicity(String name,
                                                                                            String packageName) throws Throwable {
        String referencedEntityName = StringUtils.capitalize(RandomStringUtils.randomAlphabetic(10));
        String referencedEntityPackage = "foo.default.multiplicity";
        createEntityWithDefaultIdentifier(referencedEntityName, referencedEntityPackage);

        RelationInput relationInput = new RelationInput();
        relationInput.type = referencedEntityName;
        relationInput.relationPackage = referencedEntityPackage;
        relationInput.documentation = RandomStringUtils.randomAlphanumeric(20);
        relationInput.fetchMode = Relation.FetchMode.LAZY.toString();
        createEntityWithRelation(name, packageName, relationInput);
    }

    @Given("an entity named {string} in {string} with a valid relation that does not specify fetch mode")
    public void an_entity_named_in_with_a_valid_relation_that_does_not_specify_fetch_mode(String name,
                                                                                          String packageName) throws Throwable {
        String referencedEntityName = StringUtils.capitalize(RandomStringUtils.randomAlphabetic(10));
        String referencedEntityPackage = "foo.default.fetchmode";
        createEntityWithDefaultIdentifier(referencedEntityName, referencedEntityPackage);

        RelationInput relationInput = new RelationInput();
        relationInput.type = referencedEntityName;
        relationInput.relationPackage = referencedEntityPackage;
        relationInput.documentation = RandomStringUtils.randomAlphanumeric(20);
        relationInput.multiplicity = Relation.Multiplicity.MANY_TO_MANY.toString();
        createEntityWithRelation(name, packageName, relationInput);
    }

    @Given("an entity named {string} in {string} with an invalid multiplicity {string}")
    public void an_entity_named_in_with_an_invalid_multiplicity(String name, String packageName,
                                                                String invalidMultiplicity) throws Throwable {
        String referencedEntityName = StringUtils.capitalize(RandomStringUtils.randomAlphabetic(10));
        String referencedEntityPackage = "foo.bad.multiplicity";
        createEntityWithDefaultIdentifier(referencedEntityName, referencedEntityPackage);

        RelationInput relationInput = new RelationInput();
        relationInput.type = referencedEntityName;
        relationInput.relationPackage = referencedEntityPackage;
        relationInput.documentation = RandomStringUtils.randomAlphanumeric(20);
        relationInput.fetchMode = Relation.FetchMode.LAZY.toString();
        relationInput.multiplicity = invalidMultiplicity;
        createEntityWithRelation(name, packageName, relationInput);
    }

    @Given("an entity named {string} in {string} with an invalid fetch mode {string}")
    public void an_entity_named_in_with_an_invalid_fetch_mode(String name, String packageName, String invalidFetchMode)
        throws Throwable {
        String referencedEntityName = StringUtils.capitalize(RandomStringUtils.randomAlphabetic(10));
        String referencedEntityPackage = "foo.bad.multiplicity";
        createEntityWithDefaultIdentifier(referencedEntityName, referencedEntityPackage);

        RelationInput relationInput = new RelationInput();
        relationInput.type = referencedEntityName;
        relationInput.relationPackage = referencedEntityPackage;
        relationInput.documentation = RandomStringUtils.randomAlphanumeric(20);
        relationInput.fetchMode = invalidFetchMode;
        relationInput.multiplicity = Relation.Multiplicity.ONE_TO_ONE.toString();
        createEntityWithRelation(name, packageName, relationInput);
    }

    private EntityElement createEntityWithIdentifier(String name, String packageName, FieldInput id)
        throws IOException {
        EntityElement entity = createBaseEntity(name, packageName, null);
        FieldElement idField = createIdentifier(id);
        entity.setIdentifier(idField);

        return createEntityElement(entity);
    }

    private EntityElement createEntityWithDefaultIdentifier(String name, String packageName)
        throws IOException {
        EntityElement entity = createBaseEntity(name, packageName, null);
        FieldInput id = new FieldInput();
        id.name = "id";
        id.column = "ID";
        id.type = "string";
        FieldElement idField = createIdentifier(id);
        entity.setIdentifier(idField);

        return createEntityElement(entity);
    }

    private FieldElement createIdentifier(FieldInput id) {
        FieldElement idField = new FieldElement();
        idField.setName(id.name);
        idField.setDocumentation(id.documentation);
        idField.setColumn(id.column);
        if (StringUtils.isNotBlank(id.generator)) {
            idField.setGenerator(id.generator);
        }
        ValidationElement type = new ValidationElement();
        String typeValue = StringUtils.isNotBlank(id.type) ? id.type : "string";
        type.setName(typeValue);
        idField.setType(type);
        return idField;
    }

    private FieldElement createDefaultIdentifier() {
        FieldElement idField = new FieldElement();
        idField.setName("id");
        idField.setDocumentation("Auto created to make a valid entity");
        idField.setColumn("ID");
        ValidationElement type = new ValidationElement();
        type.setName("String");
        idField.setType(type);
        return idField;
    }

    private FieldElement createField(FieldInput field) {
        FieldElement newField = new FieldElement();
        newField.setName(field.name);
        newField.setDocumentation(field.documentation);
        newField.setColumn(field.column);
        newField.setTransient(field.transientValue);
        newField.setLabel(field.label);
        ValidationElement type = new ValidationElement();
        type.setName(field.type);
        newField.setType(type);
        return newField;
    }

    private EntityElement createEntityWithRelation(String name, String packageName, RelationInput relation)
        throws IOException {
        EntityElement entity = createBaseEntity(name, packageName, null);

        RelationElement entityRelation = new RelationElement();
        entityRelation.setType(relation.type);
        entityRelation.setPackage(relation.relationPackage);
        entityRelation.setDocumentation(relation.documentation);
        entityRelation.setMultiplicity(relation.multiplicity);
        entityRelation.setFetchMode(relation.fetchMode);

        entity.addRelation(entityRelation);

        return createEntityElement(entity);
    }

    @When("entities are read")
    public void entities_are_read() {
        readEntities();
    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with the documentation {string}")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_the_documentation(String expectedName,
                                                                                                String expectedPackage, String expectedDocumentation) {

        validateLoadedEntities(expectedName, expectedPackage);
        assertEquals(expectedDocumentation, loadedEntity.getDocumentation(), "Unexpected documentation value!");

    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with parent {string} and inheritance strategy {string}")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_parent_and_inheritance_strategy(
        String expectedName, String expectedPackage, String expectedParent, String expectedInheritanceStrategy) {

        validateLoadedEntities(expectedName, expectedPackage);
        Parent parent = loadedEntity.getParent();
        assertEquals(expectedParent, parent.getType(), "unexpected parent value!");
        Parent.InheritanceStrategy foundInheritanceStrategy = Parent.InheritanceStrategy.fromString(expectedInheritanceStrategy);
        assertEquals(foundInheritanceStrategy, parent.getInheritanceStrategy(), "unexpected inheritance strategy value!");

    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with table {string}")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_table(String expectedName,
                                                                                    String expectedPackage, String expectedTable) {

        validateLoadedEntities(expectedName, expectedPackage);
        assertEquals(expectedTable, loadedEntity.getTable(), "unexpected table value!");

    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with a lock strategy of {string}")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_a_lock_strategy_of(String expectedName,
                                                                                                 String expectedPackage, String expectedStrategy) {

        validateLoadedEntities(expectedName, expectedPackage);
        Entity.LockStrategy expectedLockStrategy = Entity.LockStrategy.fromString(expectedStrategy);
        assertEquals(expectedLockStrategy, loadedEntity.getLockStrategy(), "unexpected lock strategy value!");

    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with a transient flag of \"{booleanValue}\"")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_a_transient_flag_of(String expectedName,
                                                                                                  String expectedPackage, Boolean expectedTransient) {

        validateLoadedEntities(expectedName, expectedPackage);
        assertEquals(expectedTransient, loadedEntity.isTransient(), "unexpected lock strategy value!");

    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with the following identifier:")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_the_following_identifier(
        String expectedName, String expectedPackage, List<FieldInput> expectedIdentifier) {
        validateLoadedEntities(expectedName, expectedPackage);
        Field foundIdField = loadedEntity.getIdentifier();
        assertNotNull(foundIdField, "No identifier found!");

        FieldInput expectedIdField = expectedIdentifier.iterator().next();
        assertEquals(expectedIdField.name, foundIdField.getName(), "Identifier field name did not match!");
        assertEquals(expectedIdField.column, foundIdField.getColumn(), "Identifier column name did not match!");
        assertEquals(expectedIdField.documentation, foundIdField.getDocumentation(), "Identifier documentation did not match!");

        Validation foundType = foundIdField.getValidation();
        assertNotNull(foundType, "No identifier type found!");
        assertEquals(expectedIdField.type, foundType.getName(), "Identifier type name did not match!");

    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with the following field:")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_the_following_field(String expectedName,
                                                                                                  String expectedPackage, List<FieldInput> expectedFields) {

        validateLoadedEntities(expectedName, expectedPackage);

        Field foundField = null;
        FieldInput expectedField = expectedFields.iterator().next();
        List<Field> loadedEntityFields = loadedEntity.getFields();
        for (Field loadedEntityField : loadedEntityFields) {
            if (loadedEntityField.getName().equals(expectedField.name)) {
                foundField = loadedEntityField;
                Boolean expectedTransientDefaultIsFalse = expectedField.transientValue != null
                    ? expectedField.transientValue
                    : Boolean.FALSE;
                assertEquals(expectedField.name, foundField.getName(), "Field name did not match!");
                assertEquals(expectedTransientDefaultIsFalse, foundField.isTransient(), "Field transient property did not match!");
                assertEquals(expectedField.column, foundField.getColumn(), "Column name did not match!");
                assertEquals(expectedField.documentation, foundField.getDocumentation(), "Documentation did not match!");
                assertTrue(StringUtils.equals(expectedField.label, foundField.getLabel()), "Label did not match!");
                break;
            }
        }

        Validation foundType = foundField != null ? foundField.getValidation() : null;
        assertNotNull(foundType, "No field type found!");
        assertEquals(expectedField.type, foundType.getName(), "Field type name did not match!");
    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with the following reference:")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_the_following_reference(
        String expectedName, String expectedPackage, List<ReferenceInput> expectedReferences) {

        validateLoadedEntities(expectedName, expectedPackage);
        Reference foundReference = loadedEntity.getReferences().iterator().next();
        assertNotNull(foundReference, "No reference found!");

        ReferenceInput expectedReference = expectedReferences.iterator().next();
        assertEquals(expectedReference.referenceName, foundReference.getName(), "Reference name did not match!");
        assertEquals(expectedReference.documentation, foundReference.getDocumentation(), "Reference documentation did not match!");
        assertEquals(expectedReference.type, foundReference.getType(), "Reference type did not match!");
        assertEquals(expectedReference.referencePackage, foundReference.getPackage(), "Reference type package did not match!");
        assertEquals(expectedReference.localColumn, foundReference.getLocalColumn(), "Reference local column did not match!");
        assertEquals(expectedReference.required, foundReference.isRequired(), "Reference requiredness did not match!");

    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with the following relation:")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_the_following_relation(
        String expectedName, String expectedPackage, List<RelationInput> expectedRelations) {
        validateLoadedEntities(expectedName, expectedPackage);
        Relation foundRelation = getAndAssertRelation();

        RelationInput expectedRelation = expectedRelations.iterator().next();
        assertEquals(expectedRelation.documentation, foundRelation.getDocumentation(), "Relation documentation did not match!");
        assertEquals(expectedRelation.type, foundRelation.getType(), "Relation type did not match!");
        assertEquals(expectedRelation.relationPackage, foundRelation.getPackage(), "Relation type package did not match!");
        assertEquals(Relation.Multiplicity.fromString(expectedRelation.multiplicity),
            foundRelation.getMultiplicity(), "Relation multiplicity did not match!");
        assertEquals(Relation.FetchMode.fromString(expectedRelation.fetchMode),
            foundRelation.getFetchMode(), "Relation fetchMode did not match!");
    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with the following relation that has one-to-many multiplicity")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_the_following_relation_that_has_M_multiplicity(
        String expectedName, String expectedPackage) {
        validateLoadedEntities(expectedName, expectedPackage);

        Relation foundRelation = getAndAssertRelation();

        assertEquals(Relation.Multiplicity.ONE_TO_MANY, foundRelation.getMultiplicity(), "Relation multiplicity did not match!");
    }

    @Then("an entity metamodel instance is returned for the name {string} in {string} with the following relation that has eager fetch mode")
    public void an_entity_metamodel_instance_is_returned_for_the_name_in_with_the_following_relation_that_has_eager_fetch_mode(
        String expectedName, String expectedPackage) {
        validateLoadedEntities(expectedName, expectedPackage);

        Relation foundRelation = getAndAssertRelation();

        assertEquals(Relation.FetchMode.EAGER, foundRelation.getFetchMode(), "Relation fetch modes did not match!");
    }

    private Relation getAndAssertRelation() {
        Relation foundRelation = loadedEntity.getRelations().iterator().next();
        assertNotNull(foundRelation, "No relation found!");
        return foundRelation;
    }

    private void validateLoadedEntities(String expectedName, String expectedPackage) {
        if (encounteredException != null) {
            throw encounteredException;
        }

        loadedEntity = metadataRepo.getEntities(expectedPackage).get(expectedName);
        assertEquals(expectedName, loadedEntity.getName(), "Unexpected entity name!");
        assertEquals(expectedPackage, loadedEntity.getPackage(), "Unexpected entity package!");

    }

    /**
     * Uses to pass field-level information into test steps
     */
    public static class FieldInput {
        public String name;
        public String fieldPackage;
        public String documentation;
        public String type;
        public String column;
        public String generator;
        public String label;
        public Boolean required;
        public Boolean transientValue;
    }

    /**
     * Uses to pass reference-level information into test steps
     */
    public static class ReferenceInput {
        public String name;
        public String referenceName;
        public String documentation;
        public String type;
        public String referencePackage;
        public Boolean required;
        public String localColumn;
    }

    /**
     * Uses to pass relation-level information into test steps
     */
    public static class RelationInput {
        public String documentation;
        public String type;
        public String relationPackage;
        public String multiplicity;
        public String localColumn;
        public String fetchMode;
    }

    @DataTableType
    public FieldInput fieldInputEntry(Map<String, String> entry) {
        FieldInput input = new FieldInput();
        input.name = entry.get("name");
        input.fieldPackage = entry.get("fieldPackage");
        input.documentation = entry.get("documentation");
        input.type = entry.get("type");
        input.column = entry.get("column");
        input.generator = entry.get("generator");
        input.label = entry.get("label");
        input.required = Boolean.valueOf(entry.get("required"));
        input.transientValue = Boolean.valueOf(entry.get("transientValue"));

        return input;
    }

    @DataTableType
    public ReferenceInput referenceInputEntry(Map<String, String> entry) {
        ReferenceInput input = new ReferenceInput();
        input.name = entry.get("name");
        input.referenceName = entry.get("referenceName");
        input.documentation = entry.get("documentation");
        input.type = entry.get("type");
        input.referencePackage = entry.get("referencePackage");
        input.required = Boolean.valueOf(entry.get("required"));
        input.localColumn = entry.get("localColumn");

        return input;
    }

    @DataTableType
    public RelationInput relationInputEntry(Map<String, String> entry) {
        RelationInput input = new RelationInput();
        input.documentation = entry.get("documentation");
        input.type = entry.get("type");
        input.relationPackage = entry.get("relationPackage");
        input.multiplicity = entry.get("multiplicity");
        input.localColumn = entry.get("localColumn");
        input.fetchMode = entry.get("fetchMode");

        return input;
    }

}
