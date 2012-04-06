package iitm.apl.player;

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

	public static Vector<Song> search(String s, BKTree tree) {

		Vector<Song> queryResult = tree.query(s, 2);

		return queryResult;
	}
}
