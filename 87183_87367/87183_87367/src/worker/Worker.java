package worker;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import auxiliar.AnaliseImagem;
import auxiliar.Coordenadas;
import auxiliar.ImageConverter;
import packages.Resposta;
import packages.Task;

public abstract class Worker implements Runnable {
	
	private int porto;                                                                          //porto IP da máquina do servidor.
	private String endereço_servidor;                                                           //endereço da máquina do servidor.
	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private BufferedImage logo;
	private BufferedImage imagem;
	private int clientID;
	private int imageID;
	private ArrayList<Coordenadas> pontos;
	
	
	//Constructor
	public Worker(int porto, String endereço_servidor) {
		this.porto = porto;
		this.endereço_servidor = endereço_servidor;
		
	}
	
	private void connectToServer() {
		InetAddress address;
		try {
			address = InetAddress.getByName(endereço_servidor);
			socket = new Socket(address, porto);
			System.out.println("Trabalhador ligado:\n" + socket + " Através do IP: " + address);
			
			output = new ObjectOutputStream(socket.getOutputStream());
			identifyWorker(output);
			
			input = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			tryToReconnect();
		}
	}
	
	protected abstract BufferedImage rotateLogo(BufferedImage logo);
	
	protected abstract void identifyWorker(ObjectOutputStream output);
	
	private void openTask(Task task) {
		System.out.println("Worker: A descompactar a tarefa");
		BufferedImage[] temp = ImageConverter.byteToImage(task.getImage(), task.getLogo());
		imagem = temp[0];
		logo = temp[1];
		
		clientID = task.getClientID();
		imageID = task.getImageID();
		
	}


	@Override
	public void run() {
		connectToServer();
		while(true) {
			try {
			
				Task task = (Task) input.readObject();
				openTask(task);
				logo = rotateLogo(logo);
								
				pontos = AnaliseImagem.procuraLogo(logo, imagem);
				Resposta pack = new Resposta(pontos, imageID, clientID);
				
				System.out.println("Analise completa, vou enviar os resultados da imagem: " + imageID + " do cliente " + clientID);
				output.writeObject(pack);
				output.reset();
			}	
		 catch (IOException | ClassNotFoundException e) {
			tryToReconnect();
				
			}
		}
	}
	
	
	protected void tryToReconnect() {
		System.out.println("I will try to reconnect in 10 seconds...");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        this.connectToServer();
	}

	
}
