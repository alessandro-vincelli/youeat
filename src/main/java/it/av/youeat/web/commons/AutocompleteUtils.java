package it.av.youeat.web.commons;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;

public final class AutocompleteUtils {

    /**
     * 
     * @return common settings for autocomplete box
     */
    public final static AutoCompleteSettings getAutoCompleteSettings() {
        AutoCompleteSettings autoCompleteSettings = new AutoCompleteSettings();
        autoCompleteSettings.setCssClassName("autocomplete-risto");
        autoCompleteSettings.setAdjustInputWidth(false);
        return autoCompleteSettings;
    }

}
