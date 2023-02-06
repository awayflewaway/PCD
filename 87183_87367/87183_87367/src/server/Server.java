package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import packages.Resposta;
import packages.Task;
import packages.Tipos_Rotacao;

public class Server {

	private final int PORT;
	private ServerSocket s;

	int workerSimples = 0;
	int worker90 = 0;
	int worker180 = 0;
	int worker270 = 0;
	int workerCustom = 0;

	private BlockingQueue<Task> queueSimple;
	private BlockingQueue<Task> queue180;
	private BlockingQueue<Task> queue90;
	private BlockingQueue<Task> queue270;
	private BlockingQueue<Task> queueCustom;

	private HashMap<Integer, Barrier> clients_barriers;
	private HashMap<Integer, ObjectOutputStream> clients_portfolio;
	private ArrayList<String> tipos_rotacao;

	public static void main(String[] args) {
		new Server(8080).startServing();
	}

	public Server(int port) {
		this.PORT = port;
		this.queueSimple = new BlockingQueue<Task>();
		this.queue180 = new BlockingQueue<Task>();
		this.queue90 = new BlockingQueue<Task>();
		this.queue270 = new BlockingQueue<Task>();
		this.queueCustom = new BlockingQueue<Task>();
		clients_barriers = new HashMap<Integer, Barrier>(1000);
		tipos_rotacao = new ArrayList<String>();
		clients_portfolio = new HashMap<Integer, ObjectOutputStream>();

	}

