package TicTacToe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import TicTacToe.Square.Piece;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * This class is the game class. It does many things including getting user input for settings,
 * handling clicks and interfacing with the board class. It also updates the status bar with
 * appropriate information.
 */
public class TicTacToe {
	private HashMap<String, int[]> _playerRecord;
	private GameSetting _gameSetting;
	private PlayerSetting _playerSetting;
	private Pane _gamePane;
	private Board _board;
	private boolean _errorTriggered;
	private Player[] _players;
	private Player _playerToGo;
	private boolean _gameOver;
	private StatusBar _statusBar;
	// These four variables remember the settings to persist for later games.
	private String _playerOneName;
	private String _playerTwoName;
	private RadioButton _gameModeChoice;
	private RadioButton _playerModeChoice;
	
	/**
	 * This enum holds information about the setting the user chose for players.
	 */
	public enum PlayerSetting {
		HUMANVHUMAN, HUMANVCOMPUTER, COMPUTERVCOMPUTER
	}

	/**
	 * This enum encodes which game type the user chose.
	 */
	public enum GameSetting {
		BASIC, ULTIMATE
	}

	/**
	 * This constructor sets a number of flag variables to their initial states and sets up the top
	 * level graphical game pane.
	 */
	public TicTacToe() {
		_gameOver = false;
		_errorTriggered = false;
		// Holds game statistics for various players.
		_playerRecord = new HashMap<String, int[]>();

		_gamePane = new Pane();
		_gamePane.setStyle("-fx-background-color:black");
		_gamePane.getChildren().add(new Pane());
		// Makes sure the user's clicks are available for the game.
		_gamePane.setFocusTraversable(true);

		this.getUserInput();
	}

	/**
	 * This method visually sets up user input and calls a method to deal with processing user
	 * input. This method is quite long and could be split up into smaller methods but there are
	 * many many local variables which are required throughout. To make these private instance
	 * variables would be a waste as they are not required once the user's settings have been
	 * received.
	 */
	private void getUserInput() {
		// The next three blocks of code set up the panes for the settings panel.
		VBox settingsPane = new VBox();
		settingsPane.setStyle("-fx-background-color:white");
		HBox gameModePane = new HBox();
		HBox playerModePane = new HBox();
		HBox namesPane = new HBox();

		settingsPane.setAlignment(Pos.CENTER);
		gameModePane.setAlignment(Pos.CENTER);
		playerModePane.setAlignment(Pos.CENTER);
		namesPane.setAlignment(Pos.CENTER);
		settingsPane.setPrefWidth(Constants.GAME_SIDE_LENGTH);
		settingsPane.setPrefHeight(Constants.GAME_SIDE_LENGTH);
		settingsPane.setSpacing(100);

		gameModePane.setSpacing(60);
		playerModePane.setSpacing(60);
		namesPane.setSpacing(60);

		/*
		 * The next three blocks of code set up the input objects and groups those objects to
		 * appropriately process the data. ToggleGroups allow radio buttons to work together (i.e.
		 * only one radio button in a toggle group can be selected).
		 */
		ToggleGroup gameMode = new ToggleGroup();
		RadioButton gameMode1 = new RadioButton("Tic-Tac-Toe");
		RadioButton gameMode2 = new RadioButton("Ultimate Tic-Tac-Toe");
		gameMode1.setToggleGroup(gameMode);
		gameMode2.setToggleGroup(gameMode);
		gameModePane.getChildren().addAll(gameMode1, gameMode2);

		ToggleGroup playerMode = new ToggleGroup();
		RadioButton playerMode1 = new RadioButton("Human vs Human");
		RadioButton playerMode2 = new RadioButton("Human vs Computer");
		RadioButton playerMode3 = new RadioButton("Computer vs Computer");
		playerMode1.setToggleGroup(playerMode);
		playerMode2.setToggleGroup(playerMode);
		playerMode3.setToggleGroup(playerMode);

		TextField playerOneName = new TextField();
		playerOneName.setPromptText("Human 1's name");
		playerOneName.setPrefWidth(210);
		TextField playerTwoName = new TextField();
		playerTwoName.setPromptText("Human 2's name (if applicable)");
		playerTwoName.setPrefWidth(210);
		namesPane.getChildren().addAll(playerOneName, playerTwoName);
		
		// These if statements set the fields with data from last game.
		if(_playerOneName != null) {
			playerOneName.setText(_playerOneName);
		}
		if(_playerTwoName != null) {
			playerTwoName.setText(_playerTwoName);
		}
		if(_gameModeChoice != null) {
			// Finds equivalent toggle button and toggles it
			for(Toggle t: gameMode.getToggles()) {
				if(((RadioButton)(t)).getText() == _gameModeChoice.getText()) {
					gameMode.selectToggle(t);
				}
			}
		}
		if(_playerModeChoice != null) {
			// Finds equivalent toggle button and toggles it
			for(Toggle t: playerMode.getToggles()) {
				if(((RadioButton)(t)).getText() == _playerModeChoice.getText()) {
					playerMode.selectToggle(t);
				}
			}
		}
		
		// This submit button contains an anonymous function which processes the user's input
		Button submitButton = new Button("Play");
		submitButton.setOnAction(e -> {
			this.processUserInput(gameMode, gameMode1, gameMode2, playerMode, playerMode1, playerMode2, playerMode3,
					playerOneName, playerTwoName);
		});

		// These lines add the various panes to the scene graph.
		settingsPane.getChildren().addAll(gameModePane, playerModePane, namesPane, submitButton);
		_gamePane.getChildren().add(settingsPane);
		playerModePane.getChildren().addAll(playerMode1, playerMode2, playerMode3);
	}

