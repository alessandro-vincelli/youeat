/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.service;

import static org.junit.Assert.assertTrue;
import it.av.youeat.service.EaterService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback=true)
@Transactional
public class UserServiceTransactionManagerTest{
	
	@Autowired
	//@Qualifier("userServiceFailing")
	@Qualifier("userService")
	protected EaterService userService;

	 /**
     * Proves that the instance of {@link TransferService} that is retrieved
     * from the context is in fact an AOP proxy.
     */
    @Test
    public void verifyTransferServiceBeanIsProxy() {
        assertTrue("transaction management proxy was not applied as expected",
                AopUtils.isAopProxy(userService));
    }
	
	@Test
	public void testUsersBasic(){
			/*Eater a = new Eater();
			a.setFirstname("Alessandro");
			a.setLastname("Vincelli");
			a.setPassword("secret");
			a.setEmail("userServiceTest@test.com");
			*/
			try {
			    //FIXME Change the faultijection class
				//userService.insert(a);
				//fail("should have thrown injected fault exception");
			} catch (Exception e) {
				//expected
			}
	}

}