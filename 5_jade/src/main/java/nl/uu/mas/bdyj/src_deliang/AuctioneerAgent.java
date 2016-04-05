package nl.uu.mas.bdyj;

import jade.core.Agent;

import java.util.Timer;

import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;


public class AuctioneerAgent extends Agent{
	// agent initializations
	// the auctioneer will send the latest price to every bidder agents when getNewPrice is asserted
	// set the initial value of getNewPrice to true to make the auctioneer agent send out the starting price
	// getNewPrice will be set to false after sending the latest price and 
	// be set to true after receiving a new price which is higher than current price from any bidder agents
	private boolean getNewPrice = true;
	String auctionGood;
	float startingPrice;
	float currentPrice;
	String currentBidder;
	AID currentBidderAid;
	int timer = 0;
	boolean closeAuction = false;
	protected void setup() {
		System.out.println("Hello! AuctioneerAgent " + getAID().getLocalName() + " is ready.");
		// get the arguments
		Object[] args = getArguments();
		if ((args != null) && (args.length > 0)) {
			auctionGood = (String) args[0];
			startingPrice = Float.valueOf((String)args[1]);
			currentPrice = startingPrice;
			System.out.println("The auction good is " + (String) args[0]+ ", and the starting price is " + (String)args[1]);
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
					System.out.println("timer = " + String.valueOf(timer));
				}
			});
		}
		else {
			System.out.println("Invaild arguments for AuctioneerAgent, please set the auction good for AuctioneerAgent");
			doDelete();
		}
		
	}
	
	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("AuctioneerAgent "+getAID().getLocalName()+" terminating.");
	}
	
	/*
	 * behaviour for acutioneer to announce the latest higest price
	 * to every bidder which are still in the acution 
	 */
	private class SendPrice extends CyclicBehaviour {
		public void action() {
			AID bidderAgents[] = new AID[5];
			int numOfBidder = 0;
			
			if (getNewPrice == true) {
				getNewPrice = false;
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
					for (int i = 0; i < numOfBidder; i ++) {
						bidderAgents[i] = result[i].getName();
						System.out.println(bidderAgents[i].getLocalName());
					}
					System.out.println("****************************************************");
					System.out.println("");
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
				// Send latest highest price to all bidder agents which are still in	
				for (int i = 0; i < numOfBidder; i ++) {
					ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
					cfp.addReceiver(bidderAgents[i]);
					// content is the latest highest price and the local name of the bidder who cried this price
					// format is localName + " " + currentPrice
					cfp.setContent(currentBidder + " " + String.valueOf(currentPrice));
					cfp.setConversationId(auctionGood);
					cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
					myAgent.send(cfp);
				}
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
				float receivedPrice = Float.valueOf(msg.getContent());
				String sender = msg.getSender().getLocalName();
				
				// If the received price is higher than last highest price, then update it
				// as the latest highest price
				if (receivedPrice > currentPrice) {
					currentPrice = receivedPrice;
					currentBidder = sender;
					currentBidderAid = msg.getSender();
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("The acutioneer announce that the latest highest price is " + msg.getContent());
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("");
					getNewPrice = true;
				}
			}
			else {
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
				ACLMessage cfp = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				cfp.addReceiver(currentBidderAid);
				cfp.setContent(String.valueOf(currentPrice));
				cfp.setConversationId(auctionGood);
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				myAgent.doDelete();
			}
			else {
			}
		}
	}
	
}
