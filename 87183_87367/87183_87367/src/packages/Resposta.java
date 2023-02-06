package packages;

import java.io.Serializable;
import java.util.ArrayList;

import auxiliar.Coordenadas;

public class Resposta implements Serializable {
	
	private ArrayList<Coordenadas> pontos;
	private int clientID;
	private int imageID;
	
	
	//Constructor
	public Resposta(ArrayList<Coordenadas> pontos, int imageID, int clientID) {
		this.pontos = pontos;
		this.clientID = clientID;
		this.imageID = imageID;
	}
	
	
	//Methods
	public int getClientID() {
		return clientID;
	}
	
	public ArrayList<Coordenadas> getPontos() {
		return pontos;
	}
	
	public void concatResposta(Resposta resp) {
		pontos.addAll(resp.getPontos());
	}
	
	@Override
	public String toString() {
		return "Resposta [clientID=" + clientID + ", imageID=" + imageID + "]";
	}


	public int getImageID() {
		return imageID;
	}
	
	static final long serialVersionUID = 3L;  //identificador tipo hash para confirmar na descompactação que está tudo ok.

}