	/**
	 * This method takes in the data the user entered into the form and processes it accordingly. It
	 * also checks for errors – does not submit the form if fields are empty.
	 */
	private void processUserInput(ToggleGroup gameMode, RadioButton gameMode1, RadioButton gameMode2,
			ToggleGroup playerMode, RadioButton playerMode1, RadioButton playerMode2, RadioButton playerMode3,
			TextField playerOneName, TextField playerTwoName) {
		// The flag for unfilled fields.
		boolean error = false;
		
		// Sets the game mode and if none selected, throws an error.
		if (gameMode.getSelectedToggle() == gameMode1) {
			_gameSetting = GameSetting.BASIC;
		} else if (gameMode.getSelectedToggle() == gameMode2) {
			_gameSetting = GameSetting.ULTIMATE;
		} else {
			error = true;
		}

		/*
		 * This if statement sets the player mode and for each mode checks that the appropriate name
		 * fields are filled. The .matches("//s*") code ensures that the user can't just enter
		 * spaces or tabs as a name.
		 * 
		 * For human vs human, both name fields must be filled.
		 */
		if (playerMode.getSelectedToggle() == playerMode1) {
			if (playerOneName.getText().matches("\\s*") || playerTwoName.getText().matches("\\s*")) {
				error = true;
			}
			_playerSetting = PlayerSetting.HUMANVHUMAN;

			// For human vs computer, only the first name field must be filled
		} else if (playerMode.getSelectedToggle() == playerMode2) {
			if (playerOneName.getText().matches("\\s*")) {
				error = true;
			}
			_playerSetting = PlayerSetting.HUMANVCOMPUTER;

			// For computer vs computer, no name fields have to be filled.
		} else if (playerMode.getSelectedToggle() == playerMode3) {
			_playerSetting = PlayerSetting.COMPUTERVCOMPUTER;
		} else {
			error = true;
		}

		// If there is no error, play the game, Otherwise add an error label.
		if (!error) {
			// Remembers settings for next game
			_gameModeChoice = (RadioButton)gameMode.getSelectedToggle();
			_playerModeChoice = (RadioButton)playerMode.getSelectedToggle();
			_statusBar.toggleVisibility();
			this.playGame(playerOneName.getText(), playerTwoName.getText());
		} else {
			Label errorLabel = new Label("Please select all options");
			errorLabel.setTranslateY(120);
			VBox errorLabelBox = new VBox();
			errorLabelBox.setPrefSize(Constants.GAME_SIDE_LENGTH, Constants.GAME_SIDE_LENGTH);
			errorLabelBox.setAlignment(Pos.CENTER);
			errorLabelBox.getChildren().add(errorLabel);
			errorLabelBox.setPickOnBounds(false);

			// Ensures that the visual components are only created once if the user triggers the
			// error multiple times.
			if (!_errorTriggered) {
				_gamePane.getChildren().add(errorLabelBox);
				_errorTriggered = true;
			}
		}
	}

