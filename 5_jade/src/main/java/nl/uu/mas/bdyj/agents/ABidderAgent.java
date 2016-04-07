package nl.uu.mas.bdyj.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import nl.uu.mas.bdyj.Item;
import nl.uu.mas.bdyj.valstrat.NextPriceStrategy;

/**
 * ABidder servers as a common base for all bidders.
 * It handles most of the message received logic whie giving the childeren
 * the choice on how to implement it.
 */
abstract public class ABidderAgent extends Agent{
	public ABidderAgent(NextPriceStrategy valuation, Item auctionGood){
		super();
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
		addBehaviour(new EndBid());
		addBehaviour(new OfferBid());
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
	//When received ACCEPT_PROPOSAL message, all bidders quit
	private class EndBid extends CyclicBehaviour{
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
					MessageTemplate.MatchConversationId(auctionGood.name)
			);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.println(getAID().getLocalName()+" quit the auction, while enjoying his new "+ auctionGood.name);
				System.out.println("");
				myAgent.doDelete();
			}
			else {
				block();
			}
		}
	}

	abstract void onMessage(String msg, ACLMessage reply);
	private class OfferBid extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.CFP),
					MessageTemplate.MatchConversationId(auctionGood.name)
			);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				onMessage(msg.getContent(), msg.createReply());
			}
			else {
				block();
			}
		}
	}
}
