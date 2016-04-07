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


public class AuctioneerAgentSecond extends AAuctioneer{
	public AuctioneerAgentSecond(int startingPrice, String auctionGood) {
		super(startingPrice, auctionGood);
	}

	int secondPrice = 0;

	protected void setup() {
		super.setup();
		addBehaviour(new WakerBehaviour(this, 5000) {
			protected void onWake() {
				closeAuction();
			}
		});
		addBehaviour(new WakerBehaviour(this, 2000) {
			protected void onWake() {
				addBehaviour(new SendPrice());
			}
		});
	}

	@Override
	protected void onReceivePrice(int price, AID aid) {
		if (price > currentPrice) {
			secondPrice = currentPrice;
			currentPrice = price;
			currentBidder = aid.getLocalName();
			currentBidderAid = aid;
		} else {
			if (price > secondPrice) {
				secondPrice = price;
			}
		}
	}

	/**
	 * Cheap way to override the price in result
	 * @return
	 */
	protected int getPrice(){
		return secondPrice;
	}

	/*
	 * behaviour for acutioneer to announce the latest higest price
	 * to every bidder which are still in the acution 
	 */
	private class SendPrice extends OneShotBehaviour {
		public void action() {
			broadcast(currentBidder + " " + String.valueOf(currentPrice));
		}
	}

	/*
	 * behaviour for closing the acution
	 */

	protected void takeDown() {
		super.takeDown();
		ACLMessage cfp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
		cfp.addReceiver(currentBidderAid);
		cfp.setContent(String.valueOf(currentPrice));
		cfp.setConversationId(auctionGood);
		cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
		this.send(cfp);
	}
}
