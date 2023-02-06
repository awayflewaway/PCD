package server;

import java.util.ArrayList;

import packages.Resposta;

public class Barrier {
	
	private int numImages;
	private ArrayList<Resposta> list;
	private int it = 0;
	
	public Barrier(int numImages) {
		this.numImages = numImages;
		this.list = new ArrayList<Resposta>(numImages);
	}
	
	public ArrayList<Resposta> getfff() {
		return list;
	}
	
	public synchronized void put(Resposta resposta) throws InterruptedException {

		while(it == numImages) {
			wait();
		}
			list.add(resposta);
			it++;
			
			notifyAll();
	}
	
	public void clear() {
	
		list.clear();
		it=0;

	}
	
	@Override
	public String toString() {
		return "Barrier [list=" + list + ", it=" + it + "]";
	}

	public synchronized ArrayList<Resposta> getBarrier() throws InterruptedException {
		while(it < numImages) {
			wait();
		}
			organizeElements();
			ArrayList<Resposta> temp = createCopy();
			clear();
			System.out.println("A devolver a barreira: " + temp);
			return temp;
	}
	
	
	private void organizeElements() {
		
		Resposta temp;
		boolean sorted = false;
		
		while(!sorted) {
			sorted = true;
			for(int i=0; i < numImages -1; i++) {
				if(list.get(i).getImageID() > list.get(i+1).getImageID()) {
					temp = list.get(i);
					list.set(i, list.get(i+1));
					list.set(i+1, temp);
					sorted = false;
				}
			}
		}
	}
	
	
	private ArrayList<Resposta> createCopy () {
		  ArrayList<Resposta> copy = new ArrayList<Resposta>();
		  for (Resposta s: list) {
		    copy.add(s);
		  }
		  return copy;
		}
	
	
	
	
	
	
	
	

}
