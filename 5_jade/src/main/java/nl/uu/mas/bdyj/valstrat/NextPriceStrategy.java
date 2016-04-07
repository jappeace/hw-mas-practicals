package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

public interface NextPriceStrategy {
	int decide(Item item, int currentPrice);
	ItemValuation getValuation();
}
