package system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Market {
    private Main main;

    private OrderBook orderBook = new OrderBook();
    private HashMap<String, Integer> shareOwnership = new HashMap<>();
    private HashMap<String, Trader> traders = new HashMap<>();

    Market(Main main) {
        this.main = main;
    }

    synchronized Collection<Trade> trade(Order order, Trader trader) {
        String traderID = order.getTraderID();
        assert traderID.equals(trader.getID());

        traders.put(traderID, trader);

        if (order.isSell() && order.getVolume() > shareOwnership.getOrDefault(traderID, 0)) {
            //return new ArrayList<>();
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

    public void printOrderBook() {
        System.out.println(orderBook);
    }
}
