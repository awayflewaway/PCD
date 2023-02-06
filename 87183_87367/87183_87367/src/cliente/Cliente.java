package cliente;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import auxiliar.AnaliseImagem;
import auxiliar.ImageConverter;
import packages.Entrega;
import packages.Pedido;
import packages.Resposta;
import packages.Tipos_Rotacao;
import server.DealWithServicos;


public class Cliente {
	
	private int porto;                                                                          //porto IP da máquina do servidor.
	private String endereço_servidor;                                                           //endereço da máquina do servidor.
	private JFrame janela;
	private JPanel painelSup;
	private JPanel painelInf;
	private DefaultListModel<String> modelImagens;
	private JList listaImagens;
	private DefaultListModel modelProcura;
	private JList listaProcura;
	private File[] files;
	private File logoFile;
	private JLabel photoShow;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Socket socket;
	private ArrayList<byte[]> imagensB;
	private byte[] logoB;
	private ArrayList<Resposta> respostas;
	private ArrayList<BufferedImage> buffImages;
	private ArrayList<String> tipos_rotacao_on;
	private ArrayList<String> selectedValuesListProcura;
	
	
	public static void main(String[] args) {
		new Cliente(8080, null);
	}
	
	public Cliente(int porto, String endereço_servidor) {                                       
		this.porto = porto;
		this.endereço_servidor = endereço_servidor;
		imagensB = new ArrayList<>();
		respostas = new ArrayList<>();
		buffImages = new ArrayList<>();
		tipos_rotacao_on = new ArrayList<>();
		modelProcura = new DefaultListModel();
		connectToServer(porto, endereço_servidor);
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				
				addContentsIG();                       
				openIG();
				
			}
		};
		SwingUtilities.invokeLater(r);
