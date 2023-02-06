package auxiliar;

import java.io.Serializable;

public class Coordenadas implements Serializable {
	
	private Ponto ponto_inicial;
	private Ponto ponto_final;
	
	
	public Coordenadas(Ponto ponto_inicial, Ponto ponto_final) {
		this.ponto_inicial = ponto_inicial;
		this.ponto_final = ponto_final;
	}
	
	
	//Methods
	public Ponto getPI() {
		return ponto_inicial;
	}
	
	public Ponto getPF() {
		return ponto_final;
	}
	
	static final long serialVersionUID = 6L;  

}
