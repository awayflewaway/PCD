package worker;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Worker270 extends Worker {

	public Worker270(int porto, String endereço_servidor) {
		super(porto, endereço_servidor);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		new Thread(new Worker270(8080, null)).start();
	}
	
	@Override
	protected BufferedImage rotateLogo(BufferedImage logo) {
		 int width = logo.getWidth();
		 int height = logo.getHeight();

		 BufferedImage dest = new BufferedImage(height, width, logo.getType());

		 Graphics2D graphics2D = dest.createGraphics();
	     graphics2D.translate((height - width) / 2, (height - width) / 2);
	     graphics2D.rotate(((2*Math.PI) / 360) * 270, height / 2, width / 2);
  	     graphics2D.drawRenderedImage(logo, null);

		 return dest;
	}

	
	@Override
	protected void identifyWorker(ObjectOutputStream output) {
		try {
			output.writeObject("Worker de 270");
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
