package it.av.youeat.web.xml;

import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.service.RistoranteService;
import it.av.youeat.web.util.GeneratorRistoranteURL;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.impl.Atom10Generator;

public class AtomGenerator {

    @Autowired
    private RistoranteService ristoranteService;
    @Autowired
    private GeneratorRistoranteURL generatorRistoranteURL;

    // <title>Example Feed</title>
    // <subtitle>A subtitle.</subtitle>
    // <link href="http://example.org/feed/" rel="self" />
    // <link href="http://example.org/" />
    // <id>urn:uuid:60a76c80-d399-11d9-b91C-0003939e0af6</id>
    // <updated>2003-12-13T18:30:02Z</updated>
    // <author>
    // <name>John Doe</name>
    // <email>johndoe@example.com</email>
    // </author>

    public Document generate() throws FeedException {
        Feed feed = new Feed("atom");
        feed.setTitle("YouEat Feed");
        feed.setSubtitle(new Content());
        feed.setId("");
        feed.setUpdated(Calendar.getInstance().getTime());
        List<Link> alternateLinks = new ArrayList<Link>(1);
        Link link = new Link();
        link.setHref("http://www.youeat.org/feed/");
        link.setRel("self");
        alternateLinks.add(link);
        Link linkNotSelf = new Link();
        linkNotSelf.setHref("http://www.youeat.org");
        linkNotSelf.setRel(null);
        alternateLinks.add(linkNotSelf);
        feed.setAlternateLinks(alternateLinks);
        ArrayList<Person> persons = new ArrayList<Person>(1);
        persons.add(getAuthor());
        feed.setAuthors(persons);
        feed.setEntries(getEntries());
        Atom10Generator atom10Generator = new Atom10Generator();
        Document doc = atom10Generator.generate(feed);
        return doc;
    }

    private Person getAuthor() {
        Person author = new Person();
        author.setEmail("a.vincelli@gmail.com");
        author.setName("Alessandro Vincelli");
        author.setUrl("http://www.alessandro.vincelli.name");
        return author;
    }

    private List<Entry> getEntries() {
        List<Ristorante> ristorantes = ristoranteService.getLastsModified(25);
        List<Entry> entries = new ArrayList<Entry>(ristorantes.size());
        for (Ristorante ristorante : ristorantes) {
            Entry entry = new Entry();
            entry.setId(ristorante.getId());
            entry.setUpdated(ristorante.getModificationTime());
            entry.setTitle(ristorante.getName() + " " + ristorante.getCity().getName());
            List<Link> urls = new ArrayList<Link>(1);
            Link link = new Link();
            link.setHref(generatorRistoranteURL.getUrl(ristorante));
            link.setTitle(ristorante.getName());
            urls.add(link);
            entry.setAlternateLinks(urls);
            entries.add(entry);
        }
        return entries;
    }
}
