package TicTacToe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This Application implements a virtual version of Ultimate Tic Tac Toe, as well as normal Tic Tac Toe. The rules of
 * Ultimate Tic Tac Toe are explained in this project's User Guide. It facilitates Human vs Human play and also provides an AI to play against. There are three main components of this
 * App: the TicTacToe game class, the Board class and the Player classes.
 * 
 * As usual, the game class is the top level object for game logic and instantiates most of the other classes required
 * by the game. TicTacToe firstly gets user input to determine which settings to play the game by. It's possible that
 * the settings pane logic could have been placed in the PaneOrganizer class but since it had a large number of local
 * variables that were used only to take in user input and then discarded, this would have created a lot of extra
 * overhead. It also deals with most of the game play methods that don't directly relate to the board (like switching
 * to the other player once one has played).
 * 
 * The Board class contains the bulk of the game logic because much of it relates to the board. Many of the methods were
 * previously in the TicTacToe class but were moved because they were better related to the board. For example, all the
 * methods to check wins are static and so could technically be in any class but are in the Board class because they
 * relate to boards in general. The Board class also graphically sets up much of the game (since most of the game is
 * just the board). What is not set up by the Board is set up by the Squares themselves.
 * 
 * The setup for the players is a bit weird and, in some ways, asymmetrical but I believe it to be the most logical
 * design. What I am referring to is the quite different architectures of the two player classes. However, these
 * different capabilities are due to the very different ways human and computer plays are made. It is impossible
 * to trigger a human move, since the human takes time and chooses a move by clicking. This means that the human player
 * class is handed a move and must verify it is legal before placing it. The computer player, in contrast, knows the
 * list of legal moves and so does not need to validate whether its moves are legal because any choice it makes will be
 * contained in the set of legal moves. Because of these differences, the main purpose of the HumanPlayer class is to
 * extract a move from a clicked square and then place it if it is legal. The ComputerPlayer's main purpose is to 
 * choose a move when the appropriate method is triggered.
 * 
 * Things to consider for extra credit (not limited to these though):
 * - Game records scores of players and displays them at the end of each game.
 * - Game remembers settings (e.g. names from last game and pre-populates the form for easy continual play).
 */

public class App extends Application {
	
	/**
	 * All the classic stuff. Instantiates PaneOrganizer class and sets up JavaFX scene and stage.
	 */
    @Override
    public void start(Stage stage) {
        stage.setTitle("Tic Tac Toe");
        PaneOrganizer organizer = new PaneOrganizer();
        Scene scene = new Scene(organizer.getRoot(), Constants.STAGE_WIDTH, Constants.STAGE_HEIGHT);
        
        // Links CSS stylesheet, allowing easy custom styling.
        scene.getStylesheets().add(this.getClass().getResource("stylesheet.css").toExternalForm());
        
        // The following line disables window resizing.
        stage.setResizable(false);
        stage.setScene(scene);
    	stage.show();
    }

    public static void main(String[] args) {
    	launch(args);
    }
}
