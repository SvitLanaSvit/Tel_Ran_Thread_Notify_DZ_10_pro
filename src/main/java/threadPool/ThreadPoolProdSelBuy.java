package threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolProdSelBuy {
    public static void main(String[] args) {
        Market market = new Market();
        Producer producer = new Producer(market);
        Seller seller = new Seller(market);
        Consumer consumer = new Consumer(market);

        ExecutorService es = Executors.newFixedThreadPool(3);
        es.submit(producer);
        es.submit(seller);
        es.submit(consumer);

        es.shutdown();
    }
}

class Market{
    public static final String TEXT_RESET  = "\u001B[0m";
    public static final String TEXT_GREEN  = "\u001B[32m";
    public static final String TEXT_CYAN   = "\u001B[36m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    private int breadCount = 0;
    private int saleBreadCount = 0;

    public synchronized void getBread(){
        while(breadCount < 1){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        breadCount--;
        saleBreadCount++;
        System.out.println(TEXT_CYAN+"\tSeller has got a bread to sale a bread");
        System.out.println("Bread quantity of seller is: " + saleBreadCount);
        System.out.println("Bread quantity of producer is: " + breadCount + TEXT_RESET);
        if(breadCount == 0 && saleBreadCount == 5) notify();
    }
    public synchronized void putBread(){
        while (breadCount >= 5) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        breadCount++;
        System.out.println(TEXT_GREEN + "\tProducer has produced a bread");
        System.out.println("Bread quantity of producer is: " + breadCount + TEXT_RESET);
        notify();
    }

    public synchronized void buyBread(){
        while(saleBreadCount < 1){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Thread.sleep(1000);
            saleBreadCount--;
            System.out.println(TEXT_PURPLE + "\tConsumer eats a bread");
            System.out.println("Bread quantity of seller is: " + saleBreadCount + TEXT_RESET);
            notify();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Producer implements Runnable{
    Market market;

    public Producer(Market market) {
        this.market = market;
    }

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            market.putBread();
        }
    }
}

class Seller implements Runnable{
    Market market;

    public Seller(Market market) {
        this.market = market;
    }

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            market.getBread();
        }
    }
}

class Consumer implements Runnable{
    Market market;
    public Consumer(Market market) {
        this.market = market;
    }

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            market.buyBread();
        }
    }
}