//		addContentsIG();
//		openIG();
	}
	
	private void connectToServer(int porto, String endereço_servidor) {
        InetAddress endereço;
        try {
            endereço = InetAddress.getByName(endereço_servidor);
            socket = new Socket(endereço, porto);
            System.out.println("Cliente ligado ao porto:\n" + socket + "Através do IP: " + endereço);
            
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            
            output.writeObject("Client");
            output.flush();
            
            DealWithServicos dws = new DealWithServicos(this, input, output, socket);
            dws.start();

        } catch (java.net.UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
        	tryToReconnect();       
        } 
	}
	
	public void tryToReconnect() {
		System.out.println("I will try to reconnect in 10 seconds...");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        connectToServer(porto, endereço_servidor);
	}
	
	private void sendPedido() throws IOException {
		if(logoB == null) {
			receiveAlerta("noLogo");
		}
		else {
			Pedido pedido = new Pedido(imagensB, logoB, selectedValuesListProcura);
			output.writeObject(pedido);
			output.reset();
			System.out.println("Pedido enviado para o servidor");
		}
	}
	
	private void addTiposProcura(ArrayList<String> selectedValuesListProcura) {
		this.selectedValuesListProcura = selectedValuesListProcura;
		System.out.println("Tipos de Procura selecionados " + selectedValuesListProcura);
	}

	
	private void addContentsIG() {

		janela = new JFrame("LogoFinder");
		janela.setLayout(new BorderLayout());
		
		modelImagens = new DefaultListModel();
		listaImagens = new JList(modelImagens);
		janela.add(listaImagens, BorderLayout.EAST);
		
		
		listaProcura = new JList(modelProcura);
		listaProcura.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		janela.add(listaProcura, BorderLayout.WEST);
		updateRotationsList();
		
		ListProcuraListener lPL = new ListProcuraListener(this);
		listaProcura.addListSelectionListener(lPL);
		
		MouseListImagesListener mouseListener = new MouseListImagesListener(this);
		listaImagens.addMouseListener(mouseListener);
		
		painelInf = new JPanel();
		painelInf.setLayout(new BorderLayout());
		janela.add(painelInf, BorderLayout.SOUTH);
		
		JPanel painelText = new JPanel();
		painelText.setLayout(new GridLayout(2,1));
		JTextField pastaText = new JTextField("");
		painelText.add(pastaText);
		JTextField imagemText = new JTextField("");
		painelText.add(imagemText);
		
		JPanel painelButtons = new JPanel();
		painelButtons.setLayout(new GridLayout(2,1));
		
		JButton pastaButton = new JButton("Pasta");
		
		PastaListener pastaListener = new PastaListener(pastaText, this);
		pastaButton.addActionListener(pastaListener);
		painelButtons.add(pastaButton);
		
		
		JButton imageButton = new JButton("Imagem");
		painelButtons.add(imageButton);
		
		painelInf.add(painelText, BorderLayout.CENTER);
		painelInf.add(painelButtons, BorderLayout.EAST);
		
		ImageListener imageListener = new ImageListener(imagemText, this);
		imageButton.addActionListener(imageListener);
		
		
		JButton procuraButton = new JButton("Procura");
		ProcuraListener procuraListener = new ProcuraListener(this);
		procuraButton.addActionListener(procuraListener);
		painelInf.add(procuraButton, BorderLayout.SOUTH);
		
		photoShow = new JLabel();
		photoShow.setLayout(new BorderLayout());
		janela.add(new JScrollPane(photoShow), BorderLayout.CENTER);
		
		janela.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);	
		janela.pack();
	
	}
	
	private void openIG() {
		janela.setSize(920, 720);
		janela.setVisible(true);
	}
	
	public ArrayList<BufferedImage> getBuffImages() {
		return buffImages;
	}
	
	public JLabel getPhotoShow() {
		return photoShow;
	}
	
	public File[] getFiles() {
		return files;
	}
	
	public void updateIconFromBufferedImage(BufferedImage painted_image, JLabel label) {
		ImageIcon icon = new ImageIcon(painted_image);
		label.setIcon(icon);
	}
	public void updateIconFromFile(String f_name, JLabel label) {
		ImageIcon icon = new ImageIcon(f_name);
		label.setIcon(icon);
	}
	
	public void updateImagesJList() {
		updateIconFromFile("check_mark.gif", photoShow);
		modelImagens.clear();
		for(int i=0; i < buffImages.size(); i++) {
			modelImagens.addElement("image_" + i);
		}
	}
	
	public void receiveAlerta(String desigAlerta) {
		if(desigAlerta.equals("timeOut_Error")) {
			JOptionPane.showMessageDialog(null, "#timeOut_Error\nCorra a Procura de novo");
		}
		else {
			if(desigAlerta.equals("missingInformation")) {
				JOptionPane.showMessageDialog(null, "Por favor selecione uma pasta que contenha imagens");
			}
			else {
				if(desigAlerta.equals("noLogo")) {
					JOptionPane.showMessageDialog(null, "Por favor selecione um logótipo");
				}
			}
			
		}
	}
	
	public void setImagesFiles(File[] fs) throws IOException {
		this.files = fs;
	}
	
	public void setLogoFile(File logo) {
		this.logoFile = logo;
	}
	
	private void allImagesToBytes() throws IOException {
		buffImages.clear();
		imagensB.clear();
		BufferedImage temp;
		System.out.println("A converter as imagens para byte[]...");
		try {
			for(int i=0; i<files.length; i++) {
				temp = ImageConverter.getImageFromFile(files[i]); 
				buffImages.add(temp);
				imagensB.add(ImageConverter.imageToByte(temp));
			}
		} catch(NullPointerException e) {
			receiveAlerta("missingInformation");
		}
	}
	
	public void setTipos_rotacao_on(Tipos_Rotacao tr ) {
		
		tipos_rotacao_on = tr.getTipos_rotacao();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
			
				updateRotationsList();
			}
		});
