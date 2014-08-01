package org.tigris.atlas.mda.element.objectivec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.tigris.atlas.mda.metadata.element.Field;
import org.tigris.atlas.mda.metadata.element.Query;

public class ObjectiveCQuery implements Query {

	private Query query;
	private List decoratedCriteriaList;

	/**
	 * Create a new instance of <tt>Query</tt> with the correct functionality set
	 * to generate Objective-C code
	 * @param queryToDecorate The <tt>Query</tt> to decorate
	 */
	public ObjectiveCQuery(Query queryToDecorate) {
		if (queryToDecorate == null) {
			throw new IllegalArgumentException("ObjectiveCQuerys must be instatiated with a non-null query!");
		}
		query = queryToDecorate;
	}

	@Override
	public String getStatement() {
		return query.getStatement();
	}

	@Override
	public List getCriteria() {
		if (decoratedCriteriaList == null) {
			List queryCriteriaList = query.getCriteria();
			if ((queryCriteriaList == null) || (queryCriteriaList.size() == 0)) {
				decoratedCriteriaList = Collections.EMPTY_LIST;

			} else {
				Field f;
				decoratedCriteriaList = new ArrayList((queryCriteriaList.size()));
				Iterator i = queryCriteriaList.iterator();
				while (i.hasNext()) {
					f = (Field)i.next();
					decoratedCriteriaList.add(new ObjectiveCField(f));

				}

			}
		}

		return decoratedCriteriaList;
	}

	@Override
	public String getName() {
		return query.getName();
	}

	@Override
	public String getDocumentation() {
		return query.getDocumentation();
	}

	public String getLowercaseName() {
		return StringUtils.uncapitalize(query.getName());
	}

}
