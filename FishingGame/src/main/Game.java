package main;

public class Game {
	
	private GameWindow gameWindow;
	private GamePanel gPanel;

	public Game() {
		gPanel = new GamePanel();
		gameWindow = new GameWindow(gPanel);
		gPanel.requestFocus();
	}
}
