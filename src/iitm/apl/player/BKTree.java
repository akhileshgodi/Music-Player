package iitm.apl.player;

import java.util.HashMap;


import java.util.Vector;

/*
 * The Class which implements the operations on the BKTree
 */
public class BKTree {
	public Node root;
	private HashMap<Song, Integer> match;
	private HashMap<String, Integer> matches;

	private LevenshteinsDistance<String> distance;

	/*
	 * The class whose objects are the nodes in the TREE
	 */
	class Node {

		String songAtNode; 	// Will have the part of the Song Name.
		Vector<Song> songs; // The pointers to all songs which have that part of
							// the word in the song.
		HashMap<Integer, Node> children;// And obviously the set of all Nodes
										// which are it's children

		public Node(String splitWord, Song songTitle) {
			this.songAtNode = splitWord;
			children = new HashMap<Integer, BKTree.Node>();
			songs = new Vector<Song>();
		}

		/*--------------------------------------------------------------------------------------------------*/

		public void addToTree(String songPart, Song songTitle) {
			int levDistance = distance.getDistance(songPart.toLowerCase(),
					songAtNode);
			Node child = children.get(levDistance);
			if (child == null) 
			{
				Node addedNode = new Node(songPart, songTitle);
				this.children.put(levDistance, addedNode);
				addedNode.songs.add(songTitle);
				System.out.println(addedNode.songs);
				System.out.println("-------Added------");
			} 
			
			if(child!=null && child.songAtNode!= songPart)
				child.addToTree(songPart, songTitle);
			else if(child!=null && child.songAtNode == songPart)
				child.songs.add(songTitle);
		}


		public Vector<Song> querry(String element, int boundary,
				HashMap<String, Integer> match, Vector<Song> collectedObjs) {


			int distanceAtNode = distance.getDistance(element, this.songAtNode);

			if (distanceAtNode <= boundary) {
				match.put(this.songAtNode, distanceAtNode);
				
				for(int i = 0 ; i < songs.size(); i++)
					collectedObjs.addAll(songs);
			}

			for (int dist = distanceAtNode - boundary; dist <= boundary
					+ distanceAtNode; dist++) {
				Node child = children.get(dist);
				if (child != null) {
					child.querry(element, boundary, match , collectedObjs);
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
		for (int i = 0; i < split.length; i++) {

			// SongEntry entry = new SongEntry(element, split[i]);
			String part = split[i];
			boolean acceptable = true;
			int j;
			for (j = 0; j < part.length(); j++) {
				if (!((part.charAt(j) >= 'a' && part.charAt(j) <= 'z') || (part
						.charAt(j) >= '0' && part.charAt(j) <= '9'))) {
					acceptable = false;
					break;
				}

			}
			if (acceptable) {
				if (root != null)
					root.addToTree(part, element);
				else {
					root = new Node(part, element);
					System.out.println(root.songAtNode);
				}
			}
		}
		root.songs.add(element);
		root.addToTree(element.getTitle().toLowerCase(), element);
	}

	public Vector<Song> query(String search, int boundary) {
		matches = new HashMap<String, Integer>();
		Vector<Song> results = new Vector<Song>();
		root.querry(search, boundary, matches, results);
		System.out.println("Matches are :  " + results);
		return results;
	}

	public HashMap<Song, Integer> getMatch() {
		return match;
	}

}

