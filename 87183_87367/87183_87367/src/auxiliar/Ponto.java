package auxiliar;

import java.io.Serializable;

public class Ponto implements Serializable {
	
	private int x;
	private int y;
	
	public Ponto(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	static final long serialVersionUID = 5L;  
}

