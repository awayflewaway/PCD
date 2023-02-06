package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ConcurrentModificationException;

import packages.Resposta;

public class DealWithWorkerConnections extends Thread {
	
	private ObjectInputStream in;
	private DealWithWorker dww;
	private Server server;
	private Socket socket;
	private int RespostaID;

	public DealWithWorkerConnections(DealWithWorker dww, ObjectInputStream in, Server server, Socket socket) {
		this.in = in;
		this.dww = dww;
		this.server = server;
		this.socket = socket;
	}
	
	@Override
	public void run() {
			
		try {
			//socket.setSoTimeout(30000);
			while (true) {
				Resposta temp = (Resposta) in.readObject();
				RespostaID = temp.getClientID();
				int clientID = temp.getClientID();
				server.addResponse(clientID, temp);
			}
		} catch (SocketTimeoutException e) {
			System.out.println("Trabalhador a demorar a responder");
			server.timeOut_Error(RespostaID);

		} catch (IOException e) {
			dww.interrupt();
		} catch (ClassNotFoundException | ConcurrentModificationException e) {
	  }

	}

}