package system;

import java.util.*;

public abstract class Trader {
    private static final Set<Trader> allTraders = new HashSet<>();

    private double balance = 1000;
    private final String ID;
    protected final Set<Market> markets;
    private HashMap<Market, TreeSet<Order>> activeOrders = new HashMap<>();

    public Trader(String ID, Set<Market> markets) {
        allTraders.add(this);
        this.ID = ID;
        this.markets = markets;
    }

    synchronized void tradeTaken(Trade trade) {
        assert trade.getMakerID().equals(ID);

        double total = trade.getVolume()*trade.getPrice();
        if (trade.makerBought()) {
            balance -= total;
        } else {
            balance += total;
        }
        System.out.println(getID() + "'s new balance: " + balance);

        if (trade.getMakeOrder().getVolume() == 0) {
            activeOrders.get(trade.getMarket()).remove(trade.getMakeOrder());
        }
    }

    protected void makeTrade(Market market, double price, int volume, OrderParity parity) {
        synchronized (ID) {
            Order order = new Order(price, volume, parity, getID());
            Collection<Trade> trades = market.trade(order, this);
            double total = trades.stream().mapToDouble(trade -> trade.getPrice() * trade.getVolume()).sum();
            if (order.isBuy()) {
                balance -= total;
            } else {
                balance += total;
            }

            assert volume - trades.stream().mapToInt(Trade::getVolume).sum() == order.getVolume();
            if (order.getVolume() > 0) {
                if (!activeOrders.containsKey(market)) {
                    activeOrders.put(market, new TreeSet<>());
                }
                activeOrders.get(market).add(order);
            }
        }
    }

    protected TreeSet<Order> getActiveOrders(Market market) {
        return activeOrders.getOrDefault(market, new TreeSet<>());
    }

    protected int numberOfShares(Market market) {
        return market.sharesOwnedBy(getID());
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