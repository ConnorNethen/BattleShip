import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class BattleShipPart extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final int WINDOW_HEIGHT = 900;
	private static final int WINDOW_WIDTH = 900;
	
	private static long startTime = 0;
	private static long endTime = 0;
	
	private static final int TOTAL_SHIPS = 6; // Number of ships placed on grid
	private static int totalShipsLeft = TOTAL_SHIPS;
	private static final int TOTAL_SHIP_SPOTS = 19; // Number of spots needed to sink all ships
	private static int totalSpotsLeft = TOTAL_SHIP_SPOTS; // Number of ships left on board
	private static final int TOTAL_GUESSES = 50; // Number of guesses per game
	private static int totalGuessesLeft = TOTAL_GUESSES; // Number of guesses left to guess
	
	private static final String INITIAL_CELL_TEXT = "";
    private static final String EXPOSED_SHIP_HIT_TEXT = "HIT";
    private static final String EXPOSED_SHIP_MISS_TEXT = "MISS";
  
    // visual indication of an exposed MyJButton
    private static final Color EXPOSED_CELL_BACKGROUND_COLOR = Color.blue;
    // colors used when displaying different ships
    private static final Color EXPOSED_SHIP_BACKGROUND_COLOR[] = {Color.darkGray, Color.cyan, Color.green, Color.pink, Color.orange, Color.yellow};
    // colors used when displaying the getStateStr() String
    private static final Color EXPOSED_CELL_FOREGROUND_COLOR_MAP[] = {Color.white, Color.red};

  
    private static final int GRID_ROWS = 10;
    private static final int GRID_COLS = 10;
    private int[][] shipGrid = new int[GRID_ROWS][GRID_COLS];
    private static int[] shipLocations = new int[TOTAL_SHIP_SPOTS];
    private static int[] guessedHitLocations = new int[TOTAL_SHIP_SPOTS];
	public static Ship[] playingShips;

    private static final int IS_A_MISS = 0;
  
    private boolean running = true;
    private boolean gameStatus = true;
  
    public BattleShipPart() {
    	this.setTitle("BattleShip                                                         " + 
                BattleShipPart.totalGuessesLeft +" Guesses Left, " + BattleShipPart.totalShipsLeft + " Ships Left");
    	this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    	this.setResizable(false);
    	this.setLayout(new GridLayout(GRID_ROWS, GRID_COLS, 0, 0));
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    	// set the grid of MyJbuttons
    	this.createContents();
    
    	// place ships on the board
    	this.setShips();
    	
    	this.setVisible(true);
    }

    public void createContents() {
    	for (int sgr = 0; sgr < GRID_ROWS; ++sgr) {  
    		for (int sgc = 0; sgc < GRID_COLS; ++sgc) {  
    			this.shipGrid[sgr][sgc] = 0;
        
    			// create a MyJButton that will be at location (sgr, sgc) in the GridLayout
    			MyJButton but = new MyJButton(INITIAL_CELL_TEXT, sgr, sgc); 
        
    			// register the event handler with this MyJbutton
    			but.addActionListener(new MyListener());
        
    			// add the MyJButton to the GridLayout collection
    			this.add(but);
    		}  
    	}
    }


    // begin nested private class
    private class MyListener implements ActionListener {
    	public void actionPerformed(ActionEvent event) {
    		int cellExposed = -1;
    		if ( running ) {
    			MyJButton mjb = (MyJButton)event.getSource();
        
    			// is the MyJbutton that the mouse action occurred in already exposed
    			boolean exposed = mjb.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
       
    			
    			// expose a cell : left click
    			if ( !exposed ) {
    				cellExposed = exposeCell(mjb);
    				if (BattleShipPart.totalSpotsLeft == 0) {
    					gameStatus = true;
    					running = false;
    					endTime = System.currentTimeMillis();
    				}
    				if (BattleShipPart.totalGuessesLeft == 0) {
    					exposeShips(mjb);
    					gameStatus = false;
    					running = false;
    					endTime = System.currentTimeMillis();
    				}
    				if (cellExposed != -1) {
    					BattleShipPart.totalShipsLeft = checkSunk(playingShips);
            		}
    				
    				setTitle("BattleShip                                                         " + 
    		                BattleShipPart.totalGuessesLeft +" Guesses Left, " + BattleShipPart.totalShipsLeft + " Ships Left");
    			}

    		}
    		
    		if (!running) {
    			if (gameStatus) { // If user wins game
    				setTitle("BattleShip                                                         " +  "Congratulations, You Win!");
    				float gameTime = (float) ((endTime - startTime) / 1000.0);
        			System.out.printf("\n\nCompleted game in %.2f Seconds", gameTime);
    			}
    			else { // If user loses game
    				int shipsLeft = 6 - BattleShipPart.totalShipsLeft;
    				if (shipsLeft == 1) {
    					setTitle("BattleShip                                                         " +  "Game Over, You Lose! You sunk " 
    							+ shipsLeft + " Ship!");
    				} else
    					setTitle("BattleShip                                                         " +  "Game Over, You Lose! You sunk " 
    						+ shipsLeft + " Ships!");
    			}
    		}
    	}
    	
    	/*
    	 * Upon loosing the game, this method exposes all of the ship spots
    	 * that were not hit by the user. The spots are exposed to reveal the
    	 * ship's color and the text "NOT HIT".
    	 * Argument: MyJButton mjb
    	 */
    	public void exposeShips(MyJButton mjb) {
    		// Upon losing game, reveal the remaining ships that were not hit
    		for (Ship s: BattleShipPart.playingShips) {
    			for (int i = 0; i < s.data.length; ++i) {
    				if (!s.totalHits[i]) {
    					MyJButton newButton = (MyJButton)mjb.getParent().getComponent(s.data[i]);
    	    			newButton.setBackground(EXPOSED_SHIP_BACKGROUND_COLOR[s.color-1]);
    	    			newButton.setText("NOT HIT");
    	    			newButton.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[1]);
    				}
    			}
    				
    		}
    	}
    	
    	/*
    	 * Method to expose the cell that has been clicked on
    	 * Argument: MyJButton mjb
    	 * Returns: int cellreturned representing the value of the cell that was clicked
    	 */
    	public int exposeCell(MyJButton mjb) {
    		if ( !running )
    			return -1;
    		
    		int cellReturned = -1;
    		int foreGround = -1;
    		boolean shipSunk = false;
    		
    		// if the MyJButton that was just exposed is a MISS ...
    		if ( shipGrid[mjb.ROW][mjb.COL] == IS_A_MISS ) {
    			mjb.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
    			--BattleShipPart.totalGuessesLeft;
    			foreGround = 0;
    		} else { // if the MyJButton that was just exposed is a HIT ...
    			cellReturned = mjb.ROW * 10 + mjb.COL;
    			if (shipIsSunk(cellReturned)) return cellReturned;
    			foreGround = 1;
    			if (isUnHitLocation(cellReturned)) {
    				BattleShipPart.guessedHitLocations[BattleShipPart.totalSpotsLeft-1] = cellReturned;
    				mjb.setBackground(EXPOSED_SHIP_BACKGROUND_COLOR[shipGrid[mjb.ROW][mjb.COL]-1]);
    				--BattleShipPart.totalSpotsLeft;
    				shipSunk = modifyShip(cellReturned, mjb); // modify the ship object
    			}
    			
    		}
    		
    		// expose this MyJButton
    		if (!shipSunk) {
    			mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));
    		}
    		mjb.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[foreGround]);
    		
    		return cellReturned;
    	}
    	
    	/*
    	 * Method that modifies the ship object upon a hit
    	 * Arguments: int cell, MyJButton mjb
    	 * Returns: boolean flag representing whether of not the hit cell caused a ship to be sunk
    	 */
    	public boolean modifyShip(int cell, MyJButton mjb) {
    		Boolean flag = false;
    		Boolean shipFound = false;
    		for (Ship s : BattleShipPart.playingShips) {
    			for (int i = 0; i < s.data.length; ++i) {
    				if (s.data[i] == cell) {
    					s.totalHits[i] = true;
    					s.setSunk();
    					if (s.isSunk()) {
    						// Change the text on the ships cells to "SUNK"
    						modifyShipCells(s, mjb);
    						flag = true;
    					}
    					shipFound = true;
    					break;
    				}
    			}
    			if (shipFound) break;
    		}
    		
    		return flag;
    	}
    	
    	/*
    	 * Helper function for "modifyShip()" that changes the text of the currently hit
    	 * ship that has just been sunk. The text changes from "HIT" to "SUNK"
    	 * Arguments: Ship s, MyJButton mjb
    	 */
    	public void modifyShipCells(Ship s, MyJButton mjb) {
    		for (int i = 0; i < s.data.length; ++i) {
    			MyJButton newButton = (MyJButton)mjb.getParent().getComponent(s.data[i]);
    			newButton.setText("SUNK");
    		}
    	}
    	
    	/*
    	 * Helper function for "exposeCell()" that checks the guessHitLocations array to
    	 * see if the current clicked on cell has been already hit. If it has been hit 
    	 * return false, it it hasn't been hit return True.
    	 * Argument: int cell
    	 * Returns: boolean representing whether or not the current hit spot has been guessed.
    	 */
    	public boolean isUnHitLocation(int cell) {
    		for (int i = 0; i < BattleShipPart.guessedHitLocations.length; ++i) {
    			if (BattleShipPart.guessedHitLocations[i] == cell) return false;
    		}
    		return true;
    	}
    	
    	/*
    	 * Helper function for "exposeCell()" that checks whether or not the clicked on 
    	 * cell (that resulted in a hit) is part of a already sunken ship. If the ship has
    	 * been sunk, it returns true, if the ship has not been sunk it returns false.
    	 * Argument: int cell
    	 * Returns: boolean representing whether or not the ship that was hit is sunk.
    	 */
    	public boolean shipIsSunk(int cell) {
    		boolean flag = false;
    		Ship cur = null;
    		for (Ship s: BattleShipPart.playingShips) {
    			cur = s;
    			for (int i = 0; i < s.data.length; ++i) {
    				if (s.data[i] == cell) {
    					flag = true;
    					break;
    				}
    			}
    			if (flag) break;
    		}
    		return cur.isSunk();
    	}
    }
    // end nested private class

    public static void main(String[] args)
    {
    	
    	// Generate Game
    	BattleShipPart.startTime = System.currentTimeMillis();
    	new BattleShipPart();
    }
  
    //************************************************************************************************

    // place ships on the BattleShip Grid
    private void setShips() {
    	int row, col;
    	shipLocations = createShips();
    	Arrays.fill(BattleShipPart.guessedHitLocations, -1);
    	
    	// Create and Add spots to the ship's data
    	Ship ship1 = new Ship(setSpots(shipLocations, 0, 4), 1);
    	Ship ship2 = new Ship(setSpots(shipLocations, 5, 8), 2);
    	Ship ship3 = new Ship(setSpots(shipLocations, 9, 11), 3);
    	Ship ship4 = new Ship(setSpots(shipLocations, 12, 14), 4);
    	Ship ship5 = new Ship(setSpots(shipLocations, 15, 16), 5);
    	Ship ship6 = new Ship(setSpots(shipLocations, 17, 18), 6);
    	
    	playingShips = new Ship[]{ship1, ship2, ship3, ship4, ship5, ship6};
    	
    	// PRINTS OUT LOCATIONS OF SHIPS (PLEASE DELETE) //
    	System.out.print("These are the ship locations:");
    	int num = 1;
    	for (Ship s: BattleShipPart.playingShips) {
    		System.out.println("\nPlaying ship " + num);
    		for (int i = 0; i < s.data.length; ++i) {
    			System.out.print(s.data[i] + " ");
    		}
    		++num;
    	}
    	// ********************************************* //

    	for(Ship s : playingShips){
    		for (int i = 0; i < s.data.length; ++i) {
        		row = s.data[i] / 10;
        		col = s.data[i] % 10;
        		this.shipGrid[row][col] = s.color;
        	}
    	}

    }
    
    /*
     * Helper method for setShips
     * 
     * Creates an array that holds the spots of a single ship
     */
    private int[] setSpots(int[] arr, int left, int right) {
    	int[] newArray = new int[right-left+1];
    	int index = 0;
    	for (int i = left; i <= right; ++i) {
    		newArray[index] = arr[i];
    		++index;
    	}
    	return newArray;
    }
    
    
    /*
     * Creates an array of integers that correspond ship spots
     */
    private int[] createShips() {
    	int[] spots = new int[TOTAL_SHIP_SPOTS];
		setLoc(spots);
		
		int size = TOTAL_SHIPS; // TOTAL_SHIPS = 6
		int up1, down1, left1, right1;
		int up2, down2, left2, right2;
		boolean vert = true; boolean vert1 = true; boolean vert2 = true;
		boolean horz = true; boolean horz1 = true; boolean horz2 = true;
		boolean flag = false;
		boolean flagOffset1 = false;
		boolean flagOffset2 = false;
		int offset1 = 0; // 0 or 3
		int offset2 = 0; // 0 or 2
		
		while (spots[TOTAL_SHIP_SPOTS-1] == -1) {
			flag = false; 
			int start = (int) (Math.random() * 100);
			up1 = start - 10; down1 = start + 10; up2 = start - 20; down2 = start + 20;
			left1 = start - 1; right1 = start + 1; left2 = start - 2; right2 = start + 2;
			if (checkSpot(spots, start)) { // start location is acceptable
				
				// MAKE THE 5 SPOT SHIP
				if (size == 6) {
					// check vert
					if (checkSpot(spots, up1) && checkSpot(spots, up2) && checkSpot(spots, down1) && checkSpot(spots, down2)) {
						vert = true;
					} else { vert = false; }
					
					// check horz
					if (checkSpot(spots, left1) && checkSpot(spots, left2) && checkSpot(spots, right1) && checkSpot(spots, right2)) {
						int[] temp = new int[] {left1, left2, start, right1, right2};
						if (checkHorz(temp)) {
							horz = true;
						} else horz = false;
					} else  { horz = false; }
					
					// pick
					if (vert && horz) {
						int choice = (int) (Math.random() * 2); // 0 = vert, 1 = horz
						if (choice == 0) { // vert
							spots[0] = up2; spots[1] = up1; spots[2] = start; spots[3] = down1; spots[4] = down2;
						} else { // horz
							spots[0] = left2; spots[1] = left1; spots[2] = start; spots[3] = right1; spots[4] = right2;
						}
					} else if (vert && !horz) { // vert
						spots[0] = up2; spots[1] = up1; spots[2] = start; spots[3] = down1; spots[4] = down2;
					} else if (!vert && horz) { // horz
						spots[0] = left2; spots[1] = left1; spots[2] = start; spots[3] = right1; spots[4] = right2;
					} else { flag = true; }
				}
				
				// MAKE THE 4 SPOT SHIP
				if (size == 5) {
					// check vert1
					if (checkSpot(spots, up1) && checkSpot(spots, up2) && checkSpot(spots, down1)) {
						vert1 = true;
					} else { vert1 = false; }
					
					// check vert2
					if (checkSpot(spots, up1) && checkSpot(spots, down1) && checkSpot(spots, down2)) {
						vert2 = true;
					} else  { vert2 = false; }
					
					// check horz1
					if (checkSpot(spots, left1) && checkSpot(spots, left2) && checkSpot(spots, right1)) {
						int[] temp = new int[] {left2, left1, start, right1};
						if (checkHorz(temp)) {
							horz1 = true;
						} else horz1 = false;
					} else { horz1 = false; }
					
					// check horz2
					if (checkSpot(spots, left1) && checkSpot(spots, right1) && checkSpot(spots, right2)) {
						int[] temp = new int[] {left1, start, right1, right2};
						if (checkHorz(temp)) {
							horz2 = true;
						} else horz2 = false;
					} else  { horz2 = false; }
					
					// pick
					if (vert1 && vert2 && horz1 && horz2) {
						int choice = (int) (Math.random() * 4); // 0 = vert1, 1 = vert2, 2 = horz1, 3 = horz2
						if (choice == 0) { // vert1
							spots[5] = up2; spots[6] = up1; spots[7] = start; spots[8] = down1;
						} else if (choice == 1) { // vert2
							spots[5] = up1; spots[6] = start; spots[7] = down1; spots[8] = down2;
						} else if (choice == 2) { // horz1
							spots[5] = left2; spots[6] = left1; spots[7] = start; spots[8] = right1;
						} else if (choice == 3) { // horz2
							spots[5] = left1; spots[6] = start; spots[7] = right1; spots[8] = right2;
						}
					} else if (vert1) { // vert1
						spots[5] = up2; spots[6] = up1; spots[7] = start; spots[8] = down1;
					} else if (horz1) { // horz1
						spots[5] = left2; spots[6] = left1; spots[7] = start; spots[8] = right1;
					} else if (horz2) { // horz2
						spots[5] = left1; spots[6] = start; spots[7] = right1; spots[8] = right2;
					} else if (vert2) { // vert2
						spots[5] = up1; spots[6] = start; spots[7] = down1; spots[8] = down2;
					} else { flag = true; }
				}
				
				// MAKE THE 3 SPOT SHIP (2 TIMES)
				if (size == 4 || size == 3) {
					// check vert
					if (checkSpot(spots, up1) && checkSpot(spots, down1)) {
						vert = true;
					} else { vert = false; }
					
					// check horz
					if (checkSpot(spots, left1) && checkSpot(spots, right1)) {
						int[] temp = new int[] {left1, start, right1};
						if (checkHorz(temp)) {
							horz = true;
						} else horz = false;
					} else  { horz = false; }
					
					// pick
					if (flagOffset1) {
						offset1 = 3;
					}
					if (vert && horz) {
						int choice = (int) (Math.random() * 2); // 0 = vert, 1 = horz
						if (choice == 0) { // vert
							spots[9+offset1] = up1; spots[10+offset1] = start; spots[11+offset1] = down1;
						} else { // horz
							spots[9+offset1] = left1; spots[10+offset1] = start; spots[11+offset1] = right1;
						}
					} else if (vert && !horz) { // vert
						spots[9+offset1] = up1; spots[10+offset1] = start; spots[11+offset1] = down1;
					} else if (!vert && horz) { // horz
						spots[9+offset1] = left1; spots[10+offset1] = start; spots[11+offset1] = right1;
					} else { flag = true; }
				}
				
				// MAKE THE 2 SPOT SHIP (2 TIMES)
				if (size == 2 || size == 1) {
					// check vert1
					if (checkSpot(spots, up1)) {
						vert1 = true;
					} else { vert1 = false; }
					
					// check vert2
					if (checkSpot(spots, down1)) {
						vert2 = true;
					} else  { vert2 = false; }
					
					// check horz1
					if (checkSpot(spots, left1)) {
						int[] temp = new int[] {left1, start};
						if (checkHorz(temp)) {
							horz1 = true;
						} else horz1 = false;
					} else { horz1 = false; }
					
					// check horz2
					if (checkSpot(spots, right1)) {
						int[] temp = new int[] {start, right1};
						if (checkHorz(temp)) {
							horz2 = true;
						} else horz2 = false;
					} else  { horz2 = false; }
				}
				
				// pick
				if (flagOffset2) {
					offset2 = 2;
				}
				if (vert1 && vert2 && horz1 && horz2) {
					int choice = (int) (Math.random() * 4); // 0 = vert1, 1 = vert2, 2 = horz1, 3 = horz2
					if (choice == 0) { // vert1
						spots[15+offset2] = up1; spots[16+offset2] = start;
					} else if (choice == 1) { // vert2
						spots[15+offset2] = start; spots[16+offset2] = down1;
					} else if (choice == 2) { // horz1
						spots[15+offset2] = left1; spots[16+offset2] = start;
					} else if (choice == 3) { // horz2
						spots[15+offset2] = start; spots[16+offset2] = right1;
					}
				} else if (vert1) { // vert1
					spots[15+offset2] = up1; spots[16+offset2] = start;
				} else if (horz1) { // horz1
					spots[15+offset2] = left1; spots[16+offset2] = start;
				} else if (horz2) { // horz2
					spots[15+offset2] = start; spots[16+offset2] = right1;
				} else if (vert2) { // vert2
					spots[15+offset2] = start; spots[16+offset2] = down1;
				} else { flag = true; }
				
			
				// if a ship was added properly
				if (!flag) { --size; }
				if (size == 3) { flagOffset1 = true; }
				if (size == 1) { flagOffset2 = true; }
			}
		}
    	return spots;
    }
    
    private static void setLoc(int[] arr) {
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = -1;
		}
	}
	
    /*
     * Helper function for createShips()
     */
	private static boolean checkSpot(int[] spots, int num) {
		for (int i = 0; i < spots.length; ++i) {
			if (num == spots[i]) return false; // bad location
		}
		if (num >= 100 || num < 0) return false; // bad location
		return true; // good location
	}
	
	/*
     * Helper function for createShips()
     */
	private static boolean checkHorz(int[] arr) {
		int row = arr[0] / 10;
		boolean check = true;
		for (int i = 1; i < arr.length; ++i) {
			if (row != (arr[i] / 10)) {
				check = false;
			}
		}
		return check;
	}
	
	/*
     * Checks how many ships have been sunk
     */
	private static int checkSunk(Ship[] ships) {
		int unSunk = 0;
		for (Ship s : ships) {
			if (!s.isSunk()) {
				++unSunk;
			}
		}
		return unSunk;
	}
  
    private String getGridValueStr(int row, int col) {
    	if ( this.shipGrid[row][col] == IS_A_MISS )
    		return BattleShipPart.EXPOSED_SHIP_MISS_TEXT;
    	// this MyJButton is a ship
    	else
    		return BattleShipPart.EXPOSED_SHIP_HIT_TEXT;
    }
}

