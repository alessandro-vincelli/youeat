package it.av.eatt.web.util;

import it.av.eatt.JackWicketException;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.util.diff.Diff;
import org.apache.wicket.util.diff.DifferentiationFailedException;
import org.apache.wicket.util.diff.Revision;

/**
 * Util class to render in Html RCS differences 
 * @author <a href='mailto:alessandro.vincelli@ictu.nl'>Alessandro Vincelli</a>
 * 
 */
public class TextDiffRender {
	private static final String OP_DELETED = "d";
	private static final String OP_ADDED = "a";
	private static final String OP_CHANGED = "c";

	/**
	 * Return an array with 2 element
	 * [0] Original Text with fancy changed elements
	 * [1] new Version Text with fancy changed elements
	 * 
	 * @param ori
	 * @param newVer
	 * @return String[]
	 * @throws FeedException 
	 */
	public String[] render(String ori, String newVer) throws JackWicketException {
		if(ori == null){
			ori = "";
		}
		if(newVer == null){
			newVer = "";
		}
		String[] oriArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(ori, null);
		String[] newRevArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(newVer, null);
		Diff diff = new Diff(oriArray);
		try {
			Revision rev;
			rev = diff.diff(newRevArray);
			String[] rcs = StringUtils.split(rev.toRCSString(), "\n");
			for (int i = 0; i < rcs.length; i++) {
				String item = rcs[i];
				String[] itemArray = StringUtils.split(item);
				if (itemArray.length > 1) {
					String operation = getOperation(item);
					int position = Integer.parseInt(itemArray[0].substring(1)) > 0 ? Integer.parseInt(itemArray[0].substring(1)) - 1: 0;
					int iterations = itemArray.length > 1 ? Integer.parseInt(itemArray[1]) : 0;
					if (operation.equals(OP_DELETED)) {
						for (int xlen = 0; xlen < iterations; xlen++) {
							oriArray[position + xlen] = "<span style=\"background-color:#FF9999;\" title=\"Deleted\">" + oriArray[position + xlen] + "</span>";
						}
					}
					if (operation.equals(OP_ADDED)) {
						for (int xlen = 0; xlen < iterations; xlen++) {
							newRevArray[position + xlen] = "<span style=\"background-color:#99FF99;\" title=\"Added\">" + newRevArray[position + xlen] + "</span>";
						}
					}
				}
			}
			ori = StringUtils.join(oriArray, " ");
			newVer = StringUtils.join(newRevArray, " ");
		} catch (DifferentiationFailedException e) {
			throw new JackWicketException(e);
		}
		return new String[] {ori, newVer};
	}

	private String getOperation(String item) {
		String[] itemArray = StringUtils.split(item);
		if (itemArray.length == 1) {
			return OP_CHANGED;
		}
		String operation = itemArray[0].substring(0, 1);
		if (operation.equals("d")) {
			return OP_DELETED;
		}
		if (operation.equals("a")) {
			return OP_ADDED;
		}
		return null;
	}

}
