package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import inputs.KeyboardInputs;

public class GamePanel extends JPanel {
	
	File background;
	Image backgroundImage;
	
	KeyboardInputs keyInput;
	
	// Cursor boundaries
	int cursorStart = 150;
	int cursorStop = 1050;
	
	int cursorY = 150; // Y value of cursor
	
	public GamePanel() {
		addKeyListener(new KeyboardInputs(this));
		background = new File("C:\\Users\\jackm\\git\\COEN160\\FishingGame\\resources\\lake_background.jpg");
		try {
			backgroundImage = ImageIO.read(background);
		} catch (IOException e) {
			System.out.println("Failed to load image.");
			e.printStackTrace();
		}
		
	}
	
	public void moveCursor() {
		for (int i = cursorStart; i < cursorStop; ++i) {
			this.cursorY = i;
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			repaint();
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics g2d = (Graphics2D)g;
		
		// Draw background
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		
		// Draw progress bar
		g2d.setColor(new Color(255, 0, 0));
	    g2d.fillRect(150, 30, 900, 30);
	    g2d.setColor(new Color(255, 255, 0));
	    g2d.fillRect(450, 30, 300, 30);
	    g2d.setColor(new Color(0, 255, 0));
	    g2d.fillRect(575, 30, 50, 30);
	    
	    // Draw cursor
	    g2d.setColor(new Color(0, 0, 0));
	    g2d.fillRect(cursorY, 25, 20, 40);
	}
}
