package traders;

import system.Market;
import system.OrderParity;
import system.Trader;

import java.util.Iterator;
import java.util.Set;

public class TestTrader extends Trader {

    public TestTrader(String ID, Set<Market> marketSet) {
        super(ID, marketSet);

        Iterator<Market> marketIterator = markets.iterator();
        if (!marketIterator.hasNext()) return;

        Market market = marketIterator.next();

        System.out.println("Trader 1 buys");
        makeTrade(market, 10.1, 15, OrderParity.BUY);
    }
}