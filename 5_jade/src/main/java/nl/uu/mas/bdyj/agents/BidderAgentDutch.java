package nl.uu.mas.bdyj.agents;

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
import nl.uu.mas.bdyj.Item;
import nl.uu.mas.bdyj.valstrat.ANextPriceStrategy;
import nl.uu.mas.bdyj.valstrat.NextPriceStrategy;


public class BidderAgentDutch extends Agent{
	public BidderAgentDutch(NextPriceStrategy valuation, Item auctionGood){
		this.valuation = valuation;
		this.auctionGood = auctionGood;
	}
	// agent initializations
	NextPriceStrategy valuation;
	Item auctionGood;
	protected void setup() {
		System.out.println("Hello! BidderAgent " + getAID().getLocalName() + " is ready.");
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(auctionGood.name);
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
		addBehaviour(new EndBid());

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
					                                 MessageTemplate.MatchConversationId(auctionGood.name));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Get current price from it
				int currentPrice = Integer.valueOf(msg.getContent());
				int newval = valuation.decide(auctionGood, currentPrice);
				
				// if the current price is acceptable, then send a bid.
				if (newval > ANextPriceStrategy.DONT_WANT) {

					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setConversationId(auctionGood.name);
					reply.setContent(currentPrice+"");
					myAgent.send(reply);
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println(getAID().getLocalName() + " cries a bid: " + currentPrice);
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println("");

				}
			}
			else {
				block(500);
			}
		}
	}
	
	//When received ACCEPT_PROPOSAL message, all bidders quit
	private class EndBid extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
                    MessageTemplate.MatchConversationId(auctionGood.name));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println(getAID().getLocalName()+" quit the auction!");
				System.out.println("");
				myAgent.doDelete();
			}
			else {
				block();
			}
		}
	}
}
