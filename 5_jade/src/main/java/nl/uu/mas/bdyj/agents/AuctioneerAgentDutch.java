package nl.uu.mas.bdyj.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class AuctioneerAgentDutch extends AAuctioneer{
	public AuctioneerAgentDutch(int startingPrice, String auctionGood,int minIncrement) {
		super(startingPrice, auctionGood);
		this.minIncrement = minIncrement;
	}

	int minIncrement;

	protected void setup() {
		super.setup();
		addBehaviour(new SendPrice(this, 10));
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("AuctioneerAgent " + getAID().getLocalName() + " terminating.");
	}

	@Override
	protected void onReceivePrice(int price, AID bidder) {
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("The auctioneer announces that the bid price is " + price);
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("");
		currentBidder = bidder.getLocalName();
		currentBidderAid = bidder;
		closeAuction();
	}

	/*
	 * behaviour for acutioneer to announce the latest higest price
	 * to every bidder which are still in the acution 
	 */
	private class SendPrice extends TickerBehaviour {
		SendPrice(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			// clear the timer;
			currentPrice = currentPrice - minIncrement;
			log.info("Auctioneer: the bid price is " + currentPrice);
			broadcast(currentPrice + "");

		}
	}
}
