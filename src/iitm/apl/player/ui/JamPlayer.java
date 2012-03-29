package iitm.apl.player.ui;

import iitm.apl.player.BKTree;

import iitm.apl.player.LevenshteinsDistance;
import iitm.apl.player.Song;
import iitm.apl.player.ThreadedPlayer;
import iitm.apl.player.searchSong;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

/**
 * The JamPlayer Main Class Sets up the UI, and stores a reference to a threaded
 * player that actually plays a song.
 * 
 * TODO: a) Implement the search functionality b) Implement a play-list
 * generation feature
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
	private static BKTree<String> tree = new BKTree<String>(
			new LevenshteinsDistance<String>());

	public JamPlayer() {
		// Create the player
		player = new ThreadedPlayer();
		playerThread = new Thread(player);
		playerThread.start();
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
		searchText.setMaximumSize(new Dimension(200, 20));
		searchText.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				char s = arg0.getKeyChar();
				if (s != '\b')
					search = search.concat(s + "");
				else {
					StringBuffer sb = new StringBuffer(search);
					sb.replace(search.length() - 1, search.length(), "");
					search = sb.toString();
				}
				if (!search.isEmpty()) {
					Vector<String> result = searchSong.search(search, tree);
					for (String it : result) {
						System.out.println(it);
					}
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
		/* This is just a test run. @Karthik : Remove these lines later. */

		File input = new File("./test.txt");
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = null;

		while ((line = reader.readLine()) != null) {
			tree.add(line);
		}
		reader.close();

		HashMap<String, Integer> queryMap = tree.query("remote", 6);
		System.out.println("Query Map : " + queryMap);
		/*-------------------------------------------------------------*/

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
