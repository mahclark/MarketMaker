package system;

import traders.TestTrader;
import traders.TestTrader2;

import java.util.HashSet;
import java.util.Set;

public class Main {

    private Set<Market> markets = new HashSet<>();

    @FunctionalInterface
    private interface Procedure {
        void run();
    }

    private Main() {
        markets.add(new Market());

        newThread(() -> new TestTrader("TestTrader1", new HashSet<>(markets)));
        newThread(() -> new TestTrader2("TestTrader2", new HashSet<>(markets)));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            //Ignore
        }

        printTraderBalances();
    }

    private void newThread(Procedure procedure) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                procedure.run();
            }
        }.start();
    }

    public static void printTraderBalances() {
        System.out.println(String.format("%15s", "Trader ID") + " | Balance");
        System.out.println("- - - - - - - - + - - - - - -");
        for (Trader trader : Trader.getAllTraders()) {
            System.out.println(String.format("%15s", trader.getID()) + " : " + trader.getBalance());
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
