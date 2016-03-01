package nl.uu.mas.bdyj

import jade.core.{MainContainer, Agent}
import jade.core.behaviours.TickerBehaviour
import org.slf4j.LoggerFactory

class Initializer extends Agent{
	val log = LoggerFactory.getLogger(classOf[Initializer])
	override def setup() = {
		addBehaviour(new SpawnBehavior(this, 3000))
		log.info("spawining!!")
	}
}
class SpawnBehavior(agent: Agent, timeInMS:Long) extends TickerBehaviour(agent,timeInMS){
	val log = LoggerFactory.getLogger(classOf[SpawnBehavior])
	var agentCount = 0
	val arguments = Array[AnyRef]()
	override def onTick(): Unit = {
		val newName = s"client-$agentCount"
		log.info(s"creating $newName")
		agent.getContainerController.acceptNewAgent(newName, new Client)
		agentCount += 1
	}
}
