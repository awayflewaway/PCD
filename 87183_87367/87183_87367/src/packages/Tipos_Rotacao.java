package packages;

import java.io.Serializable;
import java.util.ArrayList;

public class Tipos_Rotacao implements Serializable {
	
	private ArrayList<String> tipos_rotacao;
	
	public Tipos_Rotacao(ArrayList<String> tipos_rotacao) {
		this.tipos_rotacao = tipos_rotacao;
	}

	public ArrayList<String> getTipos_rotacao() {
		return tipos_rotacao;
	}
	
	static final long serialVersionUID = 1L;  
	
}
