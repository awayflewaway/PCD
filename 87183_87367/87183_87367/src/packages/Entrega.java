package packages;

import java.io.Serializable;
import java.util.ArrayList;

public class Entrega implements Serializable {
	
	private ArrayList<Resposta> entrega;
	
	public Entrega(ArrayList<Resposta> respostas) {
		this.entrega = respostas;
	}

	public ArrayList<Resposta> getEntrega() {
		return entrega;
	}

}
