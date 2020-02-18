import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Market {
    private OrderBook orderBook = new OrderBook();
    private HashMap<String, Integer> shareOwnership = new HashMap<>();

    public Collection<Trade> trade(Order order, String traderID) {
        if (order.isSell() && order.getVolume() > shareOwnership.get(traderID)) {
            return new ArrayList<>();
        }

        Collection<Trade> trades = orderBook.addOrder(order);

        int totalVolume = trades.stream().mapToInt(Trade::getVolume).sum();
        if (order.isBuy()) {
            shareOwnership.put(traderID, shareOwnership.getOrDefault(traderID, 0) + totalVolume);
        } else {
            shareOwnership.put(traderID, shareOwnership.getOrDefault(traderID, 0) - totalVolume);
        }

        return trades;
    }

    public void printOrderBook() {
        System.out.println(orderBook);
    }
}
