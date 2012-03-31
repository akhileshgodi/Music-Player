package iitm.apl.player;

import java.util.HashMap;

import java.util.HashSet;
import java.util.Set;

public class BKTree {
	private Node root;
	private HashMap<Song, Integer> match;
	private LevenshteinsDistance<String> distance;

	private class Node {
		Song element;
		HashMap<Integer, Node> children;

		public Node(Song element2) {

			this.element = element2;
			children = new HashMap<Integer, Node>();
		}

		public void addToTree(Song element) {

			int levDistance = distance.getDistance(element.getTitle()
					.toLowerCase().split("\\s+")[0], this.element.getTitle()
					.toLowerCase().split("\\s+")[0]);
			Node child = children.get(levDistance);

			if (child == null)
				children.put(levDistance, new Node(element));
			else
				child.addToTree(element);
		}

		public Set<Song> query(String element, int boundary,
				HashMap<Song, Integer> match) {
			int distanceAtNode = distance.getDistance(element, this.element
					.getTitle().toLowerCase().split("\\s+")[0]);
			Set<Song> collectedObjs = new HashSet<Song>();
			if (distanceAtNode <= boundary) {
				match.put(this.element, distanceAtNode);
				collectedObjs.add(this.element);

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
		if (root != null)
			root.addToTree(element);
		else
			root = new Node(element);
	}

	public HashMap<Song, Integer> query(String search, int boundary) {
		match = new HashMap<Song, Integer>();
		root.query(search, boundary, match);
		return match;
	}

}
