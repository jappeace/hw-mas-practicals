package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

public class ConstantIncrease extends ANextPriceStrategy{
	private final int increaseAmount;
	public ConstantIncrease(ItemValuation valuationStrategy, int increase) {
		super(valuationStrategy);
		this.increaseAmount = increase;
	}

	@Override
	public int decide(Item item, int currentPrice) {
		int newval =  currentPrice + increaseAmount;
		if(newval < valuationStrategy.valuate(item)){
			return newval;
		}
		return DONT_WANT;
	}
}
