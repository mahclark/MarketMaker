package system;

public class Order implements Comparable<Order> {
    private final double price;
    private int volume;
    private final OrderParity parity;
    private final String traderID;

    Order(double price, int volume, OrderParity parity, String traderID) {
        this.price = price;
        this.volume = volume;
        this.traderID = traderID;
        this.parity = parity;
    }

    public double getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    public OrderParity getParity() {
        return parity;
    }

    public boolean isBuy() {
        return parity == OrderParity.BUY;
    }

    public boolean isSell() {
        return parity == OrderParity.SELL;
    }

    public String getTraderID() {
        return traderID;
    }

    public void setVolume(int volume) {
        if (volume > 0) {
            this.volume = volume;
        }
    }

    @Override
    public int compareTo(Order o) {
        return Double.compare(price, o.price);
    }
}
