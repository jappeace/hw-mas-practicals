package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;
import java.util.Random;

public class RandomValuation extends ANextPriceStrategy{
	Random rand = new Random();

	public RandomValuation(ItemValuation valuationStrategy) {
		super(valuationStrategy);
	}

	@Override
	public int decide(Item item, int currentPrice) {
		int myValuation = valuationStrategy.valuate(item);
		if(currentPrice > myValuation){
			return DONT_WANT;
		}
		int newPrice = currentPrice + rand.nextInt(3) + 1;
		if(newPrice < myValuation){
			return newPrice;
		}
		return myValuation;
	}
}