	/**
	 * This method plays the game! It instantiates the Player class twice, depending on the game
	 * setting and randomly assigns a starting player. Then, if a computer is to play first, it
	 * triggers the computer's method to find a good move.
	 */
	private void playGame(String playerOneName, String playerTwoName) {
		// Sets these private instance variables so the text fields can be populated next game.
		_playerOneName = playerOneName;
		_playerTwoName = playerTwoName;
		
		// Gets rid of the settings pane
		_gamePane.getChildren().clear();

		_board = new Board(_gameSetting, false);
		_gamePane.getChildren().add(_board.getPane());

		Player pieceSetter;
		/*
		 * This switch statement sets up appropriate Players, depending on the game setting. The
		 * players are set up one at a time because the second player created needs to know the
		 * first's piece in order not to accidentally choose the same piece.
		 */
		switch (_playerSetting) {
		case HUMANVHUMAN:
			pieceSetter = new HumanPlayer(_gameSetting, playerOneName);
			_players = new Player[] { pieceSetter,
					new HumanPlayer(pieceSetter.getPiece(), _gameSetting, playerTwoName) };
			break;

		case HUMANVCOMPUTER:
			pieceSetter = new HumanPlayer(_gameSetting, playerOneName);
			_players = new Player[] { pieceSetter, new ComputerPlayer(pieceSetter.getPiece(), _gameSetting) };
			break;

		case COMPUTERVCOMPUTER:
			pieceSetter = new ComputerPlayer(_gameSetting);
			_players = new Player[] { pieceSetter, new ComputerPlayer(pieceSetter.getPiece(), _gameSetting) };
			break;
		}
		// Adds the click handler for squares
		this.addSquareEventHandler();
		// Randomly chooses first player.
		_playerToGo = _players[(new Random()).nextInt(2)];

		/*
		 * If a computer plays first, trigger its algorithm. Otherwise, do nothing move-wise because
		 * the play is triggered by a user click.
		 */
		if (_playerToGo.isComputer()) {
			this.updateStatusBar(true);
			((ComputerPlayer) (_playerToGo)).animateMove(this);
		} else {
			this.updateStatusBar(false);
		}
	}

	/**
	 * This method switches the player who is to go next. It involves highlighting legal moves if
	 * playing the Ultimate game and switches to a human player.
	 */
	public void switchPlayer() {
		boolean[] win = Board.checkWin(_board, _playerToGo.getPiece(), _gameSetting);
		// Doesn't switch player if someone has won the game.
		if (!win[0]) {
			// Flips the player.
			if (_players[0] == _playerToGo) {
				_playerToGo = _players[1];
			} else {
				_playerToGo = _players[0];
			}

			// If the player is a computer, call its method to choose a move.
			if (_playerToGo.isComputer()) {
				if (!_gameOver) {
					this.updateStatusBar(true);
					((ComputerPlayer) (_playerToGo)).animateMove(this);
				}

				// If the player is not a computer, highlight legal moves and just chill out until
				// they select a move.
			} else {
				this.updateStatusBar(false);
				this.highlightLegalMoves();
			}
		} else {
			// If game is won, trigger game over.
			this.gameOver(win[1], _board);
		}
	}

