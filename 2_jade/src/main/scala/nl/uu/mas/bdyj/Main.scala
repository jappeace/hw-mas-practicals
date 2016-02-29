package nl.uu.mas.bdyj

import jade.Boot
import jade.core.Agent
import org.slf4j.LoggerFactory
import org.slf4j.Logger

object Main {
	private val log: Logger = LoggerFactory.getLogger("Main")

	def main(args: Array[String]) {
		log.info("the JADI returns!")
		val a: Agent = new Agent() {
			protected override def setup {
				super.setup
				log.info("agent reporting")
			}
		}
		Boot.main("-gui -local-port 1100 jade.Boot;buyer1:examples.bookTrading.BookBuyerAgent(book1,book2,book3);seller1:examples.bookTrading.BookSellerAgent(book2,book3,book4)".split(" "))
		new ServiceProvider(ServiceProvider.Dentist)
	}
}