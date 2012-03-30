package iitm.apl.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

public class searchSong {
	public static Vector<Song> search(String s, BKTree tree) {

		HashMap<Song, Integer> queryMap = tree.query(s, s.length());
		Vector<Song> queryResult = new Vector<Song>();
		Set<Map.Entry<Song, Integer>> result = queryMap.entrySet();
		for (Entry<Song, Integer> it : result) {
			// Currently blindly everything a a edit distance of 3 being
			// displayed
			if (it.getValue().intValue() <= 3)
				queryResult.add(it.getKey());
		}
		return queryResult;
	}
}