	/**
	 * This method highlights legal moves only in the Ultimate game to help the human know where to
	 * click.
	 */
	public void highlightLegalMoves() {
		// Doesn't trigger on the first move so as not to have the board blue and then change to red
		// once and forever.
		if (!_board.getIsFirstMove() && _gameSetting == GameSetting.ULTIMATE) {
			ArrayList<int[]> legalMoves = _board.getLegalMoves();

			for (int[] e : legalMoves) {
				_board.getBoardArray()[e[0]][e[1]].toggleHighlight();
			}
		}
	}

	/**
	 * This method is triggered when there is a game over and visually displays a message. It also
	 * sets up the event handler to allow the user to restart with a keypress.
	 */
	private void gameOver(boolean draw, Board board) {
		_statusBar.setText("Congratulations!");
		_gameOver = true;

		// Makes three labels for the game over message.
		Label gameOverLabel = new Label();
		Label restartLabel = new Label("Press any key to restart.");
		Label statsLabel = new Label(this.queryPlayerStats(draw));

		// Makes a pane to hold the message.
		VBox gameOverPane = new VBox();
		gameOverPane.setSpacing(50);
		gameOverPane.setAlignment(Pos.CENTER);
		gameOverPane.setPrefSize(Constants.GAME_SIDE_LENGTH, Constants.GAME_SIDE_LENGTH);
		gameOverPane.setId("game-over-pane");

		_gamePane.getChildren().add(gameOverPane);
		// These lines add the labels to the gameOverPane but only adds a stats label if a human is
		// playing.
		gameOverPane.getChildren().add(gameOverLabel);
		if (statsLabel.getText() != null) {
			gameOverPane.getChildren().add(statsLabel);
		}
		gameOverPane.getChildren().add(restartLabel);

		// Adds CSS classes to the three labels.
		gameOverLabel.getStyleClass().add("game-over-label");
		restartLabel.getStyleClass().add("game-over-label");
		statsLabel.getStyleClass().add("game-over-label");
		
		// Sets the text of the label, depending on who won.
		if (!draw) {
			if (_playerSetting != PlayerSetting.COMPUTERVCOMPUTER) {
				gameOverLabel.setText(_playerToGo.getName() + " wins!");
			} else {
				gameOverLabel.setText(_playerToGo.getPiece() + " wins!");
			}
		} else {
			gameOverLabel.setText("It's a draw!");
		}

		// Adds the event handler to restart if a key is pressed.
		_gamePane.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			this.restart();
		});
	}

	/**
	 * This method both adds statistics to the record and recalls the statistics to display to the
	 * user.
	 */
	private String queryPlayerStats(boolean draw) {
		// This loop adds the game over to the participating players' statistics.
		for (int i = 0; i < 2; i++) {
			// Computers don't get statistics.
			if (!_players[i].isComputer()) {
				// Adds the player if they are not already in the hash map.
				if (!_playerRecord.containsKey(_players[i].getName())) {
					_playerRecord.put(_players[i].getName(), new int[] { 0, 0 });
				}
				// Adds 1 to the wins if the player won and adds 1 to the number of games played either way.
				if(_players[i] == _playerToGo && !draw) {
					int[] stats = _playerRecord.get(_players[i].getName());
					_playerRecord.put(_players[i].getName(), new int[]{stats[0]+1, stats[1]+1});
				} else {
					int[] stats = _playerRecord.get(_players[i].getName());
					_playerRecord.put(_players[i].getName(), new int[]{stats[0], stats[1]+1});
				}
			}
		}
		
		// Three cases depending on the players – the computer player(s) don't get statistics shown.
		if(_playerSetting == PlayerSetting.HUMANVHUMAN) {
			// If human vs human, display both player's stats.
			return _players[0].getName()+"'s score: "+_playerRecord.get(_players[0].getName())[0]
					+" out of "+_playerRecord.get(_players[0].getName())[1]
					+"\n"+_players[1].getName()+"'s score: "
					+_playerRecord.get(_players[1].getName())[0]+" out of "
					+_playerRecord.get(_players[1].getName())[1];
		} else if(_playerSetting ==  PlayerSetting.HUMANVCOMPUTER) {
			// This if statement finds the human player and displays their statistics.
			if(!_players[0].isComputer()) {
				return _players[0].getName() + "'s score: "+_playerRecord.get(_players[0].getName())[0]
						+" out of "+_playerRecord.get(_players[0].getName())[1]+"";
			} else {
				return _players[1].getName() + "'s score: "+_playerRecord.get(_players[1].getName())[0]
						+" out of "+_playerRecord.get(_players[1].getName())[1]+"";
			}
		} else {
			// Computer vs computer displays nothing.
			return null;
		}
	}

	/**
	 * This method restarts the game by clearing the game pane and resetting certain variables to
	 * their inital states. Finally, it calls this.getUserInput() to cause the settings pane to
	 * reappear again.
	 */
	public void restart() {
		if (_gameOver) {
			_gamePane.getChildren().clear();
			_gameOver = false;
			_board.setIsFirstMove(true);
			_statusBar.toggleVisibility();
			this.getUserInput();
		}
	}

	/**
	 * This method adds a ClickHandler event handler to every square on the board.
	 */
	public void addSquareEventHandler() {
		Square[][] boardArray = _board.getBoardArray();
		for (int i = 0; i < Board.findBoardSize(_gameSetting); i++) {

			for (int j = 0; j < Board.findBoardSize(_gameSetting); j++) {

				boardArray[i][j].getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new ClickHandler(this));
			}
		}
	}

	/**
	 * This method gets the instance of the board class in the game.
	 */
	public Board getBoard() {
		return _board;
	}

	/**
	 * This method gets the top level game pane.
	 */
	public Pane getPane() {
		return _gamePane;
	}

	/**
	 * This method gets the game setting (i.e. Basic or Ultimate).
	 */
	public GameSetting getGameSetting() {
		return _gameSetting;
	}

	/**
	 * This static method returns the opposite of a piece.
	 */
	public static Piece flipPiece(Piece piece) {
		if (piece == Piece.X) {
			return Piece.O;
		} else if (piece == Piece.O) {
			return Piece.X;
		} else {
			return null;
		}
	}

	/**
	 * This method gives the TicTacToe class knowledge of the status bar so that it can set its
	 * label text.
	 */
	public void setStatusBar(StatusBar statusBar) {
		_statusBar = statusBar;
	}

	/**
	 * This method updates the status bar, depending on which player is playing. It also holds the
	 * names and pieces of the two players.
	 */
	private void updateStatusBar(boolean computerIsThinking) {
		String moveString;
		// Gets an indication of who is moving.
		if (!computerIsThinking) {
			moveString = _playerToGo.getName() + " to go!";
		} else {
			moveString = "Computer is thinking...";
		}

		// Adds the move string to a static section indicating who is who.
		_statusBar.setText(moveString + "   |   " + _players[0].getPiece() + " = " + _players[0].getName() + ", "
				+ _players[1].getPiece() + " = " + _players[1].getName() + "");
	}

	/**
	 * This inner class is for an event handler added to each square of the board. It triggers the
	 * HumanPlayer's evaluateMove() method to check if the clicked square is a legal move.
	 */
	private class ClickHandler implements EventHandler<MouseEvent> {
		private TicTacToe _ticTacToe;

		public ClickHandler(TicTacToe ticTacToe) {
			_ticTacToe = ticTacToe;
		}

		public void handle(MouseEvent e) {
			// If it is the computer's turn or a game over, do not allow play./
			if (!_playerToGo.isComputer() && !_gameOver) {
				((HumanPlayer) (_playerToGo)).evaluateMove(_ticTacToe, (StackPane) (e.getSource()));
			}
		}
	}
}
