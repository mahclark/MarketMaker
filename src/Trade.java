public class Trade {
    private final double price;
    private final int volume;

    public Trade(double price, int volume) {
        this.price = price;
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "price=" + price +
                ", volume=" + volume +
                '}';
    }
}
