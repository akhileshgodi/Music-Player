package iitm.apl.player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

/*
 * Class which implements the search by invoking the call to the BKTree
 */
public class SearchSong {
	/*
	 * This takes a String searches in the BKTree for the closest match
	 * available.
	 * 
	 * @params : String, BKTree
	 * 
	 * @return: Vector<Song>
	 */

	public static Vector<Song> search(String s, BKTree tree) 
	{

		HashMap<Song, Integer> queryMap = tree.query(s, s.length());
		Vector<Song> queryResult = new Vector<Song>();

		ValueComparator compare = new ValueComparator(queryMap);
		TreeMap<Song, Integer> sorted_map = new TreeMap<Song, Integer>(compare);
		sorted_map.putAll(queryMap);

		for (Song it : sorted_map.keySet()) {

			if (sorted_map.get(it).intValue() <= 1)
				queryResult.add(it);
		}
		System.out.println(queryResult);
		return queryResult;
	}
}

/*
 * A comparator class which compares two songs based on the Edit distance from a
 * given String
 */
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
