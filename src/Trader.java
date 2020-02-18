import java.util.Collection;
import java.util.HashSet;

public abstract class Trader {
    private double balance;
    private final String ID;
    final HashSet<Market> markets = new HashSet<>();

    public Trader(String ID) {
        this.ID = ID;
    }

    public void makeTrade(Market market, Order order) {
        Collection<Trade> trades = market.trade(order, ID);
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
}

class TestTrader extends Trader {

    public TestTrader(String ID) {
        super(ID);
        Market market = new Market();
        markets.add(market);
        market.printOrderBook();
        makeTrade(market, new Order(10.1, 15, OrderParity.BUY, ID));
        market.printOrderBook();
    }

    public static void main(String[] args) {
        new TestTrader("I am Max");
    }
}