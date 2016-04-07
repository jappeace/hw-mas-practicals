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


public class BidderAgentDutch extends ABidderAgent{
	public BidderAgentDutch(NextPriceStrategy valuation, Item auctionGood){
		super(valuation,auctionGood);
	}
	protected void setup() {
		super.setup();
		addBehaviour(new OfferBid());
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
	
}
