package nl.uu.mas.bdyj.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AAuctioneer extends Agent{
	String auctionGood;
	int startingPrice;
	int currentPrice;
	private static final Logger log = LoggerFactory.getLogger(AAuctioneer.class);
	public AAuctioneer(int startingPrice, String auctionGood) {
		this.auctionGood = auctionGood;
		this.startingPrice = startingPrice;
		currentPrice = startingPrice;
	}
	public void broadcast(String str){
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(auctionGood);
		template.addServices(sd);
		try {
			for (DFAgentDescription agent : DFService.search(this, template)) {
				log.info("sending "+ str + " to " + agent.getName());
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				cfp.addReceiver(agent.getName());
				// content is the latest highest price and the local name of the bidder who cried this price
				// format is localName + " " + currentPrice
				cfp.setContent(str);
				cfp.setConversationId(auctionGood);
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
				this.send(cfp);

			}

		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}
	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("AuctioneerAgent " + getAID().getLocalName() + " terminating.");
	}
}
