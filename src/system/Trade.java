package system;

public class Trade {
    private final double price;
    private final int volume;
    private final String makerID;
    private final String takerID;
    private final Order makeOrder;
    private final Market market;

    Trade(double price, int volume, String makerID, String takerID, Order makeOrder, Market market) {
        this.price = price;
        this.volume = volume;
        this.makerID = makerID;
        this.takerID = takerID;
        this.makeOrder = makeOrder;
        this.market = market;
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
        return makeOrder.isBuy();
    }

    public boolean makerSold() {
        return makeOrder.isSell();
    }

    public Order getMakeOrder() {
        return makeOrder;
    }

    public Market getMarket() {
        return market;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "price=" + price +
                ", volume=" + volume +
                '}';
    }
}
