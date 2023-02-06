package worker;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Worker180 extends Worker {

	public Worker180(int porto, String endereço_servidor) {
		super(porto, endereço_servidor);
	}

//	@Override
//	protected BufferedImage rotateLogo(BufferedImage logo) {
//		int[] temp_line = new int[logo.getWidth()];
//		BufferedImage rotated_image = new BufferedImage(logo.getWidth(), logo.getHeight(), logo.getType());
//		
//		for(int i = 0; i < logo.getHeight(); i++) {
//			for(int j = 0; j < logo.getWidth(); j++) {
//				temp_line[j] = logo.getRGB(j, i);
//				
//				swap(rotated_image, organizeArray(temp_line), i);
//			}
//		}
//		
//		return null;
//	}
//
//	private void swap(BufferedImage rotated_image, int[] temp_line, int height) {
//		
//		for(int j = 0; j < rotated_image.getWidth(); j++) {
//			rotated_image.setRGB(j, rotated_image.getHeight()-height, temp_line[j]);
//		}
//		
//		
//	}
//	
//	private int[] organizeArray(int[] temp_line) {
//		int[] line = new int[temp_line.length];
//		
//		for(int j = 0; j < line.length; j++) {
//			line[j] = temp_line[line.length-j];
//		}
//		
//		return line;
//	}
	
	
	@Override
	protected BufferedImage rotateLogo(BufferedImage logo) {
		 int width = logo.getWidth();
		 int height = logo.getHeight();

		 BufferedImage dest = new BufferedImage(height, width, logo.getType());

		 Graphics2D graphics2D = dest.createGraphics();
	     graphics2D.translate((height - width) / 2, (height - width) / 2);
	     graphics2D.rotate(Math.PI, height / 2, width / 2);
  	     graphics2D.drawRenderedImage(logo, null);

		 return dest;
	}
	
	
	@Override
	protected void identifyWorker(ObjectOutputStream output) {
		try {
			output.writeObject("Worker de 180");
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new Thread(new Worker180(8080, null)).start();
	}

}
