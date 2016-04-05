package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

import java.util.Random;

public class ConstantIncrease implements NextPriceStrategy{
	private int increase  = 0;
	public ConstantIncrease(int increase) {
		this.increase = increase;
	}

	@Override
	public int decide(Item item, int currentPrice) {
		return increase;
	}
}
