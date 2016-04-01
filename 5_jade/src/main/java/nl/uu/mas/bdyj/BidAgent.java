package nl.uu.mas.bdyj;

import jade.core.Agent;
import nl.uu.mas.bdyj.valstrat.Valuation;

public class BidAgent extends Agent{
	private int money;
	private Valuation valuationStrategy;
	public BidAgent(int money, Valuation valuationStrategy){
		this.money = money;
		this.valuationStrategy = valuationStrategy;
	}
}
