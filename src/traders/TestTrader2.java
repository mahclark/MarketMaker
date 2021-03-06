package traders;

import system.Market;
import system.OrderParity;
import system.Trader;

import java.util.Iterator;
import java.util.Set;

public class TestTrader2 extends Trader {

    public TestTrader2(String ID, Set<Market> marketSet) {
        super(ID, marketSet);

        Iterator<Market> marketIterator = markets.iterator();
        if (!marketIterator.hasNext()) return;

        Market market = marketIterator.next();

        System.out.println("Trader 2 sells");
        makeTrade(market, 10.0, 5, OrderParity.SELL);

//        Main.printTraderBalances();
    }
}