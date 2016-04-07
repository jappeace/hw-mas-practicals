package nl.uu.mas.bdyj.agents;

import jade.core.Agent;

//import java.util.Random;
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
import nl.uu.mas.bdyj.valstrat.ItemValuation;

public class BidderAgentSecond extends Agent{
	public BidderAgentSecond(ItemValuation valuation, Item auctionGood){
		this.valuation = valuation;
		this.auctionGood = auctionGood;
	}
	// agent initializations
	ItemValuation valuation;
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
					                                 MessageTemplate.MatchConversationId(auctionGood.name));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				// currentPrice is the latest higest price from all bidder agents
				String msgContent = msg.getContent();
				String msgSplit[] = msgContent.split(" ");
				// msgSplit[0] is currentBidder and msgSplit[1] is currentPrice
				String currentBidder = msgSplit[0];
				int currentPrice = Integer.valueOf(msgSplit[1]);
				int newval = valuation.valuate(auctionGood);
				// if the latest highest price is larger than the valuation of each bidder agent
				// then quit the auction and terminate the bidder agent
				if (newval  <= ANextPriceStrategy.DONT_WANT) {
					System.out.println(getAID().getLocalName()+" quit the auction!");
					System.out.println("");
					myAgent.doDelete();
					return;
				}
				
				// if the latet highest price if less than the valuation
				// and if not given by the bidder agent self
				// then give a new bid price which is higher than the latest higest price and less than valuation
				if (!currentBidder.equals(getAID().getLocalName())) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setConversationId(auctionGood.name);
					reply.setContent(newval+"");
					myAgent.send(reply);
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println(getAID().getLocalName() + " cries a higher price: " + newval);
					System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println("");
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
                    MessageTemplate.MatchConversationId(auctionGood.name));
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
