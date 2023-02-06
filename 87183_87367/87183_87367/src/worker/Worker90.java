package worker;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Worker90 extends Worker {
	
	public Worker90(int porto, String endereço_servidor) {
		super(porto, endereço_servidor);
	}

//	@Override
//	protected BufferedImage rotateLogo(BufferedImage logo) {
//		BufferedImage rotated_image = new BufferedImage(logo.getHeight(), logo.getWidth(), logo.getType());
//		int[] temp_line = new int[logo.getHeight()];
//		
//		for(int i = 0; i < logo.getHeight(); i++) {
//			for(int j = 0; j < logo.getWidth(); j++) {
//				temp_line[j] = logo.getRGB(j, i);
//				if(j == logo.getHeight()-1) startRotation(rotated_image, temp_line, i);
//				
//			}
//		}
//		return rotated_image;
//	}
//	
//	private void startRotation(BufferedImage rotated_image, int[] temp_line, int height) {
//		for(int i = 0; i < temp_line.length; i++) {
//			rotated_image.setRGB(height, i, temp_line[i]);
//		}
//	}
	
	protected BufferedImage rotateLogo(BufferedImage logo) {
		 int width = logo.getWidth();
		 int height = logo.getHeight();

		 BufferedImage dest = new BufferedImage(height, width, logo.getType());

		 Graphics2D graphics2D = dest.createGraphics();
	     graphics2D.translate((height - width) / 2, (height - width) / 2);
	     graphics2D.rotate(Math.PI / 2, height / 2, width / 2);
   	     graphics2D.drawRenderedImage(logo, null);

		 return dest;
	}

	@Override
	protected void identifyWorker(ObjectOutputStream output) {
		try {
			output.writeObject("Worker de 90");
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args) {
		new Thread(new Worker90(8080, null)).start();
	}
}