//			updateRotationsList();
				
		System.out.println("Tipos de Procura disponíveis: " + tipos_rotacao_on);
		
	}
	
	private void updateRotationsList() {
	
		modelProcura.clear();
		for(int i=0; i < tipos_rotacao_on.size(); i++) {			
			modelProcura.addElement(tipos_rotacao_on.get(i));
		}
		
	}
	
	public void receiveRespostas(Entrega entrega) throws ClassNotFoundException, IOException {
		respostas.clear();
		ArrayList<Resposta> respostasl = entrega.getEntrega();
		Resposta temp;
		System.out.println("A tratar todas as Respostas");
		for(int i=0; i != buffImages.size(); i++) {
			temp = respostasl.get(i);
        	System.out.println(temp.getPontos());
        	if(temp.getPontos().isEmpty()) {
        		System.out.println("Sem logo");
        		buffImages.remove(i);
        		respostasl.remove(i);
        		i--;
        	}
        	else {
        		System.out.println("Resposta recebida:" + temp);
        		respostas.add(temp);
        	}
        	
        }
		System.out.println("Acabei o tratamento das Respostas");
		prepareFinalShow();
		
	}
	
	private void prepareFinalShow() throws IOException {
		System.out.println("A pintar o contorno...");
		for(int i=0; i < respostas.size(); i++) {
			AnaliseImagem.paintRed(respostas.get(i).getPontos(), buffImages.get(i));
		}
		updateImagesJList();
	}
	
	private class PastaListener implements ActionListener {
		
		private String currentPath;
		private String pastaName;
		private JTextField pastaText;
		private File[] listadeImagens;
		private Cliente client;
		
		public PastaListener(JTextField pastaText, Cliente client) {
			this.pastaText = pastaText;
			this.client = client;
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = chooser.showOpenDialog(null);
			
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				pastaName = chooser.getSelectedFile().getName();
				pastaText.setText(pastaName);
				currentPath = chooser.getSelectedFile().getAbsolutePath();
				listadeImagens = findImages();
				try {
					client.setImagesFiles(listadeImagens);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println(listaImagens);
			}
			
		}
		
		private File[] findImages() {
			File[] temp = new File(currentPath).listFiles(new FileFilter() {
				public boolean accept(File f) {
					if(f.getName().contains(".png") || f.getName().contains(".PNG")) {
						return true;
					}
					else { return false; }
				}
			});
			return temp;
		}
	}
	
	private class ImageListener implements ActionListener {
		
		private JTextField imagemText;
		private File imageFile;
		private Cliente c;
		
		public ImageListener(JTextField imagemText, Cliente c) {
			this.imagemText = imagemText;
			this.c = c;
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			int returnValue = chooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				imageFile = chooser.getSelectedFile();
				imagemText.setText(imageFile.getName());
				c.setLogoFile(imageFile);
				try {
					BufferedImage temp = ImageConverter.getImageFromFile(imageFile);
					logoB = ImageConverter.imageToByte(temp);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			 }
		}
	}
	
	private class ProcuraListener implements ActionListener {
		
		private Cliente c;
		
		public ProcuraListener(Cliente c) {
			this.c = c;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				updateIconFromFile("loading_image.gif", c.getPhotoShow());
				c.allImagesToBytes();
				c.sendPedido();
	
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	
	private class ListProcuraListener implements ListSelectionListener {
		
		private Cliente client;
		
		public ListProcuraListener(Cliente client) {
			this.client = client;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			 if(!e.getValueIsAdjusting()) {
				 try {
				 ArrayList<String> selectedValuesList = (ArrayList<String>) listaProcura.getSelectedValuesList();
				 client.addTiposProcura(selectedValuesList);
				 } catch(ClassCastException e1) {}
				
		     }
		}
		
		
	}

	private class MouseListImagesListener implements MouseListener {
		
		private Cliente c;

		public MouseListImagesListener(Cliente c) {
			this.c = c;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
			if (e.getClickCount() == 2) {
                
                int index = listaImagens.locationToIndex(e.getPoint());
                System.out.println("Double clicked on item: " + index);

                BufferedImage final_photo = c.getBuffImages().get(index);
    
                updateIconFromBufferedImage(final_photo, c.getPhotoShow());
                    
              
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}

}
