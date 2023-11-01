package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;
import main.GameState;

public class KeyboardInputs implements KeyListener {

	private GamePanel gPanel;
	
	public KeyboardInputs(GamePanel gPanel) {
		this.gPanel = gPanel;
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// Change based off game state
		switch(GameState.state) {
			case PLAYING:
				// If spacebar pressed, move cursor
				if(e.getKeyCode() == KeyEvent.VK_SPACE) {
					System.out.println("Spacebar pressed");
					gPanel.moveCursor();
				}
				
				// If ESC pressed, show menu
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.out.println("Menu Opened");
					GameState.state = GameState.MENU;
				}
		
				break;
		
			case MENU:
				// If ESC pressed, show menu
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					System.out.println("Menu closed");
					GameState.state = GameState.PLAYING;
				}
				break;
				
			default:
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// If spacebar released, stop cursor
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			System.out.println("Spacebar released");
			gPanel.stopCursor();
		}
		
	}

}
