package it.av.youeat.web;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Locales {

    /**
     * Useful constant for language.
     */
    public static final Locale ENGLISH = new Locale("en", "US");

    /**
     * Useful constant for language.
     */
    public static final Locale DUTCH = new Locale("nl", "NL");

    /**
     * Useful constant for language.
     */
    public static final Locale ITALIAN = new Locale("it", "IT");

    /**
     * The default locale, used when no or unsupported locale is requested.
     */
    private static final Locale DEFAULT_LOCALE = ENGLISH;

    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(DUTCH, ENGLISH, ITALIAN);

    /**
     * Tries to find a supported locale that best matches the requested locale.
     * 
     * @param requestedLocale the requested locale or null
     * @return a supported locale
     */
    public static Locale getSupportedLocale(Locale requestedLocale) {
        if (requestedLocale == null) {
            return DEFAULT_LOCALE;
        }

        if (SUPPORTED_LOCALES.contains(requestedLocale)) {
            return requestedLocale;
        }

        // Find first supported locale that has the same language.
        String requestedLanguage = requestedLocale.getLanguage();
        for (Locale supportedLocale : SUPPORTED_LOCALES) {
            if (supportedLocale.getLanguage().equals(requestedLanguage)) {
                return supportedLocale;
            }
        }

        return DEFAULT_LOCALE;
    }
    
    /**
     * Tries to find a supported locale that best matches the requested language.
     * 
     * @param language the requested language or null
     * @return a supported locale
     */
    public static Locale getSupportedLocale(String language) {
        if (language == null || language.isEmpty()) {
            return DEFAULT_LOCALE;
        }
        return getSupportedLocale(new Locale(language));
    }
}
