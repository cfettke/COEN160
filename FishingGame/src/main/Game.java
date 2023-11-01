package main;

public class Game implements Runnable {
	
	private GameWindow gameWindow;
	private GamePanel gPanel;
	private Thread gameThread;
	private final int FPS = 60;

	public Game() {
		gPanel = new GamePanel();
		gameWindow = new GameWindow(gPanel);
		gPanel.requestFocus();
		startGameLoop();
	}
	
	private void startGameLoop() {
		gameThread = new Thread(this);
		gameThread.start();	// calls run() method
	}
	
	// Game loop
	public void run() {
		double timePerFrame = 1000000000.0 / FPS; // length of each frame
		long lastFrame = System.nanoTime();
		long now = System.nanoTime();
		
		while(true) {
			now = System.nanoTime();
			if (now - lastFrame >= timePerFrame) {
				gPanel.repaint();
				lastFrame = now;
			}
		}
	}
}
