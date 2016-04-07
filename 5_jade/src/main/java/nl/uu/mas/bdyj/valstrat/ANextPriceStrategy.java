package nl.uu.mas.bdyj.valstrat;

/**
 * Common base for pricing strategies
 * Assuming the strategy needs a ItemValuation.
 */
public abstract class ANextPriceStrategy implements NextPriceStrategy{
	public static final int DONT_WANT = -1;
	protected final ItemValuation valuationStrategy;
	public ANextPriceStrategy(ItemValuation valuationStrategy){
		this.valuationStrategy = valuationStrategy;
	}
	@Override
	public ItemValuation getValuation(){
		return valuationStrategy;
	}
}
