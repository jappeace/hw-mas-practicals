package nl.uu.mas.bdyj.agents;

import jade.core.Agent;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import nl.uu.mas.bdyj.Item;
import nl.uu.mas.bdyj.valstrat.ANextPriceStrategy;
import nl.uu.mas.bdyj.valstrat.NextPriceStrategy;


public class BidderAgentDutch extends ABidderAgent{
	public BidderAgentDutch(NextPriceStrategy valuation, Item auctionGood){
		super(valuation,auctionGood);
	}
	protected void setup() {
		super.setup();
	}

	@Override
	void onMessage(String msg, ACLMessage reply) {
		// CFP Message received. Get current price from it
		int currentPrice = Integer.valueOf(msg);
		int newval = valuation.decide(auctionGood, currentPrice);

		// if the current price is acceptable, then send a bid.
		if (newval > ANextPriceStrategy.DONT_WANT) {
			cry(newval, reply);
		}
	}
}
