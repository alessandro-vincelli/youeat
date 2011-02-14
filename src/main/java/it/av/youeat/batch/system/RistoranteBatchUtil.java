/**
 * 
 */
package it.av.youeat.batch.system;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 *
 */
public final class RistoranteBatchUtil {

    private RistoranteBatchUtil() {
    }

    /**
     * Remove S.R.L. S.n.C SRL SnC (S.N.C.) (S.A.S.) SAS S.A.S. () ( ) 
     * Remove space 
     * UpperCase only the first letter
     * 
     * @param name
     * @return clean risto name
     */
    public static String createName(String name) {
        String text = new String(name);
        text = StringUtils.normalizeSpace(text);
        text = StringUtils.lowerCase(text);
        for (String stringToRemove : textToRemove) {
            text = StringUtils.remove(text, stringToRemove);
        }
        text = StringUtils.normalizeSpace(text);
        text = WordUtils.capitalize(text);
        return text;
    }

    /**
     * Remove space UpperCase only the first letter
     * 
     * @param name
     * @return clean address
     */
    public static String createAddress(String name) {
        String text = new String(name);
        text = StringUtils.normalizeSpace(text);
        text = StringUtils.lowerCase(text);
        text = WordUtils.capitalize(text);
        return text;
    }

    private static final Set<String> textToRemove;
    static {
        Set<String> listRemove = new HashSet<String>();
        listRemove.add("s.r.l.");
        listRemove.add("s.n.c.");
        listRemove.add("srl");
        listRemove.add("snc");
        listRemove.add("s.a.s");
        listRemove.add("sas");
        listRemove.add("(sas)");
        listRemove.add("(s.a.s.)");
        listRemove.add("(srl)");
        listRemove.add("(s.r.l.)");
        listRemove.add("(snc)");
        listRemove.add("(s.n.c.)");
        listRemove.add("( )");
        listRemove.add("()");
        listRemove.add("ristorante");
        listRemove.add("pizzeria");
        textToRemove = Collections.unmodifiableSet(listRemove);
    }

}
