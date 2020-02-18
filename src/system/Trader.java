package system;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class Trader {
    private static final Set<Trader> allTraders = new HashSet<>();

    private double balance;
    private final String ID;
    protected final Set<Market> markets;

    public Trader(String ID, Set<Market> markets) {
        allTraders.add(this);
        this.ID = ID;
        this.markets = markets;
    }

    void tradeTaken(Trade trade) {
        assert trade.getMakerID().equals(ID);

        double total = trade.getVolume()*trade.getPrice();
        if (trade.makerBought()) {
            balance -= total;
        } else {
            balance += total;
        }
        System.out.println(getID() + "'s new balance: " + balance);
    }

    protected void makeTrade(Market market, double price, int volume, OrderParity parity) {
        Order order = new Order(price, volume, parity, getID());
        Collection<Trade> trades = market.trade(order, this);
        double total = trades.stream().mapToDouble(trade -> trade.getPrice()*trade.getVolume()).sum();
        if (order.isBuy()) {
            balance -= total;
        } else {
            balance += total;
        }
    }

    public double getBalance() {
        return balance;
    }

    public String getID() {
        return ID;
    }

    static Set<Trader> getAllTraders() {
        return allTraders;
    }
}