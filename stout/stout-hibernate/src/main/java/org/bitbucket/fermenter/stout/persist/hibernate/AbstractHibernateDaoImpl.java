package org.bitbucket.fermenter.stout.persist.hibernate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.bitbucket.fermenter.stout.bizobj.AbstractBusinessObjectFactoryInterface;
import org.bitbucket.fermenter.stout.bizobj.BusinessObject;
import org.bitbucket.fermenter.stout.factory.FactoryManager;
import org.bitbucket.fermenter.stout.messages.Message;
import org.bitbucket.fermenter.stout.messages.MessageFactory;
import org.bitbucket.fermenter.stout.messages.MessageManager;
import org.bitbucket.fermenter.stout.messages.Severity;
import org.bitbucket.fermenter.stout.persist.Dao;
import org.bitbucket.fermenter.stout.transfer.PrimaryKey;
import org.hibernate.HibernateException;
import org.hibernate.ObjectDeletedException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for Hibernate persistence.
 */
public abstract class AbstractHibernateDaoImpl<BO extends BusinessObject, PK extends PrimaryKey> implements Dao<BO, PK> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractHibernateDaoImpl.class);
    private static final String DATABASE_EXCEPTION_KEY = "database.exception";

    protected Session getSession() {
        SessionFactory hibernateSessionFactory = HibernateSessionFactoryManager.getInstance().getSessionFactory();

        if (hibernateSessionFactory == null) {
            LOG.warn("Your " + HibernateSessionFactoryManager.class.getSimpleName()
                    + " has not been initialized. Calling init() on your behalf!");
            HibernateSessionFactoryManager.getInstance().init();
        }

        Session hibernateSession;
        try {
            hibernateSession = hibernateSessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            hibernateSession = hibernateSessionFactory.openSession();
        }

        return hibernateSession;
    }

    /**
     * {@inheritDoc}
     */
    public BO save(BO businessObject) {
        try {
            Session hibernateSession = getSession();
            if (businessObject.getKey().getValue() == null) {
                Serializable newlyPersistentObjectID = hibernateSession.save(businessObject);
                businessObject.setKey(getNewPKWrapperForPersistentIDValue(newlyPersistentObjectID));
            } else {
                return (BO) hibernateSession.merge(businessObject);
            }
        } catch (HibernateException exception) {
            return handleDataAccessException(businessObject, exception);
        }

        return businessObject;
    }

    /**
     * {@inheritDoc}
     */
    public BO delete(PK primaryKey) {
        BO businessObject = findByPrimaryKey(primaryKey);

        if (businessObject != null) {
            try {
                getSession().delete(businessObject);
            } catch (HibernateException exception) {
                return handleDataAccessException(businessObject, exception);
            }
        } else {
            LOG.warn("Could not find BO of type [" + getEntityName() + "] with PK [" + primaryKey.getValue()
                    + "] to delete");
        }

        return businessObject;
    }

    /**
     * {@inheritDoc}
     */
    public BO findByPrimaryKey(PK primaryKey) {
        BO businessObject = null;

        try {
            Serializable primaryKeyValue = primaryKey.getValue();
            if (primaryKeyValue != null) {
                businessObject = (BO) getSession().get(getBusinessObjectClazz(), primaryKey.getValue());
            }
        } catch (HibernateException exception) {
            return handleDataAccessException(primaryKey, exception);
        }

        return businessObject;
    }

    /**
     * Answer a list of entities that match the criteria defined by a named query.
     * 
     * @param queryName
     *            Name of the query entity execute. Must be defined in a Hibernate mapping file.
     * @param paramNames
     *            Array of query parameter names. Must match param names in Hibernate mapping file.
     * @param paramValues
     *            Array of query parameter values
     * @param types
     *            Array of query parameter types
     * @return query results
     */
    protected List<BO> query(String queryName, String[] paramNames, Object[] paramValues, Type[] paramTypes) {
        List<BO> entities = null;

        try {
            Query query = buildBaseQuery(queryName, paramNames, paramValues, paramTypes);
            entities = (List<BO>) query.list();

        } catch (HibernateException exception) {
            return Arrays.asList(handleDataAccessException(exception));
        }

        return entities;
    }

    private Query buildBaseQuery(String queryName, String[] paramNames, Object[] paramValues, Type[] paramTypes) {
        Session session = getSession();
        Query query = session.getNamedQuery(queryName);
        if (paramNames != null) {
            int length = paramNames.length;
            for (int i = 0; i < length; i++) {
                query.setParameter(paramNames[i], paramValues[i], paramTypes[i]);
            }
        }
        return query;
    }

    /**
     * Answer a list of entities that match the criteria defined by a named query with pagination.
     * 
     * @param queryName
     *            Name of the query entity execute. Must be defined in a Hibernate mapping file.
     * @param paramNames
     *            Array of query parameter names. Must match param names in Hibernate mapping file.
     * @param paramValues
     *            Array of query parameter values
     * @param types
     *            Array of query parameter types
     * @param firstResult
     *            First record to return for this given page of results
     * @param maxResults
     *            Maximum number of results to return
     * @return query results
     */
    protected List<BO> query(String queryName, String[] paramNames, Object[] paramValues, Type[] paramTypes,
            int firstResult, int maxResults) {
        List<BO> entities = null;

        try {
            Query query = buildBaseQuery(queryName, paramNames, paramValues, paramTypes);
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResults);
            entities = (List<BO>) query.list();

        } catch (HibernateException exception) {
            return Arrays.asList(handleDataAccessException(exception));
        }

        return entities;
    }

    /**
     * Answer an update or delete that match the criteria defined by a named query.
     * 
     * @param statementName
     *            Name of the HQL statement to execute. Must be defined in a Hibernate mapping file.
     * @param paramNames
     *            Array of query parameter names. Must match param names in Hibernate mapping file.
     * @param paramValues
     *            Array of query parameter values
     * @param types
     *            Array of query parameter types
     * @return the number of updated or deleted records on successful execution or a negative number on a failed
     *         execution
     */
    protected int updateOrDelete(String statementName, String[] paramNames, Object[] paramValues, Type[] paramTypes) {
        int impactedRecords;

        try {
            Query query = buildBaseQuery(statementName, paramNames, paramValues, paramTypes);

            impactedRecords = query.executeUpdate();
        } catch (HibernateException exception) {
            impactedRecords = -1;
        }

        return impactedRecords;
    }

    /**
     * Creates a new {@link PrimaryKey} wrapper based on the given ID value that was generated by Hibernate.
     * 
     * @param persistentIDValue
     *            ID that was created by Hibernate as a result of making a transient {@link BusinessObject} persistent.
     * @return wrapper {@link PrimaryKey} object that encapsulates the newly created {@link BusinessObject}'s ID.
     */
    protected abstract PK getNewPKWrapperForPersistentIDValue(Serializable persistentIDValue);

    /**
     * Returns the underlying {@link BusinessObject} implementation {@link Class} whose data access logic is managed by
     * this DAO.
     * 
     * @return
     */
    protected abstract Class<BO> getBusinessObjectClazz();

    /**
     * Returns the logical name for the entity whose data access logic is managed by this DAO.
     * 
     * @return
     */
    protected abstract String getEntityName();

    /**
     * Turn the database exception into a meaningful error message
     * 
     * @param pk
     * @param dae
     * @return
     */
    private BO handleDataAccessException(PK pk, HibernateException dae) {
        Throwable cause = dae.getCause();

        if (cause != null && cause instanceof ObjectDeletedException) {
            // Object was deleted - return null
            return null;
        } else if (cause != null && cause instanceof ObjectNotFoundException) {
            // Object not found - return null
            return null;
        }

        AbstractBusinessObjectFactoryInterface factory = (AbstractBusinessObjectFactoryInterface) FactoryManager
                .createFactory(FactoryManager.BUSINESS_OBJECT, pk.getClass());
        BusinessObject bo = factory.createBusinessObject(pk.getEntityName());
        bo.setKey(pk);
        return handleDataAccessException((BO) bo, dae);
    }

    /**
     * Turn the database exception into a meaningful error message
     * 
     * @param dae
     * @return
     */
    protected BO handleDataAccessException(HibernateException dae) {
        AbstractBusinessObjectFactoryInterface factory = (AbstractBusinessObjectFactoryInterface) FactoryManager
                .createFactory(FactoryManager.BUSINESS_OBJECT, getClass());
        BusinessObject bo = factory.createBusinessObject(getEntityName());
        return handleDataAccessException((BO) bo, dae);
    }

    /**
     * Turn the database exception into a meaningful error message
     * 
     * @param entity
     * @param dae
     * @return
     */
    protected BO handleDataAccessException(BO businessObject, HibernateException dae) {
        if (LOG.isDebugEnabled()) {
            LOG.error("A problem was encountered interacting with the database:", dae);
        }
        Message message = MessageFactory.createMessage();
        message.setKey(DATABASE_EXCEPTION_KEY);
        message.addInsert(dae.getMessage());
        message.setSeverity(Severity.ERROR);
        MessageManager.addMessage(message);

        return businessObject;
    }
}
