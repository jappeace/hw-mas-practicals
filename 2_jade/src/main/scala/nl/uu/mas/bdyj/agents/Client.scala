package nl.uu.mas.bdyj.agents

import jade.core.{AID, Agent}
import nl.uu.mas.bdyj.agents.ServiceProvider.ServiceType
import org.slf4j.LoggerFactory

class Client(servicesNeeded:Seq[ServiceType]) extends Agent{
	val log = LoggerFactory.getLogger(classOf[Client])
	override def setup() = {
		log.info(s"wanting: $servicesNeeded")
		val aids = servicesNeeded.map(x=>new AID(x.getClass.getName, AID.ISLOCALNAME))
	}
}
