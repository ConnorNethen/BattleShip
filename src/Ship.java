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

	public boolean isSunk() {
		return this.sunk;
	}
	
	public void setSunk() {
		if (this.totalHits < this.data.length) {
			this.sunk = false;
		} else {
			this.sunk = true;
		}
	}
}