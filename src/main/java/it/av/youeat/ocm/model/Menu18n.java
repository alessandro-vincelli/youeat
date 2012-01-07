package it.av.youeat.ocm.model;

import it.av.youeat.web.Locales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Represents a men managed by a Restaurateur 
 * 
 * @author Alessandro Vincelli
 */
@Entity
public class Menu18n extends BasicEntity {

    public static final String LANGUAGE = "language";
    public static final String MENU = "blackboard";
    @ManyToOne
    private Language language;
    @Column(length = 10000)
    private String blackboard;

    /**
     * default construct
     */
    public Menu18n() {
        super();
    }

    /**
     * Use the value in {@link Locales} to create the {@link Language}  
     * 
     * @param language the language to use for this blackboard
     */
    public Menu18n(Language language) {
        super();
        this.language = language;
        this.blackboard = "";
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getBlackboard() {
        return blackboard;
    }

    public void setBlackboard(String blackboard) {
        this.blackboard = blackboard;
    }
    
}
