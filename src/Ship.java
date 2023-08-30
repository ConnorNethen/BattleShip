import java.awt.Color;

public class Ship {
	int[] data;
	Color color;
	Boolean sunk;
	public Ship(int[] arr, Color color) {
		this.data = arr;
		this.color = color;
		sunk = false;
	}
}