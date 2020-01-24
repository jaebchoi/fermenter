package org.bitbucket.askllc.fermenter.cookbook.domain.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.bitbucket.askllc.fermenter.cookbook.domain.bizobj.SimpleDomainBO;
import org.bitbucket.askllc.fermenter.cookbook.domain.enumeration.SimpleDomainEnumeration;
import org.bitbucket.askllc.fermenter.cookbook.domain.service.rest.ContractTestService;
import org.bitbucket.fermenter.stout.messages.Message;
import org.bitbucket.fermenter.stout.messages.MessageManager;
import org.bitbucket.fermenter.stout.messages.MessageUtils;
import org.bitbucket.fermenter.stout.messages.Severity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

/**
 * Service implementation class for the ContractTest service.
 * 
 * @see org.bitbucket.askllc.fermenter.cookbook.domain.service.rest.ContractTestService
 *
 *      GENERATED STUB CODE - PLEASE *DO* MODIFY Genereated from service.impl.java.vm
 */
@Service
public class ContractTestServiceImpl extends ContractTestBaseServiceImpl implements ContractTestService {

    private static final String CONTRACT_TEST = "contract.test";

    private void addMessageToReponse(Severity severity, String methodName) {
        String[] properties = { "testMethodName" };
        Object[] inserts = { methodName };

        Message message = null;
        if (Severity.INFO.equals(severity)) {
            message = MessageUtils.createInformationalMessage(CONTRACT_TEST, properties, inserts);

        } else {
            message = MessageUtils.createErrorMessage(CONTRACT_TEST, properties, inserts);

        }

        MessageManager.addMessage(message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void voidResponseMethodImpl() {
        addMessageToReponse(Severity.INFO, "voidResponseMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String stringResponseMethodImpl() {
        addMessageToReponse(Severity.INFO, "stringResponseMethod");

        return RandomStringUtils.randomAlphanumeric(10);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SimpleDomainBO entityResponseMethodImpl() {
        addMessageToReponse(Severity.INFO, "entityResponseMethod");

        SimpleDomainBO entity = new SimpleDomainBO();
        entity.setName(RandomStringUtils.randomAlphanumeric(10));
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SimpleDomainEnumeration enumerationResponseMethodImpl() {
        addMessageToReponse(Severity.INFO, "enumerationResponseMethod");

        return SimpleDomainEnumeration.FIRST;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<String> multipleStringsResponseMethodImpl() {
        addMessageToReponse(Severity.INFO, "multipleStringsResponseMethod");
        return returnMultipleStrings();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<SimpleDomainBO> multipleEntitiesResponseMethodImpl() {
        addMessageToReponse(Severity.INFO, "multipleEntitiesResponseMethod");

        return returnMultipleEntities();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Page<String> multipleStringsPagedResponseMethodImpl(Integer startPage, Integer count) {
        addMessageToReponse(Severity.INFO, "multipleStringsPagedResponseMethod");
        List<String> responseCollection = returnMultipleStrings();
        return new PageImpl<>(responseCollection);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Page<SimpleDomainBO> multipleEntitiesPagedResponseMethodImpl(Integer startPage, Integer count) {
        addMessageToReponse(Severity.INFO, "multipleEntitiesPagedResponseMethod");
        List<SimpleDomainBO> responseCollection = returnMultipleEntities();
        return new PageImpl<>(responseCollection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void noParameterMethodImpl() {
        addMessageToReponse(Severity.INFO, "noParameterMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void stringParameterMethodImpl(String inputStr) {
        addMessageToReponse(Severity.INFO, "stringParameterMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void entityParameterMethodImpl(SimpleDomainBO entity) {
        addMessageToReponse(Severity.INFO, "entityParameterMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void enumerationParameterMethodImpl(SimpleDomainEnumeration enumeration) {
        addMessageToReponse(Severity.INFO, "enumerationParameterMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void entityAndStringParametersMethodImpl(String inputStr, SimpleDomainBO entity) {
        addMessageToReponse(Severity.INFO, "entityAndStringParametersMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void multipleStringParametersMethodImpl(List<String> inputStr) {
        addMessageToReponse(Severity.INFO, "multipleStringParametersMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void multipleEntitiesParameterMethodImpl(List<SimpleDomainBO> entity) {
        addMessageToReponse(Severity.INFO, "multipleEntitiesParameterMethod");

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void errorMessagesReturnedMethodImpl() {
        addMessageToReponse(Severity.ERROR, "errorMessagesReturnedMethod");

    }

    private List<SimpleDomainBO> returnMultipleEntities() {
        List<SimpleDomainBO> entities = new ArrayList<>();

        SimpleDomainBO entity1 = new SimpleDomainBO();
        entity1.setAnEnumeratedValue(SimpleDomainEnumeration.FIRST);
        entities.add(entity1);

        SimpleDomainBO entity2 = new SimpleDomainBO();
        entity2.setAnEnumeratedValue(SimpleDomainEnumeration.SECOND);
        entities.add(entity2);
        return entities;
    }

    private List<String> returnMultipleStrings() {
        List<String> strings = new ArrayList<>();
        strings.add(RandomStringUtils.randomAlphanumeric(10));
        strings.add(RandomStringUtils.randomAlphanumeric(10));
        return strings;
    }

}
