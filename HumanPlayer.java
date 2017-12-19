package TicTacToe;

import java.util.ArrayList;
import TicTacToe.Square.Piece;
import TicTacToe.TicTacToe.GameSetting;
import javafx.scene.layout.StackPane;

/**
 * This class deals with the human player. The main role of this class is to play pieces into the human's selected
 * spaces as long as they are legal moves.
 */
public class HumanPlayer extends Player {
	private GameSetting _gameSetting;
	private String _name;
	
	/**
	 * This constructor uses the parent class's constructor to assign a random piece to the player.
	 * It also sets the name chosen by the player.
	 */
	public HumanPlayer(GameSetting gameSetting, String name) {
		super();
		_name = name;
		_gameSetting = gameSetting;
	}
	
	/**
	 * This constructor uses the parent class's constructor to assign a piece opposite to the player already created.
	 * It also sets the name chosen by the player.
	 */
	public HumanPlayer(Piece piece, GameSetting gameSetting, String name) {
		super(piece);
		_name = name;
		_gameSetting = gameSetting;
	}
	
	/**
	 * This method checks if the human's move is legal and, if so, plays it. The clickedNode parameter is the
	 * scene graph node that the human clicked on.
	 */
	public void evaluateMove(TicTacToe game, StackPane clickedNode) {
		ArrayList<int[]> legalMoves = game.getBoard().getLegalMoves();
		Square[][] boardArray = game.getBoard().getBoardArray();
		int moveX = 0;
		int moveY = 0;
		boolean moveIsLegal = false;
		
		// Iterates through the board array to find the square that was clicked on.
		for (int i = 0; i < Board.findBoardSize(_gameSetting); i++) {
			for (int j = 0; j < Board.findBoardSize(_gameSetting); j++) {
				if (boardArray[i][j].getNode() == clickedNode) {
					moveX = i;
					moveY = j;
				}
			}
		}
		
		// Searches for move inside legal moves array to check if it is legal.
		for (int i = 0; i < legalMoves.size(); i++) {
			if (legalMoves.get(i)[0] == moveX && legalMoves.get(i)[1] == moveY) {
				moveIsLegal = true;
			}
		}
		
		// Only make move if it is a legal move (not that surprising).
		if (moveIsLegal) {
			if (!game.getBoard().getIsFirstMove() && _gameSetting == GameSetting.ULTIMATE) {
				// This iterates through the legal moves to un-highlight the board in preparation for the next move.
				for (int i = 0; i < legalMoves.size(); i++) {
					game.getBoard().getBoardArray()[legalMoves.get(i)[0]][legalMoves.get(i)[1]].toggleHighlight();
				}
			}
			// Calls the superclass method to place a piece on the board.
			this.move(game, Board.findBoardSize(_gameSetting), moveX, moveY);
			game.switchPlayer();
		}

	}
	
	/**
	 * This method gets the player's name to display to the user.
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * This method queries whether this player is a computer (it's not).
	 */
	public boolean isComputer() {
		return false;
	}
}
