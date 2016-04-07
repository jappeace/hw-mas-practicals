package nl.uu.mas.bdyj.agents;

import jade.core.Agent;

//import java.util.Random;
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
import nl.uu.mas.bdyj.valstrat.ItemValuation;

public class BidderAgentSecond extends ABidderAgent{
	public BidderAgentSecond(ItemValuation valuation, Item auctionGood){
		// a weird call, because the secondprice doesn't
		// need pricing stragy, its honest about its price.
		// It would've been better maybe to create a honest NextPriceStrategy that
		// just returns the valuation.
		super(null,auctionGood);
		this.valuation = valuation;
	}
	private ItemValuation valuation;
	protected void setup() {
		super.setup();
	}

	@Override
	void onMessage(String msg, ACLMessage reply) {
		// CFP Message received. Process it
		// currentPrice is the latest higest price from all bidder agents
		String msgSplit[] = msg.split(" ");
		// msgSplit[0] is currentBidder and msgSplit[1] is currentPrice
		String currentBidder = msgSplit[0];
		int currentPrice = Integer.valueOf(msgSplit[1]);
		int newval = valuation.valuate(auctionGood);
		// if the latest highest price is larger than the valuation of each bidder agent
		// then quit the auction and terminate the bidder agent
		if (newval  <= ANextPriceStrategy.DONT_WANT) {
			System.out.println(getAID().getLocalName()+" quit the auction!");
			System.out.println("");
			this.doDelete();
			return;
		}

		// if the latet highest price if less than the valuation
		// and if not given by the bidder agent self
		// then give a new bid price which is higher than the latest higest price and less than valuation
		if (!currentBidder.equals(getAID().getLocalName())) {
			reply.setPerformative(ACLMessage.PROPOSE);
			reply.setConversationId(auctionGood.name);
			reply.setContent(newval+"");
			this.send(reply);
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println(getAID().getLocalName() + " cries a higher price: " + newval);
			System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			System.out.println("");
		}
	}
}
