package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;

import java.util.Random;

public class RandomScaledValuation extends ANextPriceStrategy{
	Random rand = new Random();

	public RandomScaledValuation(ItemValuation valuationStrategy) {
		super(valuationStrategy);
	}

	@Override
	public int decide(Item item, int currentPrice) {
		int myValuation = valuationStrategy.valuate(item);
		if(currentPrice > myValuation){
			return DONT_WANT;
		}
		int newPrice = currentPrice + (int)((rand.nextInt(3) + 1)*(0.1*currentPrice));
		if(newPrice < myValuation){
			return newPrice;
		}
		return myValuation;
	}
}
