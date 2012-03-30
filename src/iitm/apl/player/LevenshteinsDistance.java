package iitm.apl.player;

/**
 * @author : Akhilesh Godi (CS10B037) Implementing the Levenshtein's distance
 */
public class LevenshteinsDistance<E> {
	public int getDistance(E element, E element2) {

		String string1 = (String) element;
		String string2 = (String) element2;

		int distance[][]; // Distance Matrix
		int n, m; // Length of first , second strings respectively
		int i, j; // Iterators for the first and second string respectively.

		/*
		 * Consider two arrays S and T to be treated as first and second
		 * respectively.
		 */

		n = string1.length();
		m = string2.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		distance = new int[n + 1][m + 1];

		for (i = 0; i <= n; i++)
			distance[i][0] = i;
		for (j = 0; j <= m; j++)
			distance[0][j] = j;

		for (i = 1; i <= n; i++) {
			for (j = 1; j <= m; j++) {
				if (string1.charAt(i - 1) == string2.charAt(j - 1))
					distance[i][j] = distance[i - 1][j - 1];
				else
					distance[i][j] = findMinimum(distance[i - 1][j] + 1,
							distance[i][j - 1] + 1, distance[i - 1][j - 1] + 1);
			}
		}

		return distance[n][m];
	}

	private int findMinimum(int a, int b, int c) {
		return Math.min(a, Math.min(b, c));
	}

}
