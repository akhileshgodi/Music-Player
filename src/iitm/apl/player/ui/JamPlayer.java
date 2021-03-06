package iitm.apl.player.ui;

import iitm.apl.player.BKTree;

import iitm.apl.player.LevenshteinsDistance;
import iitm.apl.player.Song;
import iitm.apl.player.ThreadedPlayer;
import iitm.apl.player.SearchSong;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

/**
 * The JamPlayer Main Class Sets up the UI, and stores a reference to a threaded
 * player that actually plays a song.
 * 
 * TODO: 1. Make a BK Tree based on whatever radio button is chosen. and make a
 * new one every time its changed. 2. Sorting as desired as and when you click
 * the title bar. Display should be sorted by title.
 */
public class JamPlayer {

	// UI Items
	private JFrame mainFrame;
	private PlayerPanel pPanel;

	private JTable libraryTable;
	private LibraryTableModel libraryModel;

	private Thread playerThread = null;
	private ThreadedPlayer player = null;
	private String search = new String("");
	private Vector<Song> song;
	private static BKTree treeTitle = new BKTree(
			new LevenshteinsDistance<String>());
	private static BKTree treeAlbum = new BKTree(
			new LevenshteinsDistance<String>());
	private static BKTree treeArtist = new BKTree(
			new LevenshteinsDistance<String>());

	public JamPlayer() {
		// Create the player
		player = new ThreadedPlayer();
		playerThread = new Thread(player);
		playerThread.start();

		song = new Vector<Song>();
	}

	/**
	 * Create a file dialog to choose MP3 files to add
	 */
	private Vector<Song> addFileDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION)
			return null;

		File selectedFile = chooser.getSelectedFile();
		// Read files as songs
		Vector<Song> songs = new Vector<Song>();
		if (selectedFile.isFile()
				&& selectedFile.getName().toLowerCase().endsWith(".mp3")) {
			songs.add(new Song(selectedFile));

			return songs;
		} else if (selectedFile.isDirectory()) {
			for (File file : selectedFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".mp3");
				}
			}))
				songs.add(new Song(file));
		}

		for (Song it : songs) {
			song.add(it);
			treeTitle.add(it, 0);
			treeAlbum.add(it, 1);
			treeArtist.add(it, 2);
		}
		return songs;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		mainFrame = new JFrame("JamPlayer");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setMinimumSize(new Dimension(300, 400));

		// Create and set up the content pane.
		Container pane = mainFrame.getContentPane();
		pane.add(createMenuBar(), BorderLayout.NORTH);
		pane.add(Box.createHorizontalStrut(30), BorderLayout.EAST);
		pane.add(Box.createHorizontalStrut(30), BorderLayout.WEST);
		pane.add(Box.createVerticalStrut(30), BorderLayout.SOUTH);

		JPanel mainPanel = new JPanel();
		GroupLayout layout = new GroupLayout(mainPanel);
		mainPanel.setLayout(layout);

		pPanel = new PlayerPanel(player);

		JLabel searchLabel = new JLabel("Search: ");
		JTextField searchText = new JTextField(200);

		CheckboxGroup radioButtons = new CheckboxGroup();
		final Checkbox title = new Checkbox("Title", radioButtons, true);
		pPanel.add(title);
		final Checkbox album = new Checkbox("Album", radioButtons, false);
		pPanel.add(album);
		final Checkbox artist = new Checkbox("Artist", radioButtons, false);
		pPanel.add(artist);

		searchText.setMaximumSize(new Dimension(200, 20));
		searchText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char s = arg0.getKeyChar();
				boolean enter = true;
				if (s != '\r' && s != '\n') {
					enter = false;
					libraryModel.clear();
				}

				if (s != '\b' && !enter) {
					search = search.concat(s + "");

				} else if (!search.isEmpty() && !enter) {
					StringBuffer sb = new StringBuffer(search);
					sb.replace(search.length() - 1, search.length(), "");
					search = sb.toString();

				}
				// When no search text, display the entire playlist
				if (search.isEmpty() && !enter) {

					for (Song it : song)
						libraryModel.add(it);
				} else if (!enter) {
					Vector<Song> result = new Vector<Song>();
					String searchSplit[] = search.split("\\s+");
					int i;
					for (i = 0; i < searchSplit.length; i++) {
						Vector<Song> list = new Vector<Song>();
						if (title.getState() == true) {
							list = SearchSong.search(
									searchSplit[i].toLowerCase(), treeTitle);
						}
						if (artist.getState() == true) {
							list = SearchSong.search(
									searchSplit[i].toLowerCase(), treeArtist);
						}
						if (album.getState() == true) {
							list = SearchSong.search(
									searchSplit[i].toLowerCase(), treeAlbum);
						}
						if (i != 0) {
							int j;
							Vector<Song> temp = new Vector<Song>();
							for (j = 0; j < list.size(); j++) {
								if (result.contains(list.get(j)))
									temp.add(list.get(j));
							}
							result.clear();
							result = temp;
						} else
							result = list;
					}
					if (result != null)
						libraryModel.add(result);

				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});

		libraryModel = new LibraryTableModel();
		libraryTable = new JTable(libraryModel);
		libraryTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() > 1) {
					Song song = libraryModel.get(libraryTable.getSelectedRow());
					if (song != null) {
						player.setSong(song);
						pPanel.setSong(song);
					}
				}
			}
		});

		libraryTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane libraryPane = new JScrollPane(libraryTable);

		layout.setHorizontalGroup(layout
				.createParallelGroup(Alignment.CENTER)
				.addComponent(pPanel)
				.addGroup(
						layout.createSequentialGroup().addContainerGap()
								.addComponent(searchLabel)
								.addComponent(searchText).addContainerGap())
				.addComponent(libraryPane));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(pPanel)
				.addContainerGap()
				.addGroup(
						layout.createParallelGroup(Alignment.CENTER)
								.addComponent(searchLabel)
								.addComponent(searchText))
				.addComponent(libraryPane));

		pane.add(mainPanel, BorderLayout.CENTER);

		// Display the window.
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	private JMenuBar createMenuBar() {
		JMenuBar mbar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem addSongs = new JMenuItem("Add new files to library");
		addSongs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Vector<Song> songs = addFileDialog();
				if (songs != null)
					libraryModel.add(songs);
			}
		});
		file.add(addSongs);

		JMenuItem createPlaylist = new JMenuItem("Create playlist");
		createPlaylist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createPlayListHandler();
			}
		});
		file.add(createPlaylist);

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.dispose();
			}
		});
		file.add(quitItem);

		mbar.add(file);

		return mbar;
	}

	protected void createPlayListHandler() {
		// TODO: Create a dialog window allowing the user to choose length of
		// play list, and a play list you create that best fits the time
		// specified
		PlayListMakerDialog dialog = new PlayListMakerDialog(this);
		dialog.setVisible(true);
	}

	public Vector<Song> getSongList() {
		Vector<Song> songs = new Vector<Song>();
		for (int i = 0; i < libraryModel.getRowCount(); i++)
			songs.add(libraryModel.get(i));
		return songs;
	}

	public static <T> void main(String[] args) throws IOException {

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JamPlayer player = new JamPlayer();
				player.createAndShowGUI();
			}
		});
	}
}
