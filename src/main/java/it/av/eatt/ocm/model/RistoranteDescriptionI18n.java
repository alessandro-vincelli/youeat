package it.av.eatt.ocm.model;

import it.av.eatt.web.Locales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author Alessandro Vincelli
 */
@Entity
public class RistoranteDescriptionI18n extends BasicEntity {

    public static final String LANGUAGE = "language";
    public static final String DESCRIPTION = "description";
    @ManyToOne
    private Language language;
    @Column(length = 10000)
    private String description;

    /**
     * default construct
     */
    public RistoranteDescriptionI18n() {
        super();
    }

    /**
     * Use the value in {@link Locales} to create the {@link Language}  
     * 
     * @param language the language to use for this description
     */
    public RistoranteDescriptionI18n(Language language) {
        super();
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
