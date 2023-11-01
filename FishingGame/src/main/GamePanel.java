package main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import inputs.KeyboardInputs;

public class GamePanel extends JPanel {
	
	File background;
	Image backgroundImage;
	
	public GamePanel() {
		addKeyListener(new KeyboardInputs());
		background = new File("C:\\Users\\jackm\\git\\COEN160\\FishingGame\\resources\\lake_background.jpg");
		try {
			backgroundImage = ImageIO.read(background);
		} catch (IOException e) {
			System.out.println("Failed to load image.");
			e.printStackTrace();
		}
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
	}
}
