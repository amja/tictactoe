package TicTacToe;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * This class creates the status bar at the bottom of the game. It provides methods for the game to
 * set the text.
 */
public class StatusBar {
	private Label _label;
	private HBox _statusBar;
	private boolean _visible;
	
	/**
	 * This constructor sets visual look of the status bar, via positioning, size and colour.
	 * Also instantiates a label to be populated via a later method.
	 */
	public StatusBar() {
		// Status bar starts out hidden from view until settings screen is passed.
		_visible = false;
		
		_label = new Label();
		_label.setId("status-label");
		
		_statusBar = new HBox();
		_statusBar.setTranslateY(Constants.GAME_SIDE_LENGTH);
		_statusBar.setPrefWidth(Constants.GAME_SIDE_LENGTH);
		_statusBar.setPrefHeight(Constants.STATUS_BAR_HEIGHT);
		_statusBar.setStyle("-fx-background-color:black");
		// Actually hides the bar.
		_statusBar.setOpacity(0);
		_statusBar.setAlignment(Pos.CENTER);
		_statusBar.getChildren().add(_label);
	}
	
	/**
	 * This method sets the text of the label inside the status bar.
	 */
	public void setText(String text) {
		_label.setText(text);
	}
	
	/**
	 * This method returns the current message displayed by the status bar.
	 */
	public String getText() {
		return _label.getText();
	}
	
	/**
	 * This method returns the status bar's top level node.
	 */
	public HBox getPane() {
		return _statusBar;
	}
	
	/**
	 * This method toggles the visibility to the user of the status bar. This is to hide it while
	 * the user inputs settings (it looks ugly).
	 */
	public void toggleVisibility() {
		if(_visible) {
			_visible = false;
			_statusBar.setOpacity(0);
		} else {
			_visible = true;
			_statusBar.setOpacity(1);
		}
	}
}
