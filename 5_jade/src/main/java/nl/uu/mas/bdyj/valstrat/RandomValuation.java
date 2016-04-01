package nl.uu.mas.bdyj.valstrat;

import nl.uu.mas.bdyj.Item;
import java.util.Random;

public class RandomValuation implements Valuation{
	Random rand = new Random();
	@Override
	public int valuate(Item item) {
		return rand.nextInt();
	}
}
