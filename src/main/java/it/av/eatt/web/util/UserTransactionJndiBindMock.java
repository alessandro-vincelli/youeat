package it.av.eatt.web.util;

import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class UserTransactionJndiBindMock {

    public UserTransactionJndiBindMock(UserTransaction userTransaction) throws NamingException {
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("java:comp/UserTransaction", userTransaction);
        builder.activate();
    }

}
