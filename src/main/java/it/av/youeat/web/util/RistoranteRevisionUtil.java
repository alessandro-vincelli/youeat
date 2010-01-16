package it.av.youeat.web.util;

import it.av.youeat.ocm.model.RistoranteRevision;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href='mailto:a.vincelli@gmail.com'>Alessandro Vincelli</a>
 * 
 */
public class RistoranteRevisionUtil {

	/**
	 * Return a list deep cloned
	 * 
	 * @param list
	 * @return cloned List<RistoranteRevision>
	 */
	public static List<RistoranteRevision> cloneList(List<RistoranteRevision> list) {
		List<RistoranteRevision> clone = new ArrayList<RistoranteRevision>(list.size());
		for (RistoranteRevision item : list){
			if(item != null){
				clone.add(item.clone());	
			}
		}
		return clone;
	}

}
