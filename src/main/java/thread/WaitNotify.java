package thread;

public class WaitNotify {
    public static void main(String[] args) {
        MarketDZ market = new MarketDZ();
        ProducerDZ producer = new ProducerDZ(market);
        SellerDZ seller = new SellerDZ(market);
        ConsumerDZ consumer = new ConsumerDZ(market);

        Thread thread1 = new Thread(producer);
        Thread thread2 = new Thread(seller);
        Thread thread3 = new Thread(consumer);
        thread1.start();
        thread2.start();
        thread3.start();
    }
}

class MarketDZ{
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

class ProducerDZ implements Runnable{
    MarketDZ market;

    public ProducerDZ(MarketDZ market) {
        this.market = market;
    }

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            market.putBread();
        }
    }
}

class SellerDZ implements Runnable{
    MarketDZ market;

    public SellerDZ(MarketDZ market) {
        this.market = market;
    }

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            market.getBread();
        }
    }
}

class ConsumerDZ implements Runnable{
    MarketDZ market;
    public ConsumerDZ(MarketDZ market) {
        this.market = market;
    }

    @Override
    public void run() {
        for (int i = 0; i < 15; i++) {
            market.buyBread();
        }
    }
}