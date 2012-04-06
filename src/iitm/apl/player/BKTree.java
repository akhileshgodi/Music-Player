package iitm.apl.player;

import java.util.HashMap;

import java.util.Vector;

/*
 * The Class which implements the operations on the BKTree
 */
public class BKTree {
	public Node root;
	private LevenshteinsDistance<String> distance;

	/*
	 * The class whose objects are the nodes in the TREE
	 */
	class Node {

		String songAtNode;
		Vector<Song> songs;
		HashMap<Integer, Node> children;

		public Node(String splitWord, Song songTitle) {
			this.songAtNode = splitWord;
			children = new HashMap<Integer, BKTree.Node>();
			songs = new Vector<Song>();
		}

		public void addToTree(String songPart, Song songTitle) {
			int levDistance = distance.getDistance(songPart, songAtNode);
			Node child = children.get(levDistance);
			if (child == null) {
				Node addedNode = new Node(songPart, songTitle);
				this.children.put(levDistance, addedNode);
				addedNode.songs.add(songTitle);

			}

			if (child != null && child.songAtNode != songPart)
				child.addToTree(songPart, songTitle);
			else if (child != null && child.songAtNode == songPart)
				child.songs.add(songTitle);
		}

		public void query(String element, int boundary,
				Vector<Song> collectedObjs) {

			int distanceAtNode = distance.getDistance(element, this.songAtNode);

			if (distanceAtNode <= boundary) {
				Vector<Song> temp = new Vector<Song>();
				temp.addAll(this.songs);
				for (int j = 0; j < songs.size(); j++) {
					if (!collectedObjs.contains(temp.elementAt(j)))
						collectedObjs.add(temp.elementAt(j));
				}

			}

			for (int dist = distanceAtNode - boundary; dist <= boundary
					+ distanceAtNode; dist++) {
				Node child = children.get(dist);
				if (child != null) {
					child.query(element, boundary, collectedObjs);
				}
			}

		}
	}

	public BKTree(LevenshteinsDistance<String> distance) {
		root = null;
		this.distance = distance;
	}

	public void add(Song element, int mode) {

		String split[];
		if (mode == 1)
			split = element.getAlbum().toLowerCase().split("\\s+");
		else if (mode == 2)
			split = element.getArtist().toLowerCase().split("\\s+");
		else
			split = element.getTitle().toLowerCase().split("\\s+");
		for (int i = 0; i < split.length; i++) {

			if (isAlphaNum(split[i])) {
				if (root != null)
					root.addToTree(split[i].toLowerCase(), element);
				else {
					root = new Node(split[i].toLowerCase(), element);
					root.songs.add(element);

				}
			}
		}
		root.addToTree(element.getTitle().toLowerCase(), element);
	}

	public Vector<Song> query(String search, int boundary) {
		Vector<Song> results = new Vector<Song>();
		root.query(search, boundary, results);
		return results;
	}

	private boolean isAlphaNum(String s) {
		s = s.toLowerCase();
		for (int j = 0; j < s.length(); j++) {
			if (!((s.charAt(j) >= 'a' && s.charAt(j) <= 'z') || (s.charAt(j) >= '0' && s
					.charAt(j) <= '9')))
				return false;

		}
		return true;
	}
}
