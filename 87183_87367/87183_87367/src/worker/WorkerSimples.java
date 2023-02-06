package worker;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class WorkerSimples extends Worker {

	public WorkerSimples(int porto, String endereço_servidor) {
		super(porto, endereço_servidor);
	}

	@Override
	protected BufferedImage rotateLogo(BufferedImage logo) {
		return logo;
	}

	protected void identifyWorker(ObjectOutputStream output) {
		try {
			output.writeObject("Worker Simples");
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Thread(new WorkerSimples(8080, null)).start();
	}
	

}
