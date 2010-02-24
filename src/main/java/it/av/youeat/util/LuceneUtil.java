package it.av.youeat.util;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class LuceneUtil {

    private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\*\\?]";
    // private static final String LUCENE_ESCAPE_CHARS = "[\\\\+\\-\\!\\(\\)\\:\\^\\]\\{\\}\\~\\*\\?]";
    private static final Pattern LUCENE_PATTERN = Pattern.compile(LUCENE_ESCAPE_CHARS);
    private static final String REPLACEMENT_STRING = "\\\\$0";

    private LuceneUtil() {
    }

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

    /**
     * Create a fuzzy query on the given pattern:<br>
     * sample: pattern: Alesssandro Vincelli = Alesssandro~Vincelli~
     * 
     * @param pattern
     * @return fuzzy query
     */
    public static final String fuzzyAllTerms(String pattern) {
        String[] patternSplittedt = StringUtils.split(pattern, null);
        StringBuffer fuzzy = new StringBuffer();
        for (int i = 0; i < patternSplittedt.length; i++) {
            fuzzy.append(patternSplittedt[i]);
            fuzzy.append("~");
        }
        return fuzzy.toString();
    }
}
