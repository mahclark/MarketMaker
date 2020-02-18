package system;

public class Trade {
    private final double price;
    private final int volume;
    private final String makerID;
    private final String takerID;
    private final OrderParity makerParity;

    Trade(double price, int volume, String makerID, String takerID, OrderParity makerParity) {
        this.price = price;
        this.volume = volume;
        this.makerID = makerID;
        this.takerID = takerID;
        this.makerParity = makerParity;
    }

    public double getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    public String getMakerID() {
        return makerID;
    }

    public String getTakerID() {
        return takerID;
    }

    public boolean makerBought() {
        return makerParity == OrderParity.BUY;
    }

    public boolean makerSold() {
        return makerParity == OrderParity.SELL;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "price=" + price +
                ", volume=" + volume +
                '}';
    }
}
