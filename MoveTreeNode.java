package TicTacToe;

import java.util.ArrayList;

import TicTacToe.Board.GameState;
import TicTacToe.Square.Piece;

/**
 * This class is for a node in the game tree containing a potential move.
 */
public class MoveTreeNode {
	private int[] _data;
	private MoveTreeNode _parent;
	private ArrayList<MoveTreeNode> _children;
	private boolean _isGameOver;
	private int _playouts;
	private int _wins;
	private Piece _piece;
	private boolean _isDesirable;
	
	/**
	 * This constructor sets initial values for the various variables of the node.
	 */
	public MoveTreeNode(int[] move, MoveTreeNode parent, Piece piece) {
		_isGameOver = false;
		_isDesirable = false;
		// Holds the data for the move as a 2-length array of integers for x and y.
		_data = move;
		_parent = parent;
		_children = new ArrayList<MoveTreeNode>();
		_piece = piece;
		_wins = 0;
		_playouts = 0;
	}
	
	/**
	 * This method returns the move represented by this node.
	 */
	public int[] getMove() {
		return _data;
	}
	
	/**
	 * This method gets the node's parent in the tree.
	 */
	public MoveTreeNode getParent() {
		return _parent;
	}
	
	/**
	 * This method returns the piece which the node's move corresponds to.
	 */
	public Piece getPiece() {
		return _piece;
	}
	
	/**
	 * This method returns whether the node is terminal – i.e. the move causes a game over.
	 */
	public boolean isGameOver() {
		return _isGameOver;
	}
	
	/**
	 * This method sets the node as a terminal node.
	 */
	public void setAsGameOver() {
		_isGameOver = true;
		this.setAsDesirable(1);
	}
	
	/**
	 * This method adds a child to this node in the tree.
	 */
	public MoveTreeNode addChild(int[] move) {
		MoveTreeNode child = new MoveTreeNode(move, this, TicTacToe.flipPiece(_piece));
		_children.add(child);
		return child;
	}
	
	/**
	 * This method gets the arraylist of children 
	 */
	public ArrayList<MoveTreeNode> getChildren() {
		return _children;
	}
	
	/**
	 * This method returns the UCB1 algorithmic statistic for the node's move. This is used to determine how the
	 * move tree is traversed by the program.
	 */
	public double getUCBStat() {
		return ((float) _wins / _playouts
				+ Math.sqrt(5 * Math.log((float) this.getParent().getNumPlayouts()) / (float) _playouts));
	}
	
	/**
	 * This method returns the statistic for number of playouts for this node and its children.
	 */
	public int getNumPlayouts() {
		return _playouts;
	}
	
	/**
	 * This method returns the statistic for number of wins initiated from this node.
	 */
	public int getWins() {
		return _wins;
	}
	
	/**
	 * Sets node statistics as a win for a small board.
	 */
	public void setAsSmallBoardWin() {
		this.setAsDesirable(0.5);
		 this.getParent().setAsDesirable(-1);
	}
	
	/**
	 * Sets wins ridiculously high. This incentivises the algorithm to either choose or avoid this node.
	 */
	public void setAsDesirable(double i) {
		if(!_isDesirable) {
			_wins = _wins + (int)(i * 1000000);
			_isDesirable = true;
		}
		_playouts = 1;
	}
	
	/**
	 * This method updates the statistics depending on whether the playout was won or lost (indicated by the gameState
	 * parameter).
	 */
	public void updateStat(GameState gameState) {
		if (gameState == GameState.WIN) {
			_wins++;
		} else if (gameState == GameState.LOSE) {
			_wins--;
		}
		_playouts++;
	}
}
