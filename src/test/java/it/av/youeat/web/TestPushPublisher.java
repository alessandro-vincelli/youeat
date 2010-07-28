package it.av.youeat.web;

import it.av.youeat.web.pubsubhubbub.Publisher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration(locations = "classpath:test-application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class TestPushPublisher {

	@Test
	public void testPublisher() throws Exception {
		Publisher publisher = new Publisher();
		String hub = "http://pubsubhubbub.appspot.com/publish";
		int status = publisher.execute(hub, "http://www.youeat.org/feed");
		System.out.println("Return status : " + status);
		status = publisher.execute(hub, "http://www.youeat.org/feed");

		status = publisher.execute(hub, "http://www.youeat.org/feed");

		System.out.println("Return status : " + status);
	}
}
