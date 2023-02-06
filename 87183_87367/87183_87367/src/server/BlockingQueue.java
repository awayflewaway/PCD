package server;

import java.util.*;

public class BlockingQueue<Task> {

    private Queue<Task> queue;

    public BlockingQueue() {
        this.queue = new LinkedList<Task>();
    }

//    public BlockingQueue(int capacity) {
//        if(capacity > 0) {
//        this.queue = new LinkedList<Task>();
//        this.maxCapacity = capacity;
//        this.limited = true;
//        }
//        else { throw new IllegalArgumentException("Erro"); }
//    }


    public synchronized void offer(Task task) throws InterruptedException {
    	queue.add(task);
        notifyAll();
    }


    public synchronized Task poll() throws InterruptedException {
        while(queue.isEmpty()) {
            wait();
        }
        Task ret = queue.poll();
        notifyAll();
        return ret;
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
    	
        queue.clear();
        
   
    }

}
