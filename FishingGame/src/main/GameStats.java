package main;

import java.util.HashMap;
import java.util.Random;

// Tracks game statistics
public class GameStats {
	
		private int balance = 50;
		private int balanceDifference = 0;
		private int averagePrice = 0;	// avg price of all fish caught
		private int wager = -1;
		private boolean isValidWager = false;	// if user has entered a wager
		private String currentItem;	// current item caught
		
		private Random rand;
		
		// catchable items hashMap
		private HashMap<String, Integer> commonCatchableItems;
		private HashMap<String, Integer> uncommonCatchableItems;
		private HashMap<String, Integer> rareCatchableItems;
	
		public GameStats() {
			rand = new Random();
			commonCatchableItems = new HashMap<String, Integer>();
			uncommonCatchableItems = new HashMap<String, Integer>();
			rareCatchableItems = new HashMap<String, Integer>();
			initializeCatchableItems();
		}
		
		// Initialize list of all catchable items
		public void initializeCatchableItems() {
			// Common items
			commonCatchableItems.put("Salmon", 10);
			commonCatchableItems.put("Cod", 9);
			commonCatchableItems.put("Catfish", 4);
			commonCatchableItems.put("Tilapia", 8);
			commonCatchableItems.put("Carp", 9);
			commonCatchableItems.put("Bass", 11);
			commonCatchableItems.put("Trout", 12);
			commonCatchableItems.put("Boot", 1);
			
			// Uncommon items
			uncommonCatchableItems.put("Mahi Mahi", 30);
			uncommonCatchableItems.put("Grouper", 38);
			uncommonCatchableItems.put("Halibut", 36);
			
			// Rare items
			rareCatchableItems.put("Tuna", 83);
			rareCatchableItems.put("Halibut", 91);
			rareCatchableItems.put("Pufferfish", 87);
			rareCatchableItems.put("Swordfish", 93);
		}
		
		// Triggered when cursor stops
		// Casting animation, fish animation, update stats
		public void cast() {
			if (!isValidWager) return;
			if (GameState.state == GameState.CASTING) return;
			
			isValidWager = false;	// update wager
			
			// Generate random item
			currentItem = getRandomCatchableItem();
			int itemValue = getCatchableItemPrice(currentItem);
			
			System.out.println("Item Caught: " + currentItem);
			System.out.println("Value: $" + itemValue);
			
			// Recalculate balance based on wager
			
			// If wager > value of item, deduct from balance
			if (wager > itemValue) {
				balanceDifference = -wager;
			}
			else if (GamePanel.cursorColor.equals("red")) {
				balanceDifference = itemValue/4;
			}
			else if (GamePanel.cursorColor.equals("yellow")) {
				balanceDifference = itemValue/2;
			}
			else {
				balanceDifference = itemValue;
			}
			
			balance += balanceDifference;
			
			GameState.state = GameState.CASTING;	// update game state
		}
		
		// Select random catchable item
		// cursorColor = red, yellow, or green
		public String getRandomCatchableItem() {
			String cursorColor = GamePanel.cursorColor;
			
			Object[] items;
			Object randItem;
			
			// Cursor in red
			if (cursorColor.equals("red")) {
				items = commonCatchableItems.keySet().toArray();
			}
			// Cursor in yellow
			else if (cursorColor.equals("yellow")) {
				items = uncommonCatchableItems.keySet().toArray();
			}
			// Cursor in green
			else {
				items = rareCatchableItems.keySet().toArray();
			}
			
			randItem = items[rand.nextInt(items.length)];
			currentItem = (String)randItem;
			return currentItem;
		}
		
		// Return price for specified item
		public int getCatchableItemPrice(String name) {
			if (commonCatchableItems.containsKey(name)) {
				return commonCatchableItems.get(name);
			}
			else if (uncommonCatchableItems.containsKey(name)) {
				return uncommonCatchableItems.get(name);
			}
			else if (rareCatchableItems.containsKey(name)) {
				return rareCatchableItems.get(name);
			}
			
			// Item not found
			return 0;
		}
		
		
		//***** Getters and Setters *****//
		
		// Return player's balance
		public int getBalance() {
			return balance;
		}
		
		// Return how much balance has changed
		public int getBalanceDifference() {
			return balanceDifference;
		}
		
		public String getCurrentItem() {
			return currentItem;
		}
		
		// Set value of wager
		public void setWagerValue(String wagerString) {
			try {
				int wager = Integer.parseInt(wagerString);	// update game state
				isValidWager = true;
				this.wager = wager;
			}
			// otherwise print error
			catch (NumberFormatException exception) {
				isValidWager = false;
			}
		}
		
		// getter
		public boolean isValidWager() {
			return isValidWager;
		}
}
