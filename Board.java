package TicTacToe;

import TicTacToe.TicTacToe.GameSetting;
import java.util.ArrayList;
import TicTacToe.Square.Piece;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * This class contains the game board and associated methods. Can be used for the actual, visual
 * game board or for dummy boards for the algorithm to use to determine good moves.
 */
public class Board {
	private Square[][] _board;
	private Pane _boardPane;
	private int[] _previousMove;
	private Square[][] _boardsWon;
	private GameSetting _gameSetting;
	private boolean _isDummyBoard;
	private boolean _isFirstMove;

	/**
	 * This enum describes possible game states. NOWIN means the game is still ongoing.
	 */
	public enum GameState {
		WIN, LOSE, NOWIN, DRAW
	}

	/**
	 * This constructor sets up some flags and then calls setUpBoard to set up the board.
	 */
	public Board(GameSetting gameSetting, boolean isDummyBoard) {
		_isDummyBoard = isDummyBoard;
		_gameSetting = gameSetting;
		_isFirstMove = true;
		this.setUpBoard();
	}

	/**
	 * This is a copy constructor, which does the same as the normal constructor except it copies
	 * the data from the Board parameter to itself.
	 */
	public Board(GameSetting gameSetting, boolean isDummyBoard, Board board) {
		_isDummyBoard = isDummyBoard;
		_gameSetting = gameSetting;
		_isFirstMove = board.getIsFirstMove();
		this.setUpBoard();

		// This loop copies the state of each individual square on the board.
		for (int i = 0; i < Board.findBoardSize(_gameSetting); i++) {
			for (int j = 0; j < Board.findBoardSize(_gameSetting); j++) {
				this.getBoardArray()[i][j].copySquare(board.getBoardArray()[i][j]);

			}
		}

		_previousMove = board.getPreviousMove();

		if (gameSetting == GameSetting.ULTIMATE) {
			this.setBoardsWon(board.getBoardsWon());
		}
	}

	/**
	 * This method graphically sets up the board, as well as creating logical data structures for
	 * the board. A lot of it is not run if the board is a dummy board because the dummy board is
	 * never seen visually.
	 */
	private void setUpBoard() {
		// Makes board array out of Squares.
		_board = new Square[Board.findBoardSize(_gameSetting)][Board.findBoardSize(_gameSetting)];
		if (!_isDummyBoard) {
			this.setUpBoardGraphically();
		} else {
			for (int i = 0; i < Board.findBoardSize(_gameSetting); i++) {
				for (int j = 0; j < Board.findBoardSize(_gameSetting); j++) {
					_board[i][j] = new Square();
				}
			}
			// Sets up data structure for which little boards are won in the
			// ultimate game.
			if (_gameSetting == GameSetting.ULTIMATE) {
				_boardsWon = new Square[3][3];
				for(int i=0; i<3; i++) {
					for(int j=0; j<3; j++) {
						_boardsWon[j][i] = new Square();
					}
				}
			}
		}

	}

	/**
	 * This method sets up the squares and lines of the board to be shown to the user.
	 */
	private void setUpBoardGraphically() {
		_boardPane = new Pane();
		this.setUpSquares();

		if (_gameSetting == GameSetting.ULTIMATE) {
			_boardsWon = new Square[3][3];
			for(int i=0; i<3; i++) {
				for(int j=0; j<3; j++) {
					_boardsWon[j][i] = new Square();
				}
			}
			this.setUpDividingLines();
		}
	}

	/**
	 * This method graphically sets up the squares of the board by iterating through them logically
	 * and applying graphical transformations to each one.
	 */
	private void setUpSquares() {
		for (int i = 0; i < Board.findBoardSize(_gameSetting); i++) {
			for (int j = 0; j < Board.findBoardSize(_gameSetting); j++) {
				_board[i][j] = new Square(Constants.GAME_SIDE_LENGTH / Board.findBoardSize(_gameSetting));

				_board[i][j].getNode()
						.setTranslateX(i * Constants.GAME_SIDE_LENGTH / Board.findBoardSize(_gameSetting));
				_board[i][j].getNode()
						.setTranslateY(j * Constants.GAME_SIDE_LENGTH / Board.findBoardSize(_gameSetting));

				/*
				 * The following line styles the background of a square, depending on it's position.
				 * This makes a pretty pattern.
				 */
				_board[i][j]
						.setColor(
								(int) (255 * ((double) (i + j * Board.findBoardSize(_gameSetting))
										/ (Board.findBoardSize(_gameSetting) * Board.findBoardSize(_gameSetting)))),
								0, 0);
				_boardPane.getChildren().add(_board[i][j].getNode());
			}
		}
	}

