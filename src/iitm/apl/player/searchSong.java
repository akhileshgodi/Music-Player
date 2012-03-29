package iitm.apl.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class searchSong {
	public static Vector<String> search(String s, BKTree<String> tree) {

		HashMap<String, Integer> queryMap = tree.query(s, s.length());
		Vector<String> queryResult = new Vector<String>();
		Set<Map.Entry<String, Integer>> result = queryMap.entrySet();
		for (Entry<String, Integer> it : result) {
			if (it.getValue().intValue() <= 3)
				queryResult.add(it.getKey());
		}
		return queryResult;
	}
}
