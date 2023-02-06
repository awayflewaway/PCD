package auxiliar;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AnaliseImagem {
	
	//Methods
	public static ArrayList<Coordenadas> procuraLogo(BufferedImage logo, BufferedImage image) {
		int count = 0;
		ArrayList<Coordenadas> pontos = new ArrayList<Coordenadas>();
	
		for(int h = 0; h < image.getHeight()-logo.getHeight(); h++) {
			for(int w = 0; w < image.getWidth()-logo.getWidth(); w++) {
				if(logo.getRGB(0, 0) == image.getRGB(w, h)) {
					if(callComparator(w, h, logo, image)) {
						pontos.add(new Coordenadas(new Ponto(w, h), new Ponto(w+logo.getWidth(), h+logo.getHeight())));
						int x1 = pontos.get(count).getPI().getX();
						int y1 = pontos.get(count).getPI().getY();
						int x2 = pontos.get(count).getPF().getX();
						int y2 = pontos.get(count).getPF().getY();
						System.out.println("Logo encontrado em: (" + x1 + "," + y1 + ") e (" + x2 + "," + y2 + ")");
						++count;
						if(w+logo.getWidth() < image.getWidth()-logo.getWidth()) w += logo.getWidth();
						else break;
					}
				}
			}
		}
		return pontos;
	}
	
	/*private static boolean existeLogo(File logo1, File image1) {
		
		try {
			BufferedImage logo = ImageConverter.getImageFromFile(logo1);
			BufferedImage image = ImageConverter.getImageFromFile(image1);
			for(int h = 0; h < image.getHeight()-logo.getHeight(); h++) {
				for(int w = 0; w < image.getWidth()-logo.getWidth(); w++) {
					if(logo.getRGB(0, 0) == image.getRGB(w, h)) {
						if(callComparator(w, h, logo, image)) {
							System.out.println("existe");
							return true;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("não existe");
		return false;
	}*/
	
	public static boolean callComparator(int w, int h, BufferedImage logo, BufferedImage image) {
		boolean equals = true;
		int height = h;
		
		for(int a = 0; a < logo.getHeight(); a++, height++) {
			int width = w;
			for(int l = 0; l < logo.getWidth(); l++, width++) {
				if(logo.getRGB(l, a) != image.getRGB(width, height)) { equals = false; }
			}
		}
		return equals;
	}
	
	public static void paintRed(ArrayList<Coordenadas> coordenadas, BufferedImage imagem) throws IOException {
        for(int i = 0; i < coordenadas.size(); ++i) {
            Graphics2D g2d = imagem.createGraphics();
            g2d.setColor(Color.RED);
            int xi = coordenadas.get(i).getPI().getX();
            int yi = coordenadas.get(i).getPI().getY();
            int xf = coordenadas.get(i).getPF().getX();
            int yf = coordenadas.get(i).getPF().getY();
            
            g2d.drawRect(xi, yi, xf-xi, yf-yi);
            g2d.dispose();
        }
	}
	
}
