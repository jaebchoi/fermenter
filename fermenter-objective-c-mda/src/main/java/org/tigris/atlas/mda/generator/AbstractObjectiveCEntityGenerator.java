package org.tigris.atlas.mda.generator;

import org.tigris.atlas.mda.generator.entity.AbstractEntityGenerator;

/**
 * Provides entity generation fields for Objective-C purposes.
 */
public abstract class AbstractObjectiveCEntityGenerator extends AbstractEntityGenerator {

	protected static final String OUTPUT_SUB_FOLDER_OBJECTIVE_C = "objectivec/";
	protected static final String OBJECTIVE_C_PROJECT_NAME = "Wino"; // TODO: load this from metadata

	@Override
	protected String getOutputSubFolder() {
		return OUTPUT_SUB_FOLDER_OBJECTIVE_C;
	}
}
