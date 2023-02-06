package packages;

import java.io.Serializable;

public class Task implements Serializable {
	
	private byte[] image;
	private byte[] logo;
	private String tipoProcura;
	private int clientID;
	private int imageID;
	
	public Task(byte[] image, byte[] logo, String tipoProcura, int imageID, int clientID) {
		this.image = image;
		this.logo = logo;
		this.tipoProcura = tipoProcura;
		this.clientID = clientID;
		this.imageID = imageID;
	}
	
	public byte[] getImage() {
		return image;
	}
	
	public byte[] getLogo() {
		return logo;
	}
	
	public String getTipoProcura() {
		return tipoProcura;
	}
	
	public int getClientID() {
		return clientID;
	}
	
	public int getImageID() {
		return imageID;
	}
	
	static final long serialVersionUID = 2L;  

}
