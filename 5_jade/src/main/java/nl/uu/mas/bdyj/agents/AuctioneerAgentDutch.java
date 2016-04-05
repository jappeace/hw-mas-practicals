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


public class AuctioneerAgentDutch extends Agent {
	public AuctioneerAgentDutch(int startingPrice, String auctionGood) {
		this.auctionGood = auctionGood;
		this.startingPrice = startingPrice;
		currentPrice = startingPrice;
	}

	// agent initializations
	// the auctioneer will send the latest price to every bidder agents when getNewPrice is asserted
	// set the initial value of getNewPrice to true to make the auctioneer agent send out the starting price
	// getNewPrice will be set to false after sending the latest price and 
	// be set to true after receiving a new price which is higher than current price from any bidder agents
	private boolean existBid = false;
	String auctionGood;
	int startingPrice;
	int currentPrice;
	String currentBidder;
	AID currentBidderAid;
	int timer = 0;
	boolean closeAuction = false;
	int minIncrement = 3;

	protected void setup() {
		System.out.println("Hello! AuctioneerAgent " + getAID().getLocalName() + " is ready.");
		final Agent a = this;
		addBehaviour(new TickerBehaviour(this, 1000) {
			protected void onTick() {
				timer = timer + 1;
				System.out.println("timer = " + String.valueOf(timer));
			}
		});

		addBehaviour(new SendPrice(this, 1000));

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

			AID bidderAgents[] = new AID[5];
			int numOfBidder = 0;

			if (!existBid) {
				// clear the timer;
				timer = 0;
				currentPrice = currentPrice - minIncrement;
				System.out.println("****************************************************");
				System.out.println("Now the price is: " + String.valueOf(currentPrice));
				// Update the list of Bidder agents which still in
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(auctionGood);
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					System.out.println("****************************************************");
					System.out.println("[" + getAID().getLocalName() + "] Found the following Bidder agents are still in the acution:");
					numOfBidder = result.length;
					for (int i = 0; i < numOfBidder; i++) {
						bidderAgents[i] = result[i].getName();
						System.out.println(bidderAgents[i].getLocalName());
					}
					System.out.println("****************************************************");
					System.out.println("");
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

				// Send latest highest price to all bidder agents which are still in
				for (int i = 0; i < numOfBidder; i++) {
					ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
					cfp.addReceiver(bidderAgents[i]);
					// content is the latest highest price and the local name of the bidder who cried this price
					// format is localName + " " + currentPrice
					cfp.setContent(String.valueOf(currentPrice));
					cfp.setConversationId(auctionGood);
					cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
					myAgent.send(cfp);
				}
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
			if (msg != null) {
				// PROPOSE Message received. Process it
				// receivePrice is the price received from any bidder agents
				int receivedPrice = Integer.valueOf(msg.getContent());
				String sender = msg.getSender().getLocalName();

				// If the received price is higher than last highest price, then update it
				// as the latest highest price
				if (receivedPrice == currentPrice) {
					currentPrice = receivedPrice;
					currentBidder = sender;
					currentBidderAid = msg.getSender();
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("The auctioneer announces that the latest highest price is " + msg.getContent());
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
			if (timer >= 5) {
				closeAuction = true;
				System.out.println(" ");
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println("The auction is closed, the " + auctionGood + " was sold to " + currentBidder + " with the price " + String.valueOf(currentPrice));
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				System.out.println(" ");




				// Update the list of Bidder agents which still in
				AID bidderAgents[] = new AID[5];
				int numOfBidder = 0;
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(auctionGood);
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					numOfBidder = result.length;
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

				for (int i = 0; i < numOfBidder; i++) {
					ACLMessage cfp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
					cfp.addReceiver(bidderAgents[i]);
					cfp.setContent(String.valueOf(currentPrice));
					cfp.setConversationId(auctionGood);
					cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
					myAgent.send(cfp);
					myAgent.doDelete();
				}
			}
		}
	}

}
