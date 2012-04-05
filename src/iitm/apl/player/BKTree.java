package iitm.apl.player;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Set;

/*
 * The Class which implements the operations on the BKTree
 */
public class BKTree {
	private Node root;
	private HashMap<Song, Integer> match;
	private LevenshteinsDistance<String> distance;

	/*
	 * The class whose objects are the nodes in the TREE
	 */
	private class Node {

		SongEntry element;
		HashMap<Integer, Node> children;

		public Node(SongEntry element2) {

			this.element = element2;
			children = new HashMap<Integer, Node>();
		}

		public void addToTree(SongEntry element) {

			int levDistance = distance.getDistance(element.name.toLowerCase(),
					this.element.name);
			Node child = children.get(levDistance);

			if (child == null)
				children.put(levDistance, new Node(element));
			else

				child.addToTree(element);

		}

		public Set<Song> query(String element, int boundary,
				HashMap<Song, Integer> match) {

			Set<Song> collectedObjs = new HashSet<Song>();

			int distanceAtNode = distance.getDistance(element,
					this.element.name);

			if (distanceAtNode <= boundary) {
				match.put(this.element.song, distanceAtNode);
				collectedObjs.add(this.element.song);

			}

			for (int dist = distanceAtNode - boundary; dist <= boundary
					+ distanceAtNode; dist++) {
				Node child = children.get(dist);
				if (child != null) {
					child.query(element, boundary, match);
				}
			}

			return collectedObjs;
		}
	}

	public BKTree(LevenshteinsDistance<String> distance) {
		root = null;
		this.distance = distance;
	}

	public void add(Song element) {
		String split[] = element.getTitle().toLowerCase().split("\\s+");
		int i;
		for (i = 0; i < split.length; i++) {
			SongEntry entry = new SongEntry(element, split[i]);
			boolean acceptable = true;
			int j;
			for (j = 0; j < entry.name.length(); j++) {

				if (!((entry.name.charAt(j) >= 'a' && entry.name.charAt(j) <= 'z') || (entry.name
						.charAt(j) >= '0' && entry.name.charAt(j) <= '9'))) {
					acceptable = false;
					break;
				}

			}
			if (acceptable) {
				if (root != null) {
					System.out.println(entry.name);
					root.addToTree(entry);
				} else
					root = new Node(entry);

			}
		}
		root.addToTree(new SongEntry(element, element.getTitle().toLowerCase()));
	}

	public HashMap<Song, Integer> query(String search, int boundary) {
		match = new HashMap<Song, Integer>();
		root.query(search, boundary, match);
		return match;
	}

}
