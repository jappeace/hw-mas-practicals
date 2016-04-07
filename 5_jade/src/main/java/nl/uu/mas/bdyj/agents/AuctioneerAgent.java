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


public class AuctioneerAgent extends AAuctioneer{
	public AuctioneerAgent(int startingPrice, String auctionGood) {
		super(startingPrice, auctionGood);
	}

	// agent initializations
	// the auctioneer will send the latest price to every bidder agents when getNewPrice is asserted
	// set the initial value of getNewPrice to true to make the auctioneer agent send out the starting price
	// getNewPrice will be set to false after sending the latest price and 
	// be set to true after receiving a new price which is higher than current price from any bidder agents
	private boolean getNewPrice = true;
	private boolean conflictDeal = true;
	String currentBidder;
	AID currentBidderAid;
	int timer = 0;
	boolean closeAuction = false;

	protected void setup() {
		System.out.println("Hello! AuctioneerAgent " + getAID().getLocalName() + " is ready.");
		final Agent a = this;

		addBehaviour(new SendPrice(this, 1000));

		addBehaviour(new ReceiveBid());
		addBehaviour(new CloseAuction());

		addBehaviour(new TickerBehaviour(this, 1000) {
			protected void onTick() {
				timer = timer + 1;
				System.out.println("timer = " + String.valueOf(timer));
			}
		});
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
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				broadcast(currentBidder + " " + currentPrice);
			}
			else {
				block(1);
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
			if (msg != null) {
				// receive a bid, clear the timer;
				timer = 0;
				// PROPOSE Message received. Process it
				// receivePrice is the price received from any bidder agents
				int receivedPrice = Integer.valueOf(msg.getContent());
				String sender = msg.getSender().getLocalName();

				// If the received price is higher than last highest price, then update it
				// as the latest highest price
				if (receivedPrice > currentPrice) {
					currentPrice = receivedPrice;
					currentBidder = sender;
					currentBidderAid = msg.getSender();
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("The auctioneer announces that the latest highest price is " + msg.getContent() + " from " + currentBidder);
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("");
					getNewPrice = true;
					conflictDeal = false;
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
			if (timer >= 5) {
				if (!conflictDeal) {
					closeAuction = true;
					System.out.println(" ");
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					System.out.println("The auction is closed, the " + auctionGood + " was sold to " + currentBidder + " with the price " + String.valueOf(currentPrice));
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					System.out.println(" ");
					ACLMessage cfp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					cfp.addReceiver(currentBidderAid);
					cfp.setContent(String.valueOf(currentPrice));
					cfp.setConversationId(auctionGood);
					cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
					myAgent.send(cfp);
					myAgent.doDelete();
				}
				else {
					System.out.println(" ");
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					System.out.println("The auction is closed with conflict deal since no bidder agent want to give a price higer than strating price");
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					System.out.println(" ");
					myAgent.doDelete();
				}
			} else {
			}
		}
	}

}
