package worker;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class WorkerCustom extends Worker {
	
	private int theta;

	public WorkerCustom(int porto, String endereço_servidor, int theta) {
		super(porto, endereço_servidor);
		this.theta = theta;
	}
	
	public static void main(String[] args) {
		new Thread(new WorkerCustom(8080, null, 90)).start();  //no 90 pode receber um valor qualquer
	}

	@Override
	protected BufferedImage rotateLogo(BufferedImage logo) {
		 int width = logo.getWidth();
		 int height = logo.getHeight();

		 BufferedImage dest = new BufferedImage(height, width, logo.getType());

		 Graphics2D graphics2D = dest.createGraphics();
	     graphics2D.translate((height - width) / 2, (height - width) / 2);
	     graphics2D.rotate(((2*Math.PI) / 360) * theta, height / 2, width / 2);
  	     graphics2D.drawRenderedImage(logo, null);

		 return dest;
	}

	@Override
	protected void identifyWorker(ObjectOutputStream output) {
		try {
			output.writeObject("Worker Custom");
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
