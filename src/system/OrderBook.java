package system;

import java.sql.Timestamp;
import java.util.*;


class OrderBookEntry implements Comparable<OrderBookEntry> {
    private final Order order;
    private final Timestamp timestamp;

    OrderBookEntry(Order order) {
        this.order = order;
        timestamp =  new Timestamp(System.nanoTime());
    }

    Order getOrder() {
        return order;
    }

    @Override
    public int compareTo(OrderBookEntry o) {
        int orderComparison = order.compareTo(o.order);
        if (orderComparison == 0) {
            return timestamp.compareTo(o.timestamp);
        }
        return orderComparison;
    }
}

public class OrderBook {

    private TreeSet<OrderBookEntry> buyOrders = new TreeSet<>(Comparator.reverseOrder());
    private TreeSet<OrderBookEntry> sellOrders = new TreeSet<>();
    private final Market market;

    OrderBook(Market market) {
        this.market = market;
//        addOrder(new Order(10.4, 10, OrderParity.SELL, ""));
//        addOrder(new Order(10.3, 40, OrderParity.SELL, ""));
//        addOrder(new Order(10.3, 20, OrderParity.SELL, ""));
//        addOrder(new Order(10.0, 5, OrderParity.SELL, ""));
//        addOrder(new Order(9.9, 10, OrderParity.BUY, ""));
//        addOrder(new Order(9.6, 15, OrderParity.BUY, ""));
    }

    Collection<Trade> addOrder(Order takeOrder) {
        OrderBookEntry orderBookEntry = new OrderBookEntry(takeOrder);

        double takePrice = takeOrder.getPrice();
        int takeVolume = takeOrder.getVolume();

        if (takePrice <= 0 || takeVolume <= 0) {
            return new ArrayList<>();
        }

        TreeSet<OrderBookEntry> makeOrders = takeOrder.isSell() ? buyOrders : sellOrders;
        TreeSet<OrderBookEntry> takeOrders = takeOrder.isSell() ? sellOrders : buyOrders;

        ArrayList<Trade> trades = new ArrayList<>();
        HashSet<OrderBookEntry> toRemove = new HashSet<>();

        for (OrderBookEntry makeOrderEntry : makeOrders) {
            Order makeOrder = makeOrderEntry.getOrder();
            double makePrice = makeOrder.getPrice();

            int makeVolume = makeOrder.getVolume();
            if ((takeOrder.isSell() && makePrice < takePrice) || (takeOrder.isBuy() && makePrice > takePrice) || takeVolume == 0) {
                break;
            }
            int tradeVolume = Math.min(takeVolume, makeVolume);
            takeVolume -= tradeVolume;
            trades.add(new Trade(makePrice, tradeVolume, makeOrder.getTraderID(), takeOrder.getTraderID(), makeOrder, market));

            if (makeVolume - tradeVolume == 0) {
                toRemove.add(makeOrderEntry);
            } else {
                makeOrder.setVolume(makeVolume - tradeVolume);
            }
        }

        for (OrderBookEntry removeOrder : toRemove) {
            makeOrders.remove(removeOrder);
        }

        assert takeVolume >= 0;
        takeOrder.setVolume(takeVolume);
        if (takeVolume > 0) {
            takeOrders.add(orderBookEntry);
        }

        return trades;
    }

    void cancelOrder(Order order) {
        TreeSet<OrderBookEntry> orders = order.isBuy() ? buyOrders : sellOrders;
        orders.removeIf(entry -> entry.getOrder() == order);
    }

    double sellPrice() {
        return sellOrders.first().getOrder().getPrice();
    }

    double buyPrice() {
        return buyOrders.first().getOrder().getPrice();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(String.format("%20s | %-20s\n", "BUY Orders", "SELL Orders"));
        str.append(" - - - - - - - - - - + - - - - - - - - - - ");

        Iterator<OrderBookEntry> buyIter = buyOrders.iterator();
        Iterator<OrderBookEntry> sellIter = sellOrders.iterator();
        while (buyIter.hasNext() || sellIter.hasNext()) {
            String buyText = "", sellText = "";
            if (buyIter.hasNext()) {
                Order order = buyIter.next().getOrder();
                buyText = order.getPrice() + "  : " + String.format("%3s", order.getVolume());
            }
            if (sellIter.hasNext()) {
                Order order = sellIter.next().getOrder();
                sellText = order.getPrice() + "  : " + String.format("%3s", order.getVolume());
            }
            str.append("\n").append(String.format("%20s | %-20s", buyText, sellText));
        }
        return str.toString();
    }

    public static void main(String[] args) {
        OrderBook orderBook = new OrderBook(null);
        System.out.println(orderBook);
//        System.out.println();
//        System.out.println(orderBook.addOrder(new Order(10.8, 50, OrderParity.BUY, "")));
//        System.out.println(orderBook);
//        System.out.println();
//        System.out.println(orderBook.addOrder(new Order(10.3, 60, OrderParity.BUY, "")));
//        System.out.println(orderBook);
    }
}