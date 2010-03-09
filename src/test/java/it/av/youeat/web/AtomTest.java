package it.av.youeat.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import it.av.youeat.ocm.model.Eater;
import it.av.youeat.ocm.model.Language;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.EaterService;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.service.YoueatTest;
import it.av.youeat.web.xml.AtomGenerator;

import java.io.IOException;
import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.sun.syndication.io.FeedException;

@ContextConfiguration(locations = "classpath:application-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class AtomTest extends YoueatTest {

	@Autowired
	private AtomGenerator atomGenerator;
	@Autowired
	private RistoranteService ristoranteService;
	@Autowired
	private EaterService eaterService;
	private Eater user;
	private Ristorante risto;

	@Before
	public void setUp() {
		super.setUp();
		user = new Eater();
		user.setFirstname("Alessandro");
		user.setLastname("Vincelli");
		user.setPassword("secret");
		user.setEmail("a.ristoranteService@test.com");
		user.setCountry(getNocountry());
		user.setLanguage(getLanguage());
		Language italian = new Language();
		italian.setLanguage("xx");
		italian.setCountry("xx");
		user = eaterService.addRegolarUser(user);

		risto = new Ristorante();
		risto.setName("RistoAlessandro");
		risto.setCity(getNocity());
		risto.setCountry(getNocountry());
		risto = ristoranteService.insert(risto, user);

	}

	@Test
	public void testAtom() throws FeedException, IOException, JDOMException {

		Document doc = atomGenerator.generate();

		XPath xPath = XPath.newInstance("/feed");
		xPath.addNamespace(Namespace.getNamespace("http://www.w3.org/2005/Atom"));
		Iterator<Element> entries = doc.getRootElement().getDescendants(new ElementFilter("entry"));
		assertTrue(entries.hasNext());
		while (entries.hasNext()) {
			Element element = (Element) entries.next();
			Iterator<Element> titles = element.getDescendants(new ElementFilter("title"));
			assertEquals(titles.next().getValue(), risto.getName() + " " + getNocity().getName());
		}
		// XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		// xmlOutputter.output(doc, System.out);
	}
}
