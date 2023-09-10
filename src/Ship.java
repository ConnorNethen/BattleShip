/*
 * Ship Object
 */
public class Ship {
	public int[] data;
	public boolean[] totalHits;
	public int color;
	private boolean sunk;
	
	public Ship(int[] arr, int color) {
		this.data = arr;
		this.totalHits = new boolean[arr.length];
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
		for (int i = 0; i < this.totalHits.length; ++i) {
			if (!this.totalHits[i]) {
				//this.sunk = false;
				return;
			}
		}
		this.sunk = true;
	}
}