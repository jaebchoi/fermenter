package org.bitbucket.fermenter.mda.metadata.element;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of the {@link Query} interface.
 */
public class QueryMetadata extends MetadataElement implements Query {

	private String name;
	private String documentation;
	private List<Field> criteria;
	private String statement;

	/**
	 * {@inheritDoc}
	 */
	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Field> getCriteria() {
		if (criteria == null) {
			criteria = new ArrayList<Field>();
		}
		return criteria;
	}

	/**
	 * @param criteria
	 *            The criteria to set.
	 */
	public void setCriteria(List<Field> criteria) {
		this.criteria = criteria;
	}

	public void addCriterion(Field crit) {
		getCriteria().add(crit);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	/**
	 * {@inheritDoc}
	 */
	public void validate() {
	}
}
