/*
 * Ship Object
 */
public class Ship {
	public int[] data;
	public int totalHits;
	public int color;
	private boolean sunk;
	
	public Ship(int[] arr, int color) {
		this.data = arr;
		this.totalHits = 0;
		this.color = color;
		this.sunk = false;
	}

	/*
	 * get function
	 */
	public boolean isSunk() {
		return this.sunk;
	}
	
	/*
	 * set function
	 */
	public void setSunk() {
		if (this.totalHits < this.data.length) {
			this.sunk = false;
		} else {
			this.sunk = true;
		}
	}
}