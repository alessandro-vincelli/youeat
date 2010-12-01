package it.av.youeat.ocm.model;

import it.av.youeat.web.Locales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * RestaurateurBlackboardI18n represents a blackboard managed by a Restaurateur 
 * 
 * @author Alessandro Vincelli
 */
@Entity
public class RestaurateurBlackboardI18n extends BasicEntity {

    public static final String LANGUAGE = "language";
    public static final String BLACKBOARD = "blackboard";
    @ManyToOne
    private Language language;
    @Column(length = 10000)
    private String blackboard;

    /**
     * default construct
     */
    public RestaurateurBlackboardI18n() {
        super();
    }

    /**
     * Use the value in {@link Locales} to create the {@link Language}  
     * 
     * @param language the language to use for this blackboard
     */
    public RestaurateurBlackboardI18n(Language language) {
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
