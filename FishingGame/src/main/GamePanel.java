package main;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;

import inputs.KeyboardInputs;

public class GamePanel extends JPanel {
	
	private File background;
	private Image backgroundImage;
	
	private KeyboardInputs keyInput;
	
	// Timer objects for cursor movement
	private Timer timer;
	private TimerTask cursorTask;
	
	// Cursor variables
	private int cursorStart = 150;	// left boundary
	private int cursorStop = 1050;	// right boundary
	private int cursorY = 150; 		// current Y value of cursor
	private int cursorSpeed = 1;	// speed of cursor
	private boolean moveCursorRight = true; // false -> move left
	
	// Prevents holding spacebar from calling moveCursor()
	// multiple times
	private boolean cursorLock = false;
	
	JButton menuButton = new JButton("Menu");	// Menu button
	
	public GamePanel() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		addKeyListener(new KeyboardInputs(this));
		background = new File("C:\\Users\\jackm\\git\\COEN160\\FishingGame\\resources\\lake_background.jpg");
		try {
			backgroundImage = ImageIO.read(background);
		} catch (IOException e) {
			System.out.println("Failed to load image.");
			e.printStackTrace();
		}
	}
	
	// Moves cursor along progress bar
	public void moveCursor() {
		if (!cursorLock) {
			cursorLock = true;
			// TimerTask to move cursor
			cursorTask = new TimerTask() {
				// Make cursor bounce
				public void run() {
					if (moveCursorRight) cursorY += cursorSpeed;	// Move right
					else cursorY -= cursorSpeed;	// Move left
						
					// Switch direction if y == boundary
					if (cursorY >= (cursorStop-10)) moveCursorRight = false;
					if (cursorY <= cursorStart) moveCursorRight = true;
				}
			};
			// Schedule task
			timer = new Timer();
			timer.schedule(cursorTask, 0, 1);
		}
	}
	
	// Stops cursor from moving
	public void stopCursor() {
		cursorLock = false;
		timer.cancel();
		timer.purge();
		System.out.println("Stopped!");
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics g2d = (Graphics2D)g;
		
		// Draw background
		g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		
		// Change based on GameState
		switch(GameState.state) {
			case PLAYING:
				// Draw progress bar
				g2d.setColor(new Color(255, 0, 0));
			    g2d.fillRect(150, 30, 900, 30);
			    g2d.setColor(new Color(255, 255, 0));
			    g2d.fillRect(450, 30, 300, 30);
			    g2d.setColor(new Color(0, 255, 0));
			    g2d.fillRect(575, 30, 50, 30);
			    
			    // Draw cursor
			    g2d.setColor(new Color(0, 0, 0));
			    g2d.fillRect(cursorY, 25, 10, 40);
				
			    // Add Menu button
			    menuButton.setFocusable(false);
			    menuButton.addActionListener((ActionListener) new ActionListener() {
			    	@Override
			        public void actionPerformed(ActionEvent e) {
			    		if (GameState.state == GameState.PLAYING)
			    			GameState.state = GameState.MENU;
			    		else GameState.state = GameState.PLAYING;
			        }
			    });
			    this.add(menuButton);
			    
				break;
				
			case MENU:
				// Draw Menu
				g2d.setColor(new Color(120, 120, 120));
			    g2d.fillRect(150, 30, 900, 600);
				
				break;
				
			default:
		}
	}
}
