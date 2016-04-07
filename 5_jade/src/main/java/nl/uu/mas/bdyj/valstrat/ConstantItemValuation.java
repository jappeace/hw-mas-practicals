package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

/**
 * all items are this specific number
 */
public class ConstantItemValuation implements ItemValuation{
	final private int val;
	public ConstantItemValuation(int value){
		val = value;
	}
	@Override
	public int valuate(Item item) {
		return val;
	}
}
