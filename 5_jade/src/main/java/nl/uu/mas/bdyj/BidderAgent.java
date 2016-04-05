package nl.uu.mas.bdyj;

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


public class BidderAgent extends Agent{
	public BidderAgent(float valuation, String auctionGood){
		this.valuation = valuation;
		this.auctionGood = auctionGood;
	}
	// agent initializations
	float valuation;
	String auctionGood;
	// currentBidPrice is the current bid price for each bidder agent
	float currentBidPrice = 0;
	protected void setup() {
		System.out.println("Hello! BidderAgent " + getAID().getLocalName() + " is ready.");
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(auctionGood);
		sd.setName("JADE-Auction");
		dfd.setName(getAID());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new OfferBid());
		addBehaviour(new WinBid());

	}
	
	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Printout a dismissal message
		System.out.println("BidAgent "+getAID().getLocalName()+" terminating.");
	}
	
	private class OfferBid extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP),
					                                 MessageTemplate.MatchConversationId(auctionGood));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				// currentPrice is the latest higest price from all bidder agents
				String msgContent = msg.getContent();
				String msgSplit[] = msgContent.split(" ");
				// msgSplit[0] is currentBidder and msgSplit[1] is currentPrice
				String currentBidder = msgSplit[0];
				float currentPrice = Float.valueOf(msgSplit[1]);
				// if the latest highest price is larger than the valuation of each bidder agent
				// then quit the auction and terminate the bidder agent
				if (currentPrice > valuation) {
					System.out.println(getAID().getLocalName()+" quit the auction!");
					System.out.println("");
					myAgent.doDelete();
				}
				
				// if the latet highest price if less than the valuation
				// and if not given by the bidder agent self
				// then give a new bid price which is higher than the latest higest price and less than valuation
				else if (currentBidder.equals(getAID().getLocalName()) == false) {
					float minIncrement = 3;
					float tmpPrice =  (float) (currentPrice + minIncrement);
					if (tmpPrice <= valuation) {
						currentBidPrice = tmpPrice;
					}
					else {
						currentBidPrice = valuation;
					}
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setConversationId(auctionGood);
					reply.setContent(String.valueOf(currentBidPrice));
					myAgent.send(reply);
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println(getAID().getLocalName() + " cries a higher price: " + String.valueOf(currentBidPrice));
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println("");
				}
				// if the latest highest is given by the bidder agent self, then wait to see whether
				// there are some other bidder agents will give a higer price.
				else {
					
				}
			}
			else {
				block(5000);
			}
		}
	}
	
	private class WinBid extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                    MessageTemplate.MatchConversationId(auctionGood));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				myAgent.doDelete();
			}
			else {
				block();
			}
		}
	}
}
