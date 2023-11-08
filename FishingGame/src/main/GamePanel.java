package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import inputs.KeyboardInputs;

public class GamePanel extends JPanel {
	
	private Game game;
	private GameStats gameStats;
	
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
	public static String cursorColor;
	
	private JTextField wagerField = new JTextField(8);
	private JButton wagerButton = new JButton("Wager");
	
	public GamePanel(Game game, GameStats gameStats) {
		this.game = game;
		this.gameStats = gameStats;
		this.setLayout(null);
		addKeyListener(new KeyboardInputs(this, gameStats));
		wagerField.setToolTipText("Wager");
		background = new File("C:\\Users\\jackm\\git\\COEN160\\FishingGame\\resources\\lake_background.jpg");
		try {
			backgroundImage = ImageIO.read(background);
		} catch (IOException e) {
			System.out.println("Failed to load image.");
			e.printStackTrace();
		}
		
		// Add wager field
	    wagerField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
	    wagerField.setFocusable(true);
	    wagerField.setBounds(30, 50, 70, 30);
	    wagerField.setFont(new Font("Arial", Font.BOLD, 20));
	    this.add(wagerField);
	    
	    // Add wager button
	    wagerButton.setFocusable(false);
	    wagerButton.setBounds(30, 90, 70, 30);
	    wagerButton.setFont(new Font("Arial", Font.BOLD, 11));
	    wagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// Set wager value
    			gameStats.setWagerValue(wagerField.getText().trim());
    			requestFocusInWindow();
    			cursorLock = false;
    			cursorY = cursorStart;
            }
	    
	    });
	    this.add(wagerButton);
	}
	
	// Moves cursor along progress bar
	public void moveCursor() {
		if (!cursorLock && gameStats.isValidWager()) {
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
		if (timer != null) {
			cursorLock = true;
			updateCursorColor();
			timer.cancel();
			timer.purge();
		}
	}
	
	// Return whether cursor in red, yellow, or green
	public void updateCursorColor() {
		if (((cursorY >= 150) && (cursorY <= 450))
			|| ((cursorY >= 750) && (cursorY <= 1050))) {
			cursorColor = "red";
		}
		
		else if (((cursorY > 450) && (cursorY <= 575))
				|| ((cursorY >= 625) && (cursorY < 750))) {
			cursorColor = "yellow";
		}
		
		else {
			cursorColor = "green";
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics g2d = (Graphics2D)g;
		
		// Draw background
		g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		
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
		
	    // TODO draw balance
	    g2d.setColor(new Color(250, 190, 0));
	    g2d.drawString(Integer.toString(gameStats.getBalance()), 30, 900);
	    
	    // Draw reminder
    	g2d.setColor(new Color(255, 180, 0));
    	g2d.fillRect(15, 21, 100, 25);
    	g2d.setColor(new Color(0, 0, 0));
    	g2d.fillRect(20, 24, 90, 18);
    	g2d.setColor(new Color(255, 0, 0));
    	g2d.setFont(new Font("default", Font.BOLD, 8));
    	g2d.drawString("Only enter numbers!", 26, 35);
	    
	    
	    // If player casts, do animations
	    if (GameState.state == GameState.CASTING) {
	    	// TODO Animate fishing rod
			// TODO Animate fish -> use gameState.getCurrentItem()
			
			
	    	GameState.state = GameState.PLAYING;	// return game state to playing
	    }	
	}
}
