package system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Market {
    private OrderBook orderBook = new OrderBook(this);
    private HashMap<String, Integer> shareOwnership = new HashMap<>();
    private HashMap<String, Trader> traders = new HashMap<>();

    public Market() {
        orderBook.addOrder(new Order(2.34, 500, OrderParity.SELL, ""));
    }

    synchronized Collection<Trade> trade(Order order, Trader trader) {
        String traderID = order.getTraderID();
        assert traderID.equals(trader.getID());

        traders.put(traderID, trader);

        if (order.isSell() && order.getVolume() > shareOwnership.getOrDefault(traderID, 0)) {
            return new ArrayList<>();
        }

        Collection<Trade> trades = orderBook.addOrder(order);

        int totalVolume = trades.stream().mapToInt(Trade::getVolume).sum();
        if (order.isBuy()) {
            shareOwnership.put(traderID, shareOwnership.getOrDefault(traderID, 0) + totalVolume);
        } else {
            shareOwnership.put(traderID, shareOwnership.getOrDefault(traderID, 0) - totalVolume);
        }

        for (Trade trade : trades) {
            traders.get(trade.getMakerID()).tradeTaken(trade);
        }

        printOrderBook();
        System.out.println();

        return trades;
    }

    public double sellPrice() {
        return orderBook.sellPrice();
    }

    public double buyPrice() {
        return orderBook.buyPrice();
    }

    public void cancelOrder(Order order) {
        orderBook.cancelOrder(order);
    }

    int sharesOwnedBy(String ID) {
        return shareOwnership.getOrDefault(ID, 0);
    }

    public void printOrderBook() {
        System.out.println(orderBook);
    }
}
