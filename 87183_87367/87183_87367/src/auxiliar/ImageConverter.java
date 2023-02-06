package auxiliar;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import javax.imageio.ImageIO;

public class ImageConverter {

	public static BufferedImage getImageFromString(String path) throws IOException {            //adquire a imagem e transforma em BufferedImage
		BufferedImage image = ImageIO.read(new File(path));
		System.out.println("Image " + image.getWidth() + " x " + image.getHeight());
		return image;
	}
	
	public static BufferedImage getImageFromFile(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		System.out.println("Image " + image.getWidth() + " x " + image.getHeight());
		return image;
	}
	
	public static byte[] imageToByte(BufferedImage image) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] converted_image;
		
		ImageIO.write(image, "png", buffer);
		buffer.flush();
		converted_image = buffer.toByteArray();
		buffer.close();
		
		return converted_image;
	}
	
	
	public static Object byteToObject(BufferedImage convertedImage) throws ClassNotFoundException, IOException {
		try (ByteArrayInputStream buffer = new ByteArrayInputStream(imageToByte(convertedImage)); 
			 ObjectInput out = new ObjectInputStream(buffer);) {
			return out.readObject();
		}
	}
	
	public static BufferedImage[] byteToImage(byte[] converted_image, byte[] converted_logo) {   
		BufferedImage[] temp_list = new BufferedImage[2];
		InputStream in_image = new ByteArrayInputStream(converted_image);
		InputStream in_logo = new ByteArrayInputStream(converted_logo);
		BufferedImage byte_to_image;
		BufferedImage byte_to_logo;

		try {
			byte_to_image = ImageIO.read(in_image);
			byte_to_logo = ImageIO.read(in_logo);
			ImageIO.write(byte_to_image, "png", new File("temp_image.png"));
			temp_list[0] = byte_to_image;
			ImageIO.write(byte_to_logo, "png", new File("temp_logo.png"));
			temp_list[1] = byte_to_logo;
			return temp_list;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
