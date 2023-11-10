package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import inputs.KeyboardInputs;

public class GamePanel extends JPanel {
	
	private GameStats gameStats;
	
	private File background;
	private File fishingRod;
	private Image backgroundImage;
	private BufferedImage fishingRodImage;
	
	// Timer objects for cursor movement
	private Timer cursorTimer;
	private Timer opacityTimer;
	private Timer gameStateTimer;
	private Timer rodRotateTimer;
	private TimerTask cursorTask;
	private TimerTask opacityTask;
	private TimerTask gameStateTask;
	private TimerTask rodRotateTask;
	
	private boolean opacityTimerStarted = false;
	private boolean gameStateTimerStarted = false;
	private boolean rodRotateTimerStarted = false;
	
	private int opacity = 255;	// opacity for balanceDifference
	private double rodRotation = 0.0;	// degrees to rotate fishing rod by
	private boolean moveRodUp = true;
	
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
	
	public GamePanel(GameStats gameStats) {
		this.gameStats = gameStats;
		this.setLayout(null);
		addKeyListener(new KeyboardInputs(this, gameStats));
		wagerField.setToolTipText("Wager");
		background = new File("resources\\lake_background.jpg");
		fishingRod = new File("resources\\fishing_rod.png");
		try {
			backgroundImage = ImageIO.read(background);
			fishingRodImage = ImageIO.read(fishingRod);
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
		if (!cursorLock && gameStats.isValidWager() && GameState.state == GameState.PLAYING) {
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
			cursorTimer = new Timer();
			cursorTimer.schedule(cursorTask, 0, 1);
		}
	}
	
	// Stops cursor from moving
	public void stopCursor() {
		if (cursorTimer != null && GameState.state == GameState.PLAYING) {
			cursorLock = true;
			updateCursorColor();
			cursorTimer.cancel();
			cursorTimer.purge();
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
	
	// Show how much balance has changed
	public void displayBalanceDifference() {
		if (!opacityTimerStarted) {
			opacityTimerStarted = true;
			opacityTask = new TimerTask() {
				// Gradually decrease opacity of balanceDifference
				public void run() {
					if (opacity > 0) opacity--;
				}
			};
			// Schedule task
			opacityTimer = new Timer();
			opacityTimer.schedule(opacityTask, 2000, 2);
		}
	}
	
	// Show how much balance has changed
		public void rotateFishingRod() {
			if (!rodRotateTimerStarted) {
				rodRotateTimerStarted = true;
				rodRotateTask = new TimerTask() {
					// Gradually decrease opacity of balanceDifference
					public void run() {
						// If game idle, bounce rod
						if (GameState.state == GameState.PLAYING) {
							if (moveRodUp) rodRotation += 0.025;	// Move up
							else rodRotation -= 0.025;	// Move left
								
							// Switch direction
							if (rodRotation >= 3) moveRodUp = false;
							if (rodRotation <= -3) moveRodUp = true;
						}
						
						if (GameState.state == GameState.CASTING) {
							if (moveRodUp) rodRotation += 0.2;	// Move up
							else rodRotation -= 0.2;	// Move left
								
							// Switch direction
							if (rodRotation >= 4) moveRodUp = false;
							if (rodRotation <= -3) moveRodUp = true;
						
						}
					}
				};
				// Schedule task
				rodRotateTimer = new Timer();
				rodRotateTimer.schedule(rodRotateTask, 0, 10);
			}
		}
	
	// Return game state to 'playing'
	public void updateGameState() {
		if (!gameStateTimerStarted) {
			gameStateTimerStarted = true;
			gameStateTask = new TimerTask() {
				// Return state to playing after animations complete
				public void run() {
					opacity = 255;
					gameStateTimerStarted = false;
					opacityTimerStarted = false;
					gameStateTimer.cancel();
					gameStateTimer.purge();
					opacityTimer.cancel();
					opacityTimer.purge();
					GameState.state = GameState.PLAYING;	// return game state to playing
				}
			};
			// Schedule task
			gameStateTimer = new Timer();
			gameStateTimer.schedule(gameStateTask, 4000);
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

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
	    g2d.setFont(new Font("default", Font.BOLD, 50));
	    g2d.drawString("$" + Integer.toString(gameStats.getBalance()), 1070, 60);
	    
	    // Draw reminder
    	g2d.setColor(new Color(255, 180, 0));
    	g2d.fillRect(15, 21, 100, 25);
    	g2d.setColor(new Color(0, 0, 0));
    	g2d.fillRect(20, 24, 90, 18);
    	g2d.setColor(new Color(255, 0, 0));
    	g2d.setFont(new Font("default", Font.BOLD, 8));
    	g2d.drawString("Only enter numbers!", 26, 35);
    	
    	// Rotate fishing rod
    	AffineTransform old = g2d.getTransform();
		g2d.rotate(Math.toRadians(rodRotation), 600 + fishingRodImage.getWidth(), 300 + fishingRodImage.getHeight());
		g2d.drawImage(fishingRodImage, 600, 300, 500, 300, this);
		g2d.setTransform(old);
		
		rotateFishingRod();
    	
	    // If player casts, do animations
	    if (GameState.state == GameState.CASTING) {
	    	// TODO Animate fishing rod
			// TODO Animate fish -> use gameStats.getCurrentItem()
			
	    	g2d.setColor(new Color(250, 190, 0, opacity));
		    g2d.setFont(new Font("default", Font.BOLD, 30));
		    g2d.drawString("$" + Integer.toString(gameStats.getBalanceDifference()), 1070, 100);
		    
	    	displayBalanceDifference();
	    	
	    	updateGameState();
	    }	
	}
}
