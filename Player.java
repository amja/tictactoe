package TicTacToe;

import java.util.Random;

import TicTacToe.Square.Piece;

/**
 * This abstract class is for a generic player (used by the ComputerPlayer and HumanPlayer classes).
 * This inheritance allows them to be held in homogeneous data structures (e.g. arrays of type
 * Player). This class also factors out some common code.
 */
public abstract class Player {
	private Piece _piece;

	/**
	 * The constructor chooses a random piece for the player.
	 */
	public Player() {
		_piece = Piece.values()[(new Random()).nextInt(2)];
	}

	/**
	 * This constructor is called when the player is the second to be created. The first player
	 * passes in their piece, and this constructor chooses the opposite to it. This is so that both
	 * players don't randomly choose the same piece.
	 */
	public Player(Piece piece) {
		_piece = TicTacToe.flipPiece(piece);
	}

	/**
	 * This method places a piece on the real board.
	 */
	public void move(TicTacToe game, int boardSize, int x, int y) {
		game.getBoard().setPreviousMove(new int[] { x, y });
		game.getBoard().getBoardArray()[x][y].placePiece(_piece, boardSize, false);

		// First move flag is used so the empty board isn't highlighted – that would confuse the
		// user.
		if (game.getBoard().getIsFirstMove()) {
			game.getBoard().setIsFirstMove(false);
		}
	}

	/**
	 * This method gets the player's piece.
	 */
	public Piece getPiece() {
		return _piece;
	}

	// This method is abstract because computer players don't have names and will always return
	// "computer".
	public abstract String getName();

	// This method is abstract because the computer and the human player will implement it
	// differently.
	public abstract boolean isComputer();
}
