package nl.uu.mas.bdyj.agents

import jade.core.Agent
import jade.core.behaviours.TickerBehaviour
import org.slf4j.LoggerFactory

import scala.util.Random

class Initializer extends Agent{
	val log = LoggerFactory.getLogger(classOf[Initializer])
	override def setup():Unit = {
		addBehaviour(new SpawnBehavior(this, 3000))
	}
}
class SpawnBehavior(agent: Agent, timeInMS:Long) extends TickerBehaviour(agent,timeInMS){
	val log = LoggerFactory.getLogger(classOf[SpawnBehavior])
	var agentCount = 0
	val arguments = Array[AnyRef]()
	override def onTick(): Unit = {
		val newName = s"client-$agentCount"
		log.info(s"creating $newName")
		val possibleServices = Seq(ServiceProvider.Dentist, ServiceProvider.HairDresser)
		import SpawnBehavior.rng
		val taken = rng.shuffle(possibleServices).take(rng.nextInt(possibleServices.length)+1)
		agent.getContainerController.acceptNewAgent(newName, new Client(taken)).start()
		agentCount += 1
	}
}
object SpawnBehavior{
	val rng = Random
}
