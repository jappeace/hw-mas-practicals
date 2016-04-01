package nl.uu.mas.bdyj.valstrat;

public abstract class ANextPriceStrategy implements NextPriceStrategy{
	public static final int DONT_WANT = -1;
	protected ItemValuation valuationStrategy;
	public ANextPriceStrategy(ItemValuation valuationStrategy){
		this.valuationStrategy = valuationStrategy;
	}
}
