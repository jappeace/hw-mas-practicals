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


public class AuctioneerAgentEnglish extends AAuctioneer{
	public AuctioneerAgentEnglish(int startingPrice, String auctionGood) {
		super(startingPrice, auctionGood);
	}

	private boolean getNewPrice = true;

	protected void setup() {
		super.setup();
		System.out.println("Hello! AuctioneerAgent " + getAID().getLocalName() + " is ready.");
		final Agent a = this;

		addBehaviour(new SendPrice(this, 1000));


		addBehaviour(new TickerBehaviour(this, 1000) {
			protected void onTick() {
				timer = timer + 1;
				if(timer > 5){
					closeAuction();
				}
				System.out.println("timer = " + String.valueOf(timer));
			}
		});
	}
	private int timer = 0;

	@Override
	protected void onReceivePrice(int price, AID bidder) {
		if (price > currentPrice) {
			timer = 0;
			currentPrice = price;
			currentBidder = bidder.getLocalName();
			currentBidderAid = bidder;
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("The auctioneer announces that the latest highest price is " + price + " from " + currentBidder);
			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("");
			getNewPrice = true;
		}
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

			if (getNewPrice) {
				getNewPrice = false;
				// Update the list of Bidder agents which still in
				broadcast(currentBidder + " " + currentPrice);
			}
			else {
				block(1);
			}

		}
	}
}
