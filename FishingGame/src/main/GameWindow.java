package main;

import javax.swing.JFrame;

public class GameWindow {
	
	private JFrame frame;
	
	public GameWindow(GamePanel gPanel) {
		frame = new JFrame();
		frame.setSize(1200, 700);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(gPanel);
		frame.setVisible(true);
		
	}

}
