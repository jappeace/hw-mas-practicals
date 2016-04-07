package nl.uu.mas.bdyj.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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

	// agent initializations
	// the auctioneer will send the latest price to every bidder agents when getNewPrice is asserted
	// set the initial value of getNewPrice to true to make the auctioneer agent send out the starting price
	// getNewPrice will be set to false after sending the latest price and 
	// be set to true after receiving a new price which is higher than current price from any bidder agents
	private boolean existBid = false;
	String currentBidder;
	AID currentBidderAid;
	boolean closeAuction = false;
	int minIncrement;

	protected void setup() {
		System.out.println("Hello! AuctioneerAgent " + getAID().getLocalName() + " is ready.");
		final Agent a = this;

		addBehaviour(new SendPrice(this, 10));

		addBehaviour(new WakerBehaviour(this, 2000) {
			protected void handleElapsedTimeout() {

				addBehaviour(new ReceiveBid());
				addBehaviour(new CloseAuction());
			}
		});

	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("AuctioneerAgent " + getAID().getLocalName() + " terminating.");
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

			if (!existBid) {
				// clear the timer;
				currentPrice = currentPrice - minIncrement;
				broadcast(currentPrice + "");
			} else {
				block();
			}

		}
	}

	/*
	 * behaviour for auctioneer to receive bids from each bidder 
	 */
	private class ReceiveBid extends CyclicBehaviour {
		public void action() {

			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchConversationId(auctionGood));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null && !existBid) {
				// PROPOSE Message received. Process it
				// receivePrice is the price received from any bidder agents
				int receivedPrice = Integer.valueOf(msg.getContent());
				String sender = msg.getSender().getLocalName();

				// If the received price is higher than last highest price, then update it
				// as the latest highest price
				if (receivedPrice == currentPrice) {
					currentBidder = sender;
					currentBidderAid = msg.getSender();
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("The auctioneer announces that the bid price is " + msg.getContent());
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("");
					existBid = true;
				}
			} else {
				block();
			}
		}
	}
	
	/*
	 * behaviour for closing the acution
	 */

	private class CloseAuction extends CyclicBehaviour {
		public void action() {
			// if there is no new bid receive in 5s, then award the acution good to the
			// bidder with latest highest price.
			if (existBid) {
				closeAuction = true;
				System.out.println(" ");
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println("The auction is closed, the " + auctionGood + " was sold to " + currentBidder + " with the price " + String.valueOf(currentPrice));
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println(" ");



				broadcast(currentPrice+"");

				myAgent.doDelete();
			}
		}
	}

}