	/**
	 * This method sets up visual dividing lines for the Ultimate game.
	 */
	private void setUpDividingLines() {
		Line line1;
		Line line2;

		/*
		 * Since lines are never accessed after creation, they can be added to the scene graph and
		 * then dereferenced by the next iteration of the loop.
		 */
		for (int i = 1; i < 3; i++) {
			// Instantiates and positions two lines (one vertical, one
			// horizontal)
			line1 = new Line(i * (Constants.GAME_SIDE_LENGTH / 3.0), 0, i * (Constants.GAME_SIDE_LENGTH / 3.0),
					Constants.GAME_SIDE_LENGTH);
			line2 = new Line(0, i * (Constants.GAME_SIDE_LENGTH / 3.0), Constants.GAME_SIDE_LENGTH,
					i * (Constants.GAME_SIDE_LENGTH / 3.0));

			// Sets them both to be white.
			line1.setStroke(Color.WHITE);
			line2.setStroke(Color.WHITE);

			// Adds them to the scene graph
			_boardPane.getChildren().addAll(line1, line2);
		}
	}

	/**
	 * This method returns an arraylist of the board's legal moves.
	 */
	public ArrayList<int[]> getLegalMoves() {
		ArrayList<int[]> legalMoves = new ArrayList<int[]>();

		/*
		 * This if statement checks a number of things:
		 * 
		 * 1) If game is ultimate – for the basic game, every empty space is a legal move and so
		 * covered by the else statement
		 * 
		 * 2) If there is a previous move i.e. this is not the first move of the game – for the
		 * first move of the game, every space is legal (and so will be covered by the else
		 * statement).
		 * 
		 * 3) The board you will be "sent to" is not already won – if it is won, every empty move on
		 * the board is legal and so covered by the else statement.
		 */
		if (_gameSetting == GameSetting.ULTIMATE && _previousMove != null
				&& _boardsWon[_previousMove[0] % 3][_previousMove[1] % 3].getPiece() == null) {
			/*
			 * x and y variables contain the top left corner coordinates for the small board
			 * corresponding to the last move's position inside its small board (enacts the game
			 * mechanic of "sending" the next player to whichever game corresponds on the big board
			 * to the square you played into on the small board)
			 */

			int y = 3 * ((_previousMove[1]) % 3);
			int x = 3 * (_previousMove[0] % 3);
			// Uses y+3 and x+3 because the small board is is of length
			// three on both sides.
			for (int i = y; i < y + 3; i++) {
				for (int j = x; j < x + 3; j++) {
					if (!_board[j][i].isFilled()) {
						legalMoves.add(new int[] { j, i });
					}
				}
			}
		} else {
			// Makes every empty space a legal move
			for (int i = 0; i < Board.findBoardSize(_gameSetting); i++) {
				for (int j = 0; j < Board.findBoardSize(_gameSetting); j++) {
					if (!_board[j][i].isFilled()) {
						legalMoves.add(new int[] { j, i });
					}
				}
			}
		}
		// Returns the populated arraylist of legal moves.
		return legalMoves;
	}
	
	/**
	 * This method checks if a game is won. It is static to allow the ComputerPlayer to check a win
	 * for its dummy board playouts. This method returns the game state encoded as two booleans in
	 * an array. The first indicates whether the game has been won and the second as to whether the
	 * game was drawn or not.
	 */
	public static boolean[] checkWin(Board board, Piece piece, GameSetting gameSetting) {
		// If playing normal TicTacToe, can just check for three-in-a-row (via the
		// Board.checkThreeByThreeWin method).
		if (gameSetting == GameSetting.BASIC
				&& Board.checkThreeByThreeWin(0, 0, board.getBoardArray()) == GameState.WIN) {
			return new boolean[] { true, false };

			// For a basic game draw.
		} else if (gameSetting == GameSetting.BASIC
				&& Board.checkThreeByThreeWin(0, 0, board.getBoardArray()) == GameState.DRAW) {
			return new boolean[] { true, true };
		} else {
			/*
			 * For the ultimate game, can just reuse the Board.checkThreeByThreeWin method but
			 * applied to the boards won array (since an Ultimate game win is just a three-in-a-row
			 * of boards).
			 */
			if (gameSetting == GameSetting.ULTIMATE) {
				Board.checkFinishedBoards(board, piece);
				if (Board.checkThreeByThreeWin(0, 0, board.getBoardsWon()) == GameState.WIN) {
					return new boolean[] { true, false };

					// For an Ultimate draw.
				} else if (Board.checkThreeByThreeWin(0, 0, board.getBoardsWon()) == GameState.DRAW) {
					return new boolean[] { true, true };
				}
			}
			// If no win, just return false
			return new boolean[] { false, false };
		}
	}
	
