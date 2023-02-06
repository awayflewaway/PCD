package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import packages.Entrega;
import packages.Pedido;
import packages.Resposta;
import packages.Tipos_Rotacao;



public class DealWithClient extends Thread {
	
	private Server server;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private int clientID;
	private Socket socket;
	
	
	public DealWithClient(Server server, Socket socket, ObjectInputStream in, ObjectOutputStream out, int clientID) {
		this.server = server;
		this.in = in;
		this.out = out;
		this.clientID = clientID;
		this.socket = socket;
	}
	
	
	@Override
	public void run() {
		try {
			while(true) {
				
				//Enviar tipos de rotaçao
				System.out.println("Conexão Cliente - Servidor Establecida");
				Tipos_Rotacao tipos_rotacao = server.getTipos_Rotacao();
				out.writeObject(tipos_rotacao);
				out.reset();
				
				Pedido pedido = (Pedido) in.readObject();
				System.out.println("Recebi o pedido:" + pedido);
				
				ArrayList<byte[]> imagens = pedido.getImagens();
				byte[] logo = pedido.getLogo();
				ArrayList<String> tiposProcura = pedido.getTiposProcura();
				
				server.createBarrier(clientID, imagens.size()*tiposProcura.size());
				
				for(int x=0; x < tiposProcura.size(); x++) {
					for(int i=0; i< imagens.size(); i++) {
						server.addTask(imagens.get(i), logo, tiposProcura.get(x), i, clientID);
					}
				}
				ArrayList<Resposta> pack = server.getRespostas(clientID);
				System.out.println("Obtive as respostas dos workers");
				ArrayList<Resposta> preEntrega = concatForEntrega(pack,  pedido.getTiposProcura().size());
				Entrega entrega = new Entrega(preEntrega);
				out.writeObject(entrega);
				out.reset();
			}
		} catch (ClassNotFoundException | InterruptedException e) {
				System.out.println("Fui interrompido");
		} catch (IOException e) {
			server.deleteClient(clientID);
		}
		  finally {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	}
	
	private ArrayList<Resposta> concatForEntrega(ArrayList<Resposta> respostas, int tipos) {
		System.out.println("A concatenar");
		ArrayList<Resposta> respostasF = new ArrayList<Resposta>();
		for(int i = 0; i < respostas.size(); i++) {
			Resposta temp = new Resposta(respostas.get(i).getPontos(), respostas.get(i).getImageID(), respostas.get(i).getClientID());
			for(int j = i; j < i + tipos; j++) {
				temp.concatResposta(respostas.get(j));
			}
			System.out.println("A adicionar" + temp);
			respostasF.add(temp);
			
			i +=tipos-1;
		}
		return respostasF;
		
	}

}
