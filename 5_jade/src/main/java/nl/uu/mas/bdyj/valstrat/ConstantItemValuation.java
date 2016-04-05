package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

/**
 * all items are this specific number
 */
public class ConstantItemValuation implements ItemValuation{
	private int val = 0;
	public ConstantItemValuation(int value){
		val = value;
	}
	@Override
	public int valuate(Item item) {
		return val;
	}
}
