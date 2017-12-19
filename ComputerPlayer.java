package TicTacToe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import TicTacToe.Board.GameState;
import TicTacToe.Square.Piece;
import TicTacToe.TicTacToe.GameSetting;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * This class contains the algorithm for the computer to play the game. It also has some other
 * methods that players should have (e.g. returning a name). Many of these methods pertain to the
 * algorithm (MCTS) used to choose game moves. Since it is quite confusing (took me a long time to
 * understand), I will not attempt to fully explain it but instead refer the reader to this
 * explanatory page in case of confusion:
 * 
 * https://en.wikipedia.org/wiki/Monte_Carlo_tree_search
 */
public class ComputerPlayer extends Player {
	private Piece _piece;
	private Board _dummyBoard;
	private MoveTree _moveTree;
	private GameSetting _gameSetting;
	private Piece _currentSimulationPiece;

	/**
	 * This constructor is called if this player is the first to be instantiated. It uses the
	 * superclass constructor to select a random piece.
	 */
	public ComputerPlayer(GameSetting gameSetting) {
		super();
		_piece = super.getPiece();
		this.setGameSetting(gameSetting);
	}

	/**
	 * This constructor is called if an instance of this is the second player to be instantiated. It
	 * takes in the opponent's piece and then chooses the opposite of it.
	 */
	public ComputerPlayer(Piece piece, GameSetting gameSetting) {
		super(piece);
		_piece = super.getPiece();
		this.setGameSetting(gameSetting);
	}

	/**
	 * This method gets the real game's setting and lets the class know about it.
	 */
	private void setGameSetting(GameSetting gameSetting) {
		_gameSetting = gameSetting;
	}

	/**
	 * This is the method that is called to play a move. It is effectively a wrapper around the
	 * determineMove() method but animates it for a short amount of time. This solved a bug where
	 * the previous move would not be visually displayed because the algorithm's game loop suspended
	 * other processes.
	 */
	public void animateMove(TicTacToe game) {
		KeyFrame kf = new KeyFrame(Duration.millis(100), e -> {
			this.determineMove(game);
		});
		Timeline timeline = new Timeline(kf);
		timeline.setCycleCount(1);
		timeline.play();
	}

	/**
	 * This method runs the improveGameTree() method repeatedly until the time allocated (currently
	 * 800 milliseconds) runs out.
	 */
	private void determineMove(TicTacToe game) {
		int timeBudget = 800000000;
		long startTime = System.nanoTime();
		boolean timeRanOut = false;
		// This if/else statement attempts to prune the game tree for the previous move (made by the
		// opponent).
		if (_moveTree == null) {
			_moveTree = new MoveTree(TicTacToe.flipPiece(_piece));
		} else {
			if (_moveTree.getRoot().getChildren().size() != 0) {
				boolean contained = false;
				// Attempts to find the previous move in the top level of the game tree.
				MoveTreeNode node = _moveTree.getRoot().getChildren().get(0);
				for (MoveTreeNode n : _moveTree.getRoot().getChildren()) {
					if (n.getMove()[0] == game.getBoard().getPreviousMove()[0]
							&& n.getMove()[1] == game.getBoard().getPreviousMove()[1]) {
						node = n;
						contained = true;
					}
				}

				// If found, prunes the tree by setting that node as the root.
				if (contained) {
					_moveTree.setRoot(node);
				} else {
					_moveTree = new MoveTree(TicTacToe.flipPiece(_piece));
				}
			} else {
				_moveTree = new MoveTree(TicTacToe.flipPiece(_piece));
			}

		}
		// Improves the tree until the time runs out.
		while (!timeRanOut) {
			this.improveGameTree(game);
			if (System.nanoTime() - startTime >= timeBudget) {
				timeRanOut = true;
			}
		}
		// After running out of time, chooses the best move from the tree.
		this.chooseBestChild(game);
	}

	/**
	 * This method contains the methods which make up the algorithm. It instantiates a dummy board
	 * to be populated in playouts.
	 */
	private boolean improveGameTree(TicTacToe game) {
		MoveTreeNode node = _moveTree.getRoot();

		if (game.getBoard().getIsFirstMove()) {
			_dummyBoard = new Board(_gameSetting, true);
		} else {
			_dummyBoard = new Board(_gameSetting, true, game.getBoard());
		}
		// These methods are explained in their respective comments.
		node = this.traverseTree(node);
		this.playout(this.expand(node));
		return false;
	}

	/**
	 * This method travels down the tree until it finds a viable node to expand and play out. It
	 * then places that move on the dummy board and returns the tree node.
	 */
	private MoveTreeNode traverseTree(MoveTreeNode node) {
		while (_dummyBoard.getLegalMoves().size() == node.getChildren().size() && node.getChildren().size() != 0) {
			// Chooses the best child via the UCB1 algorithm.
			node = this.selectUCB(node.getChildren());
			this.playDummyBoard(node.getMove(), node.getPiece());
		}
		return node;
	}

