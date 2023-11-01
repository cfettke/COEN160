package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import main.GamePanel;

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
		// detect spacebar
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			System.out.println("Spacebar pressed");
			gPanel.moveCursor();
		}
		
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