	/**
	 * This method checks for a win in a 3x3 game. It is used both to validated small boards in the
	 * Ultimate game and to check for game overs in the Basic game. The parameters denote the top
	 * left hand corner of the 3x3 box to check. For the Basic game, this will be 0,0 to encompass
	 * the whole board.
	 */
	public static GameState checkThreeByThreeWin(int startX, int startY, Square[][] boardArray) {
		int isFilled = 0;
		
		/*
		 * These loops check if the piece selected is equal to the one before it in the various vertical, horizontal
		 * and diagonal lines which can lead to a win. If they are not equal, the piece is set to NEITHER.
		 */
		for (int i = 0; i < 3; i++) {
			Piece vertical = boardArray[startX + i][startY].getPiece();
			Piece horizontal = boardArray[startX][startY + i].getPiece();
			Piece diagonal1 = boardArray[startX][startY].getPiece();
			Piece diagonal2 = boardArray[startX + 2][startY].getPiece();

			for (int j = 0; j < 3; j++) {
				// Checks each of the three vertical lines.
				if (vertical != boardArray[startX + i][startY + j].getPiece()) {
					vertical = Piece.NEITHER;
				}
				
				// Checks each of the three horizontal lines.
				if (horizontal != boardArray[startX + j][startY + i].getPiece()) {
					horizontal = Piece.NEITHER;
				}
				
				// Checks top left to bottom right diagonal.
				if (diagonal1 != boardArray[startX + j][startY + j].getPiece()) {
					diagonal1 = Piece.NEITHER;
				}
				
				// Checks top right to bottom left diagonal.
				if (diagonal2 != boardArray[startX + 2 - j][startY + j].getPiece()) {;
					diagonal2 = Piece.NEITHER;
				}
				
				// Increments if board space is filled.
				if (boardArray[startX + i][startY + j].isFilled()) {
					isFilled++;
				}
			}
			// The nulls are added to ensure a three-in-a-row of empty squares will not trigger a game over!
			if ((vertical != Piece.NEITHER && vertical != null) || (horizontal != Piece.NEITHER && horizontal != null)
					|| (diagonal1 != Piece.NEITHER && diagonal1 != null)
					|| (diagonal2 != Piece.NEITHER && diagonal2 != null)) {
				return GameState.WIN;
			}
		}
		// Calls a draw if all filled and no win triggered.
		if (isFilled == 9) {
			return GameState.DRAW;
		}
		return GameState.NOWIN;
	}
	
	/**
	 * This method checks whether any boards have been finished in the Ultimate game.
	 */
	public static void checkFinishedBoards(Board board, Piece piece) {
		for (int i = 0; i < 9; i += 3) {
			for (int j = 0; j < 9; j += 3) {
				// Only adds a finished board if the board is both won and also not yet present in
				// the array.
				if (Board.checkThreeByThreeWin(j, i, board.getBoardArray()) == GameState.WIN
						&& board.getBoardsWon()[j / 3][i / 3].getPiece() == null) {
					board.addFinishedBoard(j / 3, i / 3, piece, false, board.isDummy() ? true : false, board);
				} else if (Board.checkThreeByThreeWin(j, i, board.getBoardArray()) == GameState.DRAW
						&& board.getBoardsWon()[j / 3][i / 3].getPiece() == null) {
					board.addFinishedBoard(j / 3, i / 3, piece, true, board.isDummy() ? true : false, board);
				}
			}
		}
	}
	