	/**
	 * This method expands the tree by one node and then returns that node.
	 */
	private MoveTreeNode expand(MoveTreeNode node) {
		int[] nextMove = new int[2];
		boolean[] win = Board.checkWin(_dummyBoard, node.getPiece(), _gameSetting);
		// Marks game over states appropriately.
		if (win[0]) {
			node.setAsGameOver();
		} else {
			boolean included = false;
			// This loop finds a move in the legal moves array that is not already present in the
			// tree.
			for (int[] legalMove : _dummyBoard.getLegalMoves()) {
				included = false;
				for (MoveTreeNode n : node.getChildren()) {
					if (Arrays.equals(n.getMove(), legalMove)) {
						included = true;
					}
				}
				if (!included) {
					nextMove = legalMove;
					break;
				}
			}
			// Adds the child node.
			node = node.addChild(nextMove);
			// Places the move of that node.
			this.playDummyBoard(node.getMove(), node.getPiece());
		}
		/*
		 * This if statement adds some weighting to the Ultimate game. Since finishing a small board
		 * doesn't end the game and since the game tree can only go so far down in 800ms, the AI is
		 * not strongly incentivised to finish boards. This helps with that.
		 */
		if (_gameSetting == GameSetting.ULTIMATE && Board.checkThreeByThreeWin(3 * (node.getMove()[0] / 3),
				3 * (node.getMove()[1] / 3), _dummyBoard.getBoardArray()) == GameState.WIN) {
			node.setAsSmallBoardWin();
			// Disincentivises moving the user to an already won board and therefore allowing them
			// to move anywhere.
		} else if (_gameSetting == GameSetting.ULTIMATE
				&& _dummyBoard.getBoardsWon()[node.getMove()[0] % 3][node.getMove()[1] % 3].getPiece() != null) {
			node.setAsDesirable(-0.2);
		}
		return node;
	}

	/**
	 * This method performs a random playout until game over. It then calls backup() to propagate
	 * statistics to the game tree.
	 */
	private void playout(MoveTreeNode node) {
		boolean[] win = Board.checkWin(_dummyBoard, node.getPiece(), _gameSetting);
		_currentSimulationPiece = TicTacToe.flipPiece(node.getPiece());
		// Plays moves while the game is not won.
		while (!win[0]) {	
			// Chooses a random index for a move.
			int rn = (new Random()).nextInt(_dummyBoard.getLegalMoves().size());
			// Plays the move
			this.playDummyBoard(_dummyBoard.getLegalMoves().get(rn), _currentSimulationPiece);
			// Checks for win.
			win = Board.checkWin(_dummyBoard, _currentSimulationPiece, _gameSetting);
			this.flipPiece();
		}
		// Backpropagates if the game is won.
		if (win[1]) {
			this.backup(GameState.DRAW, node);
		} else {
			this.backup(_currentSimulationPiece == node.getPiece() ? GameState.LOSE : GameState.WIN, node);
		}
	}
	
	/**
	 * This method backs up the tree and propagates the game results into the statistics of the nodes.
	 */
	private void backup(GameState gameState, MoveTreeNode node) {
		int i = 0;
		// This do while loop keeps going until the node with no parent (i.e. the root) is selected.
		do {
			if (gameState == GameState.DRAW) {
				node.updateStat(gameState);
			
			// This part of the if/else statement updates alternating wins and losses up the tree.
			} else if (i % 2 == 0) {
				node.updateStat(gameState);
			} else {
				node.updateStat(gameState == GameState.WIN ? GameState.LOSE : GameState.WIN);
			}
			node = node.getParent();
			i++;
		} while (node != null);
	}
	
	/**
	 * This method chooses the most valuable child from the top level children in the game tree
	 */
	private void chooseBestChild(TicTacToe game) {
		MoveTreeNode bestMove = _moveTree.getRoot().getChildren()
				.get((new Random()).nextInt(_moveTree.getRoot().getChildren().size()));
		// This loops through all the top level nodes and finds the best, according to win rate.
		for (MoveTreeNode n : _moveTree.getRoot().getChildren()) {
			bestMove = (double) n.getWins() / n.getNumPlayouts() > (double) bestMove.getWins()
					/ bestMove.getNumPlayouts() ? n : bestMove;
		}
		// Plays the best move
		this.move(game, Board.findBoardSize(_gameSetting), bestMove.getMove()[0], bestMove.getMove()[1]);
		// Trims the tree, removing the other moves and their child branches.
		_moveTree.setRoot(bestMove);
		game.switchPlayer();

	}
	
	/**
	 * This method selects the best child of a tree node based on the UCB1 algorithm
	 */
	private MoveTreeNode selectUCB(ArrayList<MoveTreeNode> nodes) {
		MoveTreeNode bestScored = nodes.get(0);
		for (MoveTreeNode n : nodes) {
			bestScored = n.getUCBStat() > bestScored.getUCBStat() ? n : bestScored;
		}
		return bestScored;
	}

	/**
	 * This method flips the current simulation piece.
	 */
	private void flipPiece() {
		_currentSimulationPiece = TicTacToe.flipPiece(_currentSimulationPiece);
	}
	
	/**
	 * This method gets the player's name, which is always "Computer" since the player is always a computer.
	 */
	public String getName() {
		return "Computer";
	}
	
	/**
	 * This method always returns true to indicated that ComputerPlayer is a computer.
	 */
	public boolean isComputer() {
		return true;
	}
	
	/**
	 * This method bundles the required actions to place a piece on the dummy board.
	 */
	private void playDummyBoard(int[] move, Piece piece) {
		_dummyBoard.getBoardArray()[move[0]][move[1]].setPiece(piece);
		if (_gameSetting == GameSetting.ULTIMATE) {
			Board.checkFinishedBoards(_dummyBoard, piece);
		}
		_dummyBoard.setPreviousMove(move);
	}
}
