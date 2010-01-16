package it.av.youeat.util;

import java.util.regex.Pattern;

public class LuceneUtil {

    private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\*\\?]";
    // private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
    private static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_ESCAPE_CHARS);
    private static final String REPLACEMENT_STRING = "\\\\$0";

    /**
     * Remove special characters not valid in Lucene query
     * 
     * @param textToClean
     * @return the string cleaned
     */
    public static final String removeSpecialChars(String textToClean) {
        return textToClean.replaceAll(LUCENE_ESCAPE_CHARS, textToClean);
    }

    /**
     * Escape special characters not valid in Lucene query
     * 
     * @param textToClean
     * @return the string cleaned
     */
    public static final String escapeSpecialChars(String textToClean) {
        return LUCENE_PATTERN.matcher(textToClean).replaceAll(REPLACEMENT_STRING);

    }
}
