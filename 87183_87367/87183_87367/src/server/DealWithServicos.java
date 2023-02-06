package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import cliente.Cliente;
import packages.Entrega;
import packages.Pedido;
import packages.Tipos_Rotacao;

public class DealWithServicos extends Thread {
	
	private Cliente client;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket ;
	
	
	public DealWithServicos(Cliente client, ObjectInputStream in, ObjectOutputStream out, Socket socket) {
		this.client = client;
		this.in = in;
		this.out = out;
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
		try {
			while(true) {
				System.out.println("À espera de um serviço");
				Object obj = in.readObject();
				if(obj instanceof Tipos_Rotacao) {
					
	            	Tipos_Rotacao tipos_rotacao = (Tipos_Rotacao) obj;
	            	System.out.println("Recebi uma atualização de procura: " + tipos_rotacao.getTipos_rotacao());
	            	client.setTipos_rotacao_on(tipos_rotacao);
	            }
				else {
					if(obj instanceof Entrega) {
						System.out.println("Recebi uma Entrega!");
						Entrega entrega = (Entrega) obj;
				
						System.out.println(entrega.getEntrega());
						client.receiveRespostas(entrega);
		            }
					else {
						if(obj instanceof String) {
							System.out.println("Recebi um Alerta");
							String alerta = (String) obj;
							client.receiveAlerta(alerta);
						}
						else {
						continue;
						}
					}
				}
		
			}
		} catch (ClassNotFoundException | IOException e) {
			client.tryToReconnect();
		}
		finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
