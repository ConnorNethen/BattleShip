import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class BattleShipPart extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final int WINDOW_HEIGHT = 900;
	private static final int WINDOW_WIDTH = 900;
	
	
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
    private static final Color EXPOSED_SHIP_BACKGROUND_COLOR = Color.darkGray;
    // colors used when displaying the getStateStr() String
    private static final Color EXPOSED_CELL_FOREGROUND_COLOR_MAP[] = {Color.white, Color.red};

  
    // holds the "number of mines in perimeter" value for each MyJButton 
    private static final int GRID_ROWS = 10;
    private static final int GRID_COLS = 10;
    private int[][] shipGrid = new int[GRID_ROWS][GRID_COLS];
    private static int[] shipLocations = new int[TOTAL_SHIP_SPOTS];
    private static int[] hitLocations = new int[TOTAL_SHIP_SPOTS];

    private static final int IS_A_MISS = 0;
    private static final int IS_A_HIT = 1;
  
    private boolean running = true;
    private boolean gameStatus = true;
    //private boolean flag = false;
  
    public BattleShipPart() {
    	this.setTitle("BattleShip                                                         " + 
                BattleShipPart.totalGuessesLeft +" Guesses Left, " + BattleShipPart.totalShipsLeft + " Ships Left");
    	this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    	this.setResizable(false);
    	this.setLayout(new GridLayout(GRID_ROWS, GRID_COLS, 0, 0));
    	this.setDefaultCloseOperation(EXIT_ON_CLOSE);

    	// set the grid of MyJbuttons
    	this.createContents();
    
    	// place MINES number of mines in sGrid and adjust all of the "mines in perimeter" values
    	this.setShips();
    	
    	this.setVisible(true);
    }

    public void createContents() {
    	for (int sgr = 0; sgr < GRID_ROWS; ++sgr) {  
    		for (int sgc = 0; sgc < GRID_COLS; ++sgc) {  
    			// set sGrid[sgr][sgc] entry to 0 - no mines in it's perimeter
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
    			// used to determine if ctrl or alt key was pressed at the time of mouse action
    			//int mod = event.getModifiers();
    			MyJButton mjb = (MyJButton)event.getSource();
        
    			// is the MyJbutton that the mouse action occurred in already exposed
    			boolean exposed = mjb.getBackground().equals(EXPOSED_CELL_BACKGROUND_COLOR);
       
    			
    			// expose a cell : left click
    			if ( !exposed ) {
    				cellExposed = exposeCell(mjb);
    				if (BattleShipPart.totalSpotsLeft == 0) {
    					gameStatus = true;
    					running = false;
    				}
    				if (BattleShipPart.totalGuessesLeft == 0) {
    					exposeShips(mjb);
    					gameStatus = false;
    					running = false;
    				}
    				if (cellExposed != -1) {
            			BattleShipPart.totalShipsLeft = checkSunk(BattleShipPart.shipLocations, cellExposed);
            		}
    				
    				setTitle("BattleShip                                                         " + 
    		                BattleShipPart.totalGuessesLeft +" Guesses Left, " + BattleShipPart.totalShipsLeft + " Ships Left");
    			}
    			//exposeShips(mjb);
    		}
    		
    		
    		if (!running) {
    			if (gameStatus) // If user wins game
    				setTitle("BattleShip                                                         " +  "Congratulations, You Win!");
    			else // If user loses game
    				setTitle("BattleShip                                                         " +  "Game Over, You Lose!");
    		}
    	}
    	
    	
    	public void exposeShips(MyJButton mjb) {
    		// Upon losing game, reveal the remaining ships that were not hit
    		for (int i = 0; i < shipLocations.length; ++i) {
    			MyJButton newButton = (MyJButton)mjb.getParent().getComponent(shipLocations[i]);
    			newButton.setBackground(EXPOSED_SHIP_BACKGROUND_COLOR);
    			newButton.setText(EXPOSED_SHIP_HIT_TEXT);
    			newButton.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[shipGrid[newButton.ROW][newButton.COL]]);
    			mjb.setText(getGridValueStr(newButton.ROW, newButton.COL));
    		}
    	}
    	
    	
    	public int exposeCell(MyJButton mjb) {
    		if ( !running )
    			return -1;
    		
    		int cellReturned = -1;
    		// if the MyJButton that was just exposed is a ship spot ...
    		if ( shipGrid[mjb.ROW][mjb.COL] == IS_A_HIT ) {
    			mjb.setBackground(EXPOSED_SHIP_BACKGROUND_COLOR);
    			--BattleShipPart.totalSpotsLeft;
    			cellReturned = mjb.ROW * 10 + mjb.COL;
    		} else { // if the MyJButton that was just exposed is a miss ...
    			mjb.setBackground(EXPOSED_CELL_BACKGROUND_COLOR);
    			--BattleShipPart.totalGuessesLeft;
    		}
    		
    		// expose this MyJButton 
    		mjb.setForeground(EXPOSED_CELL_FOREGROUND_COLOR_MAP[shipGrid[mjb.ROW][mjb.COL]]);
    		mjb.setText(getGridValueStr(mjb.ROW, mjb.COL));
    		return cellReturned;
    	}
    }
    // end nested private class

    public static void main(String[] args)
    {
    	
    	// Generate ships
    	new BattleShipPart();
    }
  
    //************************************************************************************************

    // place ships on the BattleShip Grid
    private void setShips()
    {
    	int row, col;
    	shipLocations = createShips();
    	for (int i = 0; i < shipLocations.length; ++i) {
    		row = shipLocations[i] / 10;
    		col = shipLocations[i] % 10;
    		this.shipGrid[row][col] = IS_A_HIT;
    	}
    	setLoc(hitLocations); // for determining number of ships left
    }
    
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
	
	private static boolean checkSpot(int[] spots, int num) {
		for (int i = 0; i < spots.length; ++i) {
			if (num == spots[i]) return false; // bad location
		}
		if (num >= 100 || num < 0) return false; // bad location
		return true; // good location
	}
	
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
	
	private static int checkSunk(int[] arr, int cell) { // given shipLocations and cellExposed
		// mark which spot was hit
		for (int i = 0; i < arr.length; ++i) {
			if (arr[i] == cell) {
				hitLocations[i] = 1; // location has been hit
			}	
		}
		
		int sunk = 0;
		boolean isSunk = true;
		// check if any sunk ships --> 0-4, 5-8, 9-11, 12-14, 15-16, 17-18
		// check 5 spot ship
		for (int i = 0; i < 5; ++i) {
			if (hitLocations[i] == -1) {
				isSunk = false;
			}
		}
		if (isSunk) {
			++sunk;
		} else { isSunk = true; }
		
		// check 4 spot ship
		for (int i = 5; i < 9; ++i) {
			if (hitLocations[i] == -1) {
				isSunk = false;
			}
		}
		if (isSunk) {
			++sunk;
		} else { isSunk = true; }
		
		// check 3 spot ship (2 times)
		int j = 0;
		while (j <= 3) {
			for (int i = 9+j; i < 12+j; ++i) {
				if (hitLocations[i] == -1) {
					isSunk = false;
				}
			}
			if (isSunk) {
				++sunk;
			} else { isSunk = true; }
			j = j + 3;
		}
		
		// check 2 spot ship (2 times)
		j = 0;
		while (j <= 2) {
			for (int i = 15+j; i < 17+j; ++i) {
				if (hitLocations[i] == -1) {
					isSunk = false;
				}
			}
			if (isSunk) {
				++sunk;
			} else { isSunk = true; }
			j = j + 2;
		}
		
		return TOTAL_SHIPS - sunk;
	}
  
    private String getGridValueStr(int row, int col) {
    	if ( this.shipGrid[row][col] == IS_A_MISS )
    		return BattleShipPart.EXPOSED_SHIP_MISS_TEXT;
    	// this MyJButton is a ship
    	else
    		return BattleShipPart.EXPOSED_SHIP_HIT_TEXT;
    }
}

