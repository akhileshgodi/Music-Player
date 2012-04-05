package iitm.apl.player;

/*
 * This is a Class whose objects are stored as nodes in the BK Tree. 
 * This is a wrapper around the Song type to hold any name which is related to the Song.
 */
public class SongEntry {
	Song song;
	String name;

	public SongEntry(Song song, String name) {
		this.song = song;
		this.name = name;
	}
}
