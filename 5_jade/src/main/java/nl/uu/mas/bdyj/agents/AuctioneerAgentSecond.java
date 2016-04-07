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

	// agent initializations
	// the auctioneer will send the latest price to every bidder agents when getNewPrice is asserted
	// set the initial value of getNewPrice to true to make the auctioneer agent send out the starting price
	// getNewPrice will be set to false after sending the latest price and 
	// be set to true after receiving a new price which is higher than current price from any bidder agents
	int currentPrice;
	String currentBidder;
	AID currentBidderAid;
	int timer = 0;
	boolean closeAuction = false;

	protected void setup() {
		System.out.println("Hello! AuctioneerAgent " + getAID().getLocalName() + " is ready.");
		// start the auction, delay 2000ms to wait all bidder agents start
		addBehaviour(new WakerBehaviour(this, 2000) {
			protected void handleElapsedTimeout() {
				addBehaviour(new SendPrice());
				addBehaviour(new ReceiveBid());
				addBehaviour(new CloseAuction());
			}
		});
		addBehaviour(new TickerBehaviour(this, 1000) {
			protected void onTick() {
				timer = timer + 1;
							}
		});
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
	 * behaviour for auctioneer to receive bids from each bidder 
	 */
	int secondPrice = 0;

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
					System.out.println("test1 :" + currentPrice);
					secondPrice = currentPrice;
					currentPrice = receivedPrice;
					System.out.println(" test2 :" + currentPrice);
					System.out.println(" test3 :" + secondPrice);
					currentBidder = sender;
					currentBidderAid = msg.getSender();

				} else {
					if (receivedPrice > secondPrice) {
						secondPrice = receivedPrice;
					}
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
				closeAuction = true;
				System.out.println(" ");
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println("The auction is closed, the " + auctionGood + " was sold to " + currentBidder + " with the price " + String.valueOf(secondPrice));
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println(" ");
				ACLMessage cfp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				cfp.addReceiver(currentBidderAid);
				cfp.setContent(String.valueOf(currentPrice));
				cfp.setConversationId(auctionGood);
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				myAgent.doDelete();
			} else {
			}
		}
	}

}
