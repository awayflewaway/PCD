package packages;

import java.io.Serializable;
import java.util.ArrayList;

public class Pedido implements Serializable {
	
	private ArrayList<byte[]> imagens = new ArrayList<>();
	private byte[] logo;
	private ArrayList<String> tiposProcura;
	
	public Pedido(ArrayList<byte[]> imagens, byte[] logo, ArrayList<String> tiposProcura) {
		this.imagens = imagens;
		this.logo = logo;
		this.tiposProcura = tiposProcura;
	}

	public ArrayList<byte[]> getImagens() {
		return imagens;
	}

	public byte[] getLogo() {
		return logo;
	}

	public ArrayList<String> getTiposProcura() {
		return tiposProcura;
	}
	
	static final long serialVersionUID = 4L;  
	
}
