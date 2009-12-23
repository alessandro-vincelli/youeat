package it.av.eatt.web.components;

import it.av.eatt.JackWicketException;
import it.av.eatt.ocm.model.Ristorante;
import it.av.eatt.ocm.model.Tag;
import it.av.eatt.service.TagService;
import it.av.eatt.util.LuceneUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
/**
 * suggests tags already presents and non yet used by this restaurant 
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public class TagBox extends AutoCompleteTextField<String> {
    private static final long serialVersionUID = 1L;
    @SpringBean
    private TagService tagService;
    private Ristorante ristorante;

    /**
     * @param model
     * @param id
     * @param ristoranteService
     * @param ristorante
     */
    public TagBox(Model<String> model, String id, Ristorante ristorante) {
        super(id, model);
        this.ristorante = ristorante;
        InjectorHolder.getInjector().inject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<String> getChoices(String input) {
        Collection<String> choises = new ArrayList<String>();
        try {
            List<Tag> tags = tagService.freeTextSearch(LuceneUtil.removeSpecialChars(input) + "~");
            tags.removeAll(ristorante.getTags());
            for (Tag tag : tags) {
                choises.add(tag.getTag());
            }
        } catch (JackWicketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return choises.iterator();
    }

}
