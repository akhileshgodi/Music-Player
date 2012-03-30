package iitm.apl.player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

public class searchSong {

	public static Vector<Song> search(String s, BKTree tree) {

		HashMap<Song, Integer> queryMap = tree.query(s, s.length());
		Vector<Song> queryResult = new Vector<Song>();

		ValueComparator compare = new ValueComparator(queryMap);
		TreeMap<Song, Integer> sorted_map = new TreeMap<Song, Integer>(compare);
		sorted_map.putAll(queryMap);

		for (Song it : sorted_map.keySet()) {
			// A problem for songs of smaller lengths
			if (sorted_map.get(it).intValue() <= 3)
				queryResult.add(it);
		}

		return queryResult;
	}
}

class ValueComparator implements Comparator<Song> {

	HashMap<Song, Integer> sort;

	public ValueComparator(HashMap<Song, Integer> sort) {
		this.sort = sort;
	}

	public int compare(Song a, Song b) {

		if (sort.get(a) < sort.get(b)) {
			return -1;
		} else if (sort.get(a) == sort.get(b)) {
			return 0;
		} else {
			return 1;
		}
	}
}
