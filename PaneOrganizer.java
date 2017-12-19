package TicTacToe;

import javafx.scene.layout.Pane;

/**
 * This class organises top level visual panes for the game. Also instantiates TicTacToe, the game's
 * top level object. StatusBar is a class which deals with the status bar at the bottom of the
 * screen.
 */
public class PaneOrganizer {
	private Pane _root;

	public PaneOrganizer() {
		_root = new Pane();

		// Sets background as white so when the status bar is hidden, there isn't a grey (default
		// colour) strip left.
		_root.setStyle("-fx-background-color:white");

		TicTacToe ticTacToe = new TicTacToe();
		StatusBar statusBar = new StatusBar();

		// TicTacToe needs to have knowledge of status bar so that it can set the text when game
		// state changes.
		ticTacToe.setStatusBar(statusBar);

		_root.getChildren().addAll(ticTacToe.getPane(), statusBar.getPane());
	}

	/**
	 * Gets top level node (used by instantiation of Scene in App class).
	 */
	public Pane getRoot() {
		return _root;
	}
}
