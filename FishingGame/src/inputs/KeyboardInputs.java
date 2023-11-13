package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;
import main.GameState;
import main.GameStats;

public class KeyboardInputs implements KeyListener {

	private GamePanel gPanel;
	private GameStats gameStats;
	
	public KeyboardInputs(GamePanel gPanel, GameStats gameStats) {
		this.gPanel = gPanel;
		this.gameStats = gameStats;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// If spacebar pressed, move cursor
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			gPanel.moveCursor();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// If spacebar released, stop cursor
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			gPanel.stopCursor();
			gPanel.playReelSound();
			gameStats.cast();
		}
	}
}
