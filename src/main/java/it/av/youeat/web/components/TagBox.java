package it.av.youeat.web.components;

import it.av.youeat.YoueatException;
import it.av.youeat.ocm.model.Ristorante;
import it.av.youeat.ocm.model.Tag;
import it.av.youeat.service.TagService;
import it.av.youeat.util.LuceneUtil;
import it.av.youeat.web.commons.AutocompleteUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * suggests tags already presents and non yet used by this restaurant
 * 
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class TagBox extends AutoCompleteTextField<String> {

    private static Logger log = LoggerFactory.getLogger(TagBox.class);

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
        super(id, model, AutocompleteUtils.getAutoCompleteSettings());
        this.ristorante = ristorante;
        Injector.get().inject(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Iterator<String> getChoices(String input) {
        Collection<String> choises = new ArrayList<String>();
        try {
            String pattern = LuceneUtil.removeSpecialChars(input);
            if(pattern.length() > 2){
                List<Tag> tags = tagService.freeTextSearch(pattern);
                tags.removeAll(ristorante.getTags());
                for (Tag tag : tags) {
                    choises.add(tag.getTag());
                }
            }
        } catch (YoueatException e) {
            log.warn("error getting the tage", e);
        }
        return choises.iterator();
    }

}
