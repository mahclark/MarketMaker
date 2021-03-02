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

    OrderBookEntry(double price, boolean earliest, OrderParity parity) {
        order = new Order(price, 0, parity, "dummy");
        if (earliest) {
            timestamp = new Timestamp(0);
        } else {
            timestamp = new Timestamp(System.nanoTime());
        }
    }

    Order getOrder() {
        return order;
    }

    @Override
    public int compareTo(OrderBookEntry o) {
        int orderComparison = order.compareTo(o.order);
        if (order.isBuy() && o.getOrder().isBuy()) {
            orderComparison *= -1;
        }

        if (orderComparison == 0) {
            return timestamp.compareTo(o.timestamp);
        }
        return orderComparison;
    }
}

public class OrderBook {

    private TreeSet<OrderBookEntry> buyOrders = new TreeSet<>();
    private TreeSet<OrderBookEntry> sellOrders = new TreeSet<>();
    private HashMap<Order, OrderBookEntry> entries = new HashMap<>();
    private HashMap<Double, Integer> buyVolumeByPrice = new HashMap<>();
    private HashMap<Double, Integer> sellVolumeByPrice = new HashMap<>();
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
                HashMap<Double, Integer> volumes = makeOrder.isBuy() ? buyVolumeByPrice : sellVolumeByPrice;
                volumes.put(makeOrder.getPrice(), volumes.getOrDefault(makeOrder.getPrice(), 0) - tradeVolume);
            }
        }

        for (OrderBookEntry removeEntry : toRemove) {
            Order removeOrder = removeEntry.getOrder();

            makeOrders.remove(removeEntry);
            entries.remove(removeOrder);

            HashMap<Double, Integer> volumes = removeOrder.isBuy() ? buyVolumeByPrice : sellVolumeByPrice;
            volumes.put(removeOrder.getPrice(), volumes.getOrDefault(removeOrder.getPrice(), 0) - removeOrder.getVolume());
        }

        assert takeVolume >= 0;
        takeOrder.setVolume(takeVolume);
        if (takeVolume > 0) {
            takeOrders.add(orderBookEntry);
            entries.put(takeOrder, orderBookEntry);

            HashMap<Double, Integer> volumes = takeOrder.isBuy() ? buyVolumeByPrice : sellVolumeByPrice;
            volumes.put(takePrice, volumes.getOrDefault(takePrice, 0) + takeVolume);
        }

        return trades;
    }

    void cancelOrder(Order order) {
        TreeSet<OrderBookEntry> orders = order.isBuy() ? buyOrders : sellOrders;
        OrderBookEntry entry = entries.get(order);
        if (entry != null) {
            orders.remove(entry);
        }
    }

    int getBuyVolume(double price) {
        return buyVolumeByPrice.getOrDefault(price, 0);
    }

    int getSellVolume(double price) {
        return sellVolumeByPrice.getOrDefault(price, 0);
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
        Order order = new Order(52, 15, OrderParity.SELL, "1");

        OrderBook orderBook = new OrderBook(null);

        orderBook.addOrder(new Order(49, 10, OrderParity.BUY, "2"));
        orderBook.addOrder(new Order(50, 15, OrderParity.BUY, "1"));
        orderBook.addOrder(new Order(50, 10, OrderParity.BUY, "2"));
        orderBook.addOrder(new Order(55, 15, OrderParity.SELL, "1"));
        orderBook.addOrder(order);
        System.out.println(orderBook.addOrder(new Order(52
                , 18, OrderParity.SELL, "2")));
//        orderBook.cancelOrder(order);

        System.out.println(orderBook);
        System.out.println(orderBook.getBuyVolume(50));
        System.out.println(orderBook.getSellVolume(52));

//        System.out.println();
//        System.out.println(orderBook.addOrder(new Order(10.8, 50, OrderParity.BUY, "")));
//        System.out.println(orderBook);
//        System.out.println();
//        System.out.println(orderBook.addOrder(new Order(10.3, 60, OrderParity.BUY, "")));
//        System.out.println(orderBook);
    }
}