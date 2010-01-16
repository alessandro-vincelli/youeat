package it.av.youeat.service;

import it.av.youeat.ocm.model.BasicNode;

import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.extensions.jcr.SessionFactory;
import org.springframework.extensions.jcr.jackrabbit.ocm.JcrMappingTemplateGeneric;

/**
 * Subclass of {@link JdbcAccountRepository} expressly designed to throw a
 * Spring {@link DataAccessException} under certain conditions (see
 * {@link #updateAccount(Account)}). Used for testing purposes when proving that
 * transaction management is applied properly and that rollback functionality is
 * working as expected.
 * 
 * @see #updateAccount(Account)
 * @see TransferServiceTransactionManagementTests#causePartialFailureAndVerifyTransactionRollback()
 * 
 * @author Chris Beams
 */
class FaultInjectingJcrMappingTemplateGeneric<T extends BasicNode> extends JcrMappingTemplateGeneric {

    FaultInjectingJcrMappingTemplateGeneric() {
        super();
    }

    FaultInjectingJcrMappingTemplateGeneric(SessionFactory sessionFactory, Mapper mapper) {
        super(sessionFactory, mapper);
    }

    @Override
    public void insert(Object object) {
        throw new InvalidDataAccessResourceUsageException("intentionally injected fault");
    }


    /**
     * @throws InvalidDataAccessResourceUsageException
     *             if account.getId() is equal to 2.
     */
    /*
     * @Override public void updateAccount(Account account) {
     * if(account.getId().equals("2")) throw new
     * InvalidDataAccessResourceUsageException( "intentionally injected fault");
     * 
     * super.updateAccount(account); }
     */

}