package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import packages.Task;

public class DealWithWorker extends Thread {
	
	private Server server;
	private BlockingQueue<Task> queue;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private String tipoWorker;
	private Socket socket;
	
	
	public DealWithWorker(Server server, Socket socket, ObjectInputStream in, ObjectOutputStream out, 
			BlockingQueue<Task> queue, String tipoWorker) {
		
		this.server = server;
		this.tipoWorker = tipoWorker;
		this.queue = queue;
		this.in = in;
		this.out = out;
		this.socket = socket; 
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Conexão Worker - Servidor Establecida");
			DealWithWorkerConnections dwwc = new DealWithWorkerConnections(this, in, server, socket);
		    dwwc.start();
			while(true) {
				Task tarefa = (Task) queue.poll();
				System.out.println("Apanhei uma task");
				//socket.setSoTimeout(15000);
				out.writeObject(tarefa);
				out.flush();
				System.out.println("Enviei para o meu worker");
			}
		} catch (IOException | InterruptedException e) {
			System.out.println("A cortar ligação com o servidor");
			
			if(tipoWorker.equals("Worker Simples")) {
				server.deleteWorkerSimples();
			}
			else {
				if(tipoWorker.equals("Worker de 90")) {
					server.deleteWorker90();
				}
				else {
					if(tipoWorker.equals("Worker de 180")) {
						server.deleteWorker180();
					}
					else {
						if(tipoWorker.equals("Worker de 270")) {
							server.deleteWorker270();
						}
						else {
							if(tipoWorker.equals("Worker Custom")) {
								server.deleteWorkerCustom();
							}
						}
					}
				}
			}
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