	/**
	 * This method visually and logically adds a finished board in Ultimate TicTacToe. The boolean dummy flag
	 * indicates whether the board is visible and so whether the visual aspects of a finished board should be applied.
	 */
	public void addFinishedBoard(int x, int y, Piece piece, boolean draw, boolean dummy, Board board) {
		// Only continutes if board not already added to array.
		if (board.getBoardsWon()[x][y].getPiece() == null) {
			/*
			 * This loop iterates through all the squares inside the won board and logically and graphically labels
			 * them as finished.
			 */
			for (int i = x * 3; i < x * 3 + 3; i++) {
				for (int j = y * 3; j < y * 3 + 3; j++) {
					board.getBoardArray()[i][j].setParentBoardWon();
					if (!dummy) {
						board.getBoardArray()[i][j].getNode().setStyle("-fx-opacity:0.4");
					}
				}
			}
			
			// If not a draw, adds a label indicating the winner of the board. The label is held inside a StackPane.
			if (!draw) {
				board.getBoardsWon()[x][y].setPiece(piece);
				if (!dummy) {
					// This line converts the Piece into the appropriate Unicode character.
					Label pieceLabel = new Label(piece == Piece.O ? "◯" : "╳");
					pieceLabel.getStyleClass().add("piece");
					/*
					 * Because of annoying Unicode, there is not pair X and O of equal weight and size. This means that
					 * there are various lines of code which apply a different transformation depending on which piece is
					 * being added. The line below is one.
					 */
					pieceLabel.setStyle(
							"-fx-font-size:" + Constants.GAME_SIDE_LENGTH / (piece == Piece.O ? 2.5 : 3.0) + "px;");
					
					StackPane pieceLabelPane = new StackPane();
					pieceLabelPane.setPrefSize(Constants.GAME_SIDE_LENGTH / 3.0, Constants.GAME_SIDE_LENGTH / 3.0);
					pieceLabelPane.setTranslateX(x * Constants.GAME_SIDE_LENGTH / 3.0);
					// Thanks Unicode (see previous comment).
					pieceLabelPane.setTranslateY(y * Constants.GAME_SIDE_LENGTH / 3.0 + (piece == Piece.O ? -40 : -16));
					pieceLabelPane.setAlignment(Pos.CENTER);
					pieceLabelPane.getChildren().add(pieceLabel);
					board.getPane().getChildren().add(pieceLabelPane);
				}
			} else {
				// NEITHER piece is required to differentiate between unfinished and drawn boards.
				board.getBoardsWon()[x][y].setPiece(Piece.NEITHER);
			}
		}
	}
	
	/**
	 * This method sets the board's previous move. Used to find legal moves in the Ultimate game.
	 */
	public void setPreviousMove(int[] move) {
		_previousMove = move;
	}
	
	/**
	 * This method gets the previous move. Used to find legal moves in the Ultimate game.
	 */
	public int[] getPreviousMove() {
		return _previousMove;
	}
	
	/**
	 * This method gets the board array – an array of Squares containing Pieces.
	 */
	public Square[][] getBoardArray() {
		return _board;
	}
	
	/**
	 * This method reads the flag to see if this board is shown to the user or is just for algorithmic purposes.
	 */
	public boolean isDummy() {
		return _isDummyBoard;
	}
	
	/**
	 * This method returns the top level board pane.
	 */
	public Pane getPane() {
		return _boardPane;
	}
	
	/**
	 * This method gets the array of small boards won for the Ultimate game.
	 */
	public Square[][] getBoardsWon() {
		return _boardsWon;
	}
	
	/**
	 * This method is used for the copy constructor to populate the boardsWon array from a preexisting boardsWon array.
	 */
	public void setBoardsWon(Square[][] boardsWon) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				_boardsWon[j][i].copySquare(boardsWon[j][i]);
			}
		}
	}
	
	/**
	 * This method checks if any moves have been played.
	 */
	public boolean getIsFirstMove() {
		return _isFirstMove;
	}
	
	/**
	 * This method sets whether any moves have been played.
	 */
	public void setIsFirstMove(boolean isFirstMove) {
		_isFirstMove = isFirstMove;
	}
	
	/**
	 * This static method converts a GameSetting to a board size. This method exists because both the TicTacToe class
	 * and the Board class need to know the boardSize. Since they already both know the gameSetting, there is no value
	 * in having a _boardSize variable in each class. Furthermore, this is more extensible if more board sizes were
	 * to be added.
	 */
	public static int findBoardSize(GameSetting gameSetting) {
		if (gameSetting == GameSetting.BASIC) {
			return 3;
		} else if (gameSetting == GameSetting.ULTIMATE) {
			return 9;
		} else {
			return -1;
		}
	}
}
