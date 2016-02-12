package nl.uu.mas.bdyj;

import examples.bookTrading.BookSellerAgent;
import examples.bookTrading.BookSellerGui;
import examples.jess.BasicJessBehaviour;
import jade.Boot;
import jade.core.Agent;
import jade.domain.JADEAgentManagement.CreateAgent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Random;

class Main{
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) {
		log.info("the JADI returns!");
		final Agent a = new Agent(){
			@Override
			protected void setup() {
				super.setup();
				log.info("agent reporting");
			}
		};
		Boot.main(new String[]{"-gui -local-port 1100 jade.Boot;buyer1:examples.bookTrading.BookBuyerAgent(book1,book2,book3);seller1:examples.bookTrading.BookSellerAgent(book2,book3,book4)"});

	}

}
