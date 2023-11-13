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
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sun.tools.javac.Main;

import inputs.KeyboardInputs;

public class GamePanel extends JPanel {
	
	private GameStats gameStats;
	
	private File background;
	private File fishingRod;
	private File caughtItem;
	private Image backgroundImage;
	private BufferedImage fishingRodImage;
	private Image caughtItemImage;
	
	// Fishing reel sound effect
	private Clip fishingReelClip;
	private AudioInputStream audioInputStream;
	private boolean reelSoundPlayed = false;
	
	// Timer objects for cursor movement
	private Timer cursorTimer;
	private Timer opacityTimer;
	private Timer gameStateTimer;
	private Timer rodRotateTimer;
	private Timer moveItemTimer;
	private TimerTask cursorTask;
	private TimerTask opacityTask;
	private TimerTask gameStateTask;
	private TimerTask rodRotateTask;
	private TimerTask moveItemTask;
	
	private boolean opacityTimerStarted = false;
	private boolean gameStateTimerStarted = false;
	private boolean rodRotateTimerStarted = false;
	private boolean moveItemTimerStarted = false;
	
	private int opacity = 255;	// opacity for balanceDifference
	private double rodRotation = 0.0;	// degrees to rotate fishing rod by
	private boolean moveRodUp = true;
	private int itemY = 400;
	
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
		// Initialize reel sound effect
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
	
	// Rotate rod up and down
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
						if (moveRodUp) rodRotation += 0.8;	// Move up
						else rodRotation -= 0.4;	// Move left
							
						// Switch direction
						if (rodRotation >= 10) moveRodUp = false;
						if (rodRotation <= -3) moveRodUp = true;
					
					}
				}
			};
			// Schedule task
			rodRotateTimer = new Timer();
			rodRotateTimer.schedule(rodRotateTask, 0, 10);
			}
	}
	
	// Move fish up
	public void moveCaughtItem() {
		if (!moveItemTimerStarted) {
			moveItemTimerStarted = true;
			moveItemTask = new TimerTask() {
				// move caught item upward
				public void run() {
					if (itemY > 200) itemY--;
				}
			};

			// Schedule task
			moveItemTimer = new Timer();
			moveItemTimer.schedule(moveItemTask, 0, 2);
		}
	}
	
	public void playReelSound() {
		if (!reelSoundPlayed) {
			reelSoundPlayed = true;
			try {
				fishingReelClip = AudioSystem.getClip();
				audioInputStream = AudioSystem.getAudioInputStream(
						new File("resources\\fishing_reel.wav").getAbsoluteFile());
				fishingReelClip.open(audioInputStream);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			fishingReelClip.start();
		}
	}
	
		
	// Return game state to 'playing'
	public void updateGameState() {
		if (!gameStateTimerStarted) {
			gameStateTimerStarted = true;
			gameStateTask = new TimerTask() {
				// Return state to playing after animations complete
				public void run() {
					fishingReelClip.close();
					gameStateTimerStarted = false;
					opacityTimerStarted = false;
					moveItemTimerStarted = false;
					gameStateTimer.cancel();
					gameStateTimer.purge();
					opacityTimer.cancel();
					opacityTimer.purge();
					moveItemTimer.cancel();
					moveItemTimer.purge();
					opacity = 255;
					itemY = 400;
					reelSoundPlayed = false;
					GameState.state = GameState.PLAYING;	// return game state to playing
				}
			};
			// Schedule task
			gameStateTimer = new Timer();
			gameStateTimer.schedule(gameStateTask, 5000);
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
	    g2d.drawString("$" + Integer.toString(gameStats.getBalance()), 1060, 60);
	    
	    // Draw reminder
    	g2d.setColor(new Color(255, 180, 0));
    	g2d.fillRect(15, 21, 100, 25);
    	g2d.setColor(new Color(0, 0, 0));
    	g2d.fillRect(20, 24, 90, 18);
    	g2d.setColor(new Color(255, 0, 0));
    	g2d.setFont(new Font("default", Font.BOLD, 8));
    	g2d.drawString("Only enter numbers!", 26, 35);
    	
    	// Draw stats text at bottom
    	g2d.setColor(new Color(250, 190, 0));
    	g2d.setFont(new Font("default", Font.BOLD, 18));
    	String item = gameStats.getCurrentItem();
    	g2d.drawString("Average Value: $" + gameStats.getAveragePrice(), 20, 600);
    	g2d.drawString("Last Caught: " + item, 230, 600);
    	g2d.drawString("Value: $" + gameStats.getCatchableItemPrice(item), 450, 600);
    	
    	// Rotate fishing rod
    	AffineTransform old = g2d.getTransform();
		g2d.rotate(Math.toRadians(rodRotation), 600 + fishingRodImage.getWidth(), 300 + fishingRodImage.getHeight());
		g2d.drawImage(fishingRodImage, 600, 300, 500, 300, this);
		g2d.setTransform(old);
		
		rotateFishingRod();
    	
	    // If player casts, do animations
	    if (GameState.state == GameState.CASTING) {
	    	
	    	g2d.setColor(new Color(250, 190, 0, opacity));
		    g2d.setFont(new Font("default", Font.BOLD, 30));
		    g2d.drawString("$" + Integer.toString(gameStats.getBalanceDifference()), 1070, 100);
		    
	    	displayBalanceDifference();
	    	
	    	// Display caught item
	    	String caughtItemName = gameStats.getCurrentItem();
	    	caughtItem = new File("resources\\" + caughtItemName.toLowerCase() + ".png");
			try {
				caughtItemImage = ImageIO.read(caughtItem);
			} catch (IOException e) {
				System.out.println("Failed to load image.");
				e.printStackTrace();
			}
			
			moveCaughtItem();
			g2d.drawImage(caughtItemImage, 500, itemY, 200, 200, this);
	    	
	    	updateGameState();
	    }	
	}
}
