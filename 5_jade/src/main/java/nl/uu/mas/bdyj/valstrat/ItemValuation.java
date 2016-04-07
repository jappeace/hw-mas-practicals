package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

/**
 * The idea was to implement different ways of valuating item begind a common
 * interface, however we never came to this part.
 */
public interface ItemValuation {
	int valuate(Item item);
}