	private void startServing() {
		try {
			s = new ServerSocket(PORT);
			System.out.println("Ready to Serve");
			int clientID = 0;

			while (true) {

				Socket socket = s.accept();
				System.out.println(socket);

				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

				String str = (String) in.readObject();

				if (str.equals("Client")) {
					System.out.println(str + " " + clientID + " connected");
					DealWithClient c = new DealWithClient(this, socket, in, out, clientID);
					c.start();

					clients_portfolio.put(clientID, out);
					clientID++;
				} else {
					if (str.equals("Worker Simples")) {
						System.out.println(str + " Connected");
						DealWithWorker w = new DealWithWorker(this, socket, in, out, queueSimple, "Worker Simples");
						w.start();
						if (!tipos_rotacao.contains("Rotacao Simples")) {
							tipos_rotacao.add("Rotacao Simples");
							updateClients();
						}
						++workerSimples;
						System.out.println("Numb of workersimples: " + workerSimples);
					} else {
						if (str.equals("Worker de 90")) {
							System.out.println(str + " Connected");
							DealWithWorker w = new DealWithWorker(this, socket, in, out, queue90, "Worker de 90");
							w.start();
							if (!tipos_rotacao.contains("Rotacao de 90")) {
								tipos_rotacao.add("Rotacao de 90");
								updateClients();
							}
							++worker90;
						} else {
							if (str.equals("Worker de 180")) {
								System.out.println(str + " Connected");
								DealWithWorker w = new DealWithWorker(this, socket, in, out, queue180, "Worker de 180");
								w.start();
								if (!tipos_rotacao.contains("Rotacao de 180")) {
									tipos_rotacao.add("Rotacao de 180");
									updateClients();
								}
								++worker180;
							} else {
								if (str.equals("Worker de 270")) {
									System.out.println(str + " Connected");
									DealWithWorker w = new DealWithWorker(this, socket, in, out, queue270, "Worker de 270");
									w.start();
									if(!tipos_rotacao.contains("Rotacao de 270")) {
										tipos_rotacao.add("Rotacao de 270");
										updateClients();
									}
									++worker270;
								} else {
									if(str.equals("Worker Custom")) {
										System.out.println(str + " Connected");
										DealWithWorker w = new DealWithWorker(this, socket, in, out, queueCustom, "Worker Custom");
										w.start();
										if(!tipos_rotacao.contains("Rotacao Custom")) {
											tipos_rotacao.add("Rotacao Custom");
											updateClients();
										}
										++workerCustom;
									}
								}
							}
						}

					}
				}
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	synchronized void addTask(byte[] logo, byte[] imagem, String tipoProcura, int imageID, int clientID)
			throws InterruptedException {
		Task tarefa = new Task(logo, imagem, tipoProcura, imageID, clientID);

		if (tipoProcura.equals("Rotacao Simples")) {
			System.out.println("Adicionei uma tarefa simples");
			queueSimple.offer(tarefa);
		} else {
			if (tipoProcura.equals("Rotacao de 90")) {
				System.out.println("Adicionei uma tarefa de 90");
				queue90.offer(tarefa);
			} else {
				if (tipoProcura.equals("Rotacao de 180")) {
					System.out.println("Adicionei uma tarefa de 180");
					queue180.offer(tarefa);
				} else {
					if(tipoProcura.equals("Rotacao de 270")) {
						System.out.println("Adicionei uma tarefa de 270");
						queue270.offer(tarefa);
					} else {
						if(tipoProcura.equals("Rotacao Custom")) {
							System.out.println("Adicionei uma tarefa custom");
							queueCustom.offer(tarefa);
						}
					}
				}
			}
		}
	}

	synchronized void createBarrier(int clientID, int numImages) {
		clients_barriers.put(clientID, new Barrier(numImages));
		System.out.println("Barreira criada para o cliente: " + clientID);
	}

	synchronized void addResponse(int clienteID, Resposta response) {
		try {

			clients_barriers.get(clienteID).put(response);
			System.out.println(clients_barriers.get(clienteID));
			

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Resposta> getRespostas(int clienteID) {
		try {
			return clients_barriers.get(clienteID).getBarrier();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Tipos_Rotacao getTipos_Rotacao() {
		Tipos_Rotacao tr = new Tipos_Rotacao(tipos_rotacao);
		return tr;
	}

	public int countSimples() {
		return workerSimples;
	}

	public int count90() {
		return worker90;
	}

	public int count180() {
		return worker180;
	}
	
	public int countCustom() {
		return workerCustom;
	}
	
	public void timeOut_Error(int clientID) {
		ObjectOutputStream out = clients_portfolio.get(clientID);
		try {
			out.writeObject("timeOut_Error");
			out.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	protected void updateClients() {
		Tipos_Rotacao tr = new Tipos_Rotacao(tipos_rotacao);
		for (Map.Entry<Integer, ObjectOutputStream> entry : clients_portfolio.entrySet()) {
		    System.out.println(entry.getKey() + " = " + entry.getValue());
		    ObjectOutputStream out = entry.getValue();
		    try {
		    	System.out.println("A notificar os clientes do seguinte Tipo de Procura: " + tr.getTipos_rotacao());
				out.writeObject(tr);
				out.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	protected void deleteClient(int clientID) {
		clients_portfolio.remove(clientID);
	}

	protected void deleteWorkerSimples() {
		--workerSimples;
		System.out.println("Number of WorkerSimples: " + workerSimples);
		if (workerSimples == 0) {
			for (int i = 0; i < tipos_rotacao.size(); i++) {
				if (tipos_rotacao.get(i).equals("Rotacao Simples")) {
					tipos_rotacao.remove(i);
					updateClients();
					break;
				}
			}
		}
	}

	protected void deleteWorker90() {
		--worker90;
		System.out.println("Number of Worker 90: " + worker90);
		if (worker90 == 0) {
			for (int i = 0; i < tipos_rotacao.size(); i++) {
				if (tipos_rotacao.get(i).equals("Rotacao de 90")) {
					tipos_rotacao.remove(i);
					updateClients();
					break;
				}
			}
		}
	}

	protected void deleteWorker180() {
		--worker180;
		System.out.println("Number of Worker 180: " + worker180);
		if (worker180 == 0) {
			for (int i = 0; i < tipos_rotacao.size(); i++) {
				if (tipos_rotacao.get(i).equals("Rotacao de 180")) {
					tipos_rotacao.remove(i);
					updateClients();
					break;
				}
			}
		}
	}
	
	protected void deleteWorker270() {
		--worker270;
		System.out.println("Number of Worker 270: " + worker270);
		if (worker270 == 0) {
			for (int i = 0; i < tipos_rotacao.size(); i++) {
				if (tipos_rotacao.get(i).equals("Rotacao de 270")) {
					tipos_rotacao.remove(i);
					updateClients();
					break;
				}
			}
		}
	}

	protected void deleteWorkerCustom() {
		--workerCustom;
		System.out.println("Number of Worker Custom: " + workerCustom);
		if (workerCustom == 0) {
			for(int i = 0; i < tipos_rotacao.size(); i++) {
				if (tipos_rotacao.get(i).equals("Rotacao Custom")) {
					tipos_rotacao.remove(i);
					updateClients();
					break;
				}
			}
		}
	}

}
