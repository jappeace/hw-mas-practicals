package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

/**
 * Offering multiple pricestrategies. to spice things up and not only have
 * rational agents.
 */
public interface NextPriceStrategy {
	int decide(Item item, int currentPrice);
	ItemValuation getValuation();
}
