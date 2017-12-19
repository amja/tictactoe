package TicTacToe;

import TicTacToe.Square.Piece;

/**
 * This class is for the game tree, which is used by the computer to determine good moves to play.
 */
public class MoveTree {
	private MoveTreeNode _root;
	
	/**
	 * When the tree is instantiated, this constructor makes a new empty node to be the root.
	 */
	public MoveTree(Piece piece) {
		this.setRoot(new MoveTreeNode(null, null, piece));
	}
	
	/**
	 * This method sets a node as the root of the tree.
	 */
	public void setRoot(MoveTreeNode node) {
		_root = node;
	}
	
	/**
	 * This method gets the tree's root.
	 */
	public MoveTreeNode getRoot() {
		return _root;
	}
}
