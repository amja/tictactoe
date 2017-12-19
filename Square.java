package TicTacToe;

import TicTacToe.Square.Piece;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * This class represents a square on the TicTacToe board. It bundles visual and logical states,
 * using a StackPane with a Label inside it and a Piece variable respectively.
 */
public class Square {
	private StackPane _square;
	private Color _color;
	private boolean _highlighted;
	private Piece _piece;
	private boolean _parentBoardWon;

	/**
	 * This enum represents possible TicTacToe pieces. The third option, NEITHER, exists because
	 * this enum is also used to encode which boards have been won in the Ultimate version of the
	 * game. Since boards can be drawn (neither won nor lost), there must be a state for a completed
	 * board won by neither player. This may seem a bit inelegant but the alternatives would be to
	 * either use the Square class (which would include a bunch of useless visual stuff for a purely
	 * logical data structure) or to create a new, very similar enum just for won boards. Both
	 * alternatives would have added superfluous code.
	 */
	public enum Piece {
		X, O, NEITHER
	}

	/**
	 * The constructor sets up the visual square and some flags. _parentBoardWon allows a square to
	 * know whether it is part of a board that has already been completed (in the Ultimate game).
	 */
	public Square(double length) {
		_parentBoardWon = false;
		_highlighted = false;

		_square = new StackPane();
		_square.setPrefWidth(length);
		_square.setPrefHeight(length);
		_square.setAlignment(Pos.CENTER);
	}

	/**
	 * Constructor for dummy board where no visuals required.
	 */
	public Square() {
		_parentBoardWon = false;
		_highlighted = false;
	}

	/**
	 * This method copies parameters of another square to this one. This is used by the copy
	 * constructor for the board, which is called by the AI to create a dummy board.
	 */
	public void copySquare(Square square) {
		_piece = square.getPiece();
		_parentBoardWon = square.isParentBoardWon();
	}

	/**
	 * This method places a piece (X or O) in the square, both visually and logically.
	 */
	public void placePiece(Piece piece, int boardSize, boolean isDummy) {
		if (!isDummy) {
			// Converts enum to correct unicode character.
			Label pieceLabel = new Label(piece == Piece.O ? "◯" : "╳");
			pieceLabel.getStyleClass().add("piece");
			// Because of dumb font design, the shapes need to be different font sizes to look the
			// same on the board.
			pieceLabel.setStyle("-fx-font-size:"
					+ (double) (Constants.GAME_SIDE_LENGTH) / (boardSize / (piece == Piece.O ? 1.1 : 0.9))
					+ "; -fx-font-weight:normal; -fx-font-family: Arial;");
			_square.getChildren().add(pieceLabel);
			pieceLabel.setTranslateY((piece == Piece.O ? -60 : 1) / (double)boardSize);
		} else {
			System.out.println("ERROR ERROR");
		}

		// Logically sets the piece inside the Square.
		_piece = piece;
	}

	/**
	 * This method allows logical setting of piece without graphical placement.
	 */
	public void setPiece(Piece piece) {
		_piece = piece;
	}

	/**
	 * This method returns the Piece which has been placed in this Square.
	 */
	public Piece getPiece() {
		return _piece;
	}

	/**
	 * This method returns the graphical square node.
	 */
	public StackPane getNode() {
		return _square;
	}

	/**
	 * This method sets the flag for the parent board being won.
	 */
	public void setParentBoardWon() {
		_parentBoardWon = true;
	}

	/**
	 * This method queries the parentBoardWon flag
	 */
	public boolean isParentBoardWon() {
		return _parentBoardWon;
	}

	/**
	 * This method checks if the square is filled by a piece. It is also seen as filled if the
	 * parent board is won. This is so Squares inside a finished game are not considered empty and
	 * thus legal by the getLegalMoves method.
	 */
	public boolean isFilled() {
		if (_piece != null || _parentBoardWon) {
			return true;
		}
		return false;
	}

	/**
	 * This method sets the background colour of the square.
	 */
	public void setColor(int r, int g, int b) {
		_color = Color.rgb(r, g, b);
		_square.setStyle("-fx-background-color: rgb(" + r + "," + g + "," + b + ")");
	}

	/**
	 * This method toggles the highlighting of the square by inverting its colours. The square is
	 * highlighted when a human is playing the Ultimate version of the game to indicate that the
	 * square is a legal move.
	 */
	public void toggleHighlight() {
		_highlighted = !_highlighted;
		_color = _color.invert();
		this.setColor((int) (_color.getRed() * 255), (int) (_color.getGreen() * 255), (int) (_color.getBlue() * 255));
	}
}
