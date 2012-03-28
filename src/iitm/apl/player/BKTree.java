package iitm.apl.player;

import java.util.HashMap;


import java.util.HashSet;
import java.util.Set;

import java.util.Map;

public class BKTree <T>
{
	private Node root; 							//Root of the node
	private HashMap<T, Integer> match; 		    //Match to the string! Infact make it generic
	private LevenshteinsDistance<T> distance;			
	
	/*---------------------------------------------------*/
	//This is the node class and inside this 
	//operations that are to be done on elements in the tree.
	private class Node
	{
		T element;
		HashMap <Integer, Node> children;		// Has children with different distances in a map.
		public Node(T element) 
		{
			// TODO Auto-generated constructor stub
			this.element = element;
			children = new HashMap<Integer, Node>();
		}

		public void addToTree(T element) 
		{
			// TODO Auto-generated method stub
			int levDistance = distance.getDistance(element, this.element);	
			Node child = children.get(levDistance);
			
			if(child == null)
				children.put(levDistance, new Node(element));
			else child.addToTree(element);
		}
		
		

		public Set<T> query(T element, int boundary, Map<T, Integer> match) 
		{
			// TODO Auto-generated method stub
			int distanceAtNode = distance.getDistance(element, this.element);
			Set <T> collectedObjs = new HashSet<T>();
			if(distanceAtNode <= boundary) 
			{
				match.put(this.element, distanceAtNode);
				collectedObjs.add(this.element);
				System.out.println(collectedObjs);		//How do we sort it then? We need dist. So we'll have to keep a track of that also.
			}

			for (int dist = distanceAtNode-boundary; dist <= boundary+distanceAtNode; dist++) 
			{
				Node child = children.get(dist);
				if(child != null) 
				{
					child.query(element, boundary, match);
				}
			}
			return collectedObjs;	//TODO : Sort these objects based on their Leveinshteen's Distance
		}
		
	}
	/*------------------------------------------------------*/
	
	
	public BKTree(LevenshteinsDistance<T> distance) 
	{
		root = null;
		this.distance = distance;
	}

	public void add(T element) 
	{
		if(root != null) 
			root.addToTree(element);
		else 
			root = new Node(element);
	}
	
	public HashMap<T, Integer> query(T search, int boundary) 
	{
		match = new HashMap<T,Integer>();
		root.query(search, boundary, match);
		return match;
	}

}

