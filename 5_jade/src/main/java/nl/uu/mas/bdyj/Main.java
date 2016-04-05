package nl.uu.mas.bdyj;

import jade.Boot;
import jade.core.*;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import nl.uu.mas.bdyj.valstrat.RandomValuation;


import java.util.LinkedList;
import java.util.List;

class Main{
//	private static final Logger log = LoggerFactory.getLogger(Main.class);
	public static void main(String[] argsb){
//		log.info("the JADI returns!");
		final Agent a = new Agent(){
			@Override
			protected void setup() {
				super.setup();
//				log.info("agent reporting");
			}
		};
		Boot.main("-gui -local-port 1101 jade.Boot;Bidder_0:nl.uu.mas.bdyj.BidderAgent(book,100);Bidder_1:nl.uu.mas.bdyj.BidderAgent(book,150);Bidder_2:nl.uu.mas.bdyj.BidderAgent(book,80);Auctioneer:nl.uu.mas.bdyj.AuctioneerAgent(book,30)".split(" "));
	}
}
