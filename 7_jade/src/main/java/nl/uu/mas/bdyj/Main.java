package nl.uu.mas.bdyj;

import jade.Boot;
import jade.core.Agent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
		Boot.main("-gui -local-port 1100 jade.Boot;buyer1:examples.bookTrading.BookBuyerAgent(book1,book2,book3);seller1:examples.bookTrading.BookSellerAgent(book2,book3,book4)".split(" "));

	}

}
