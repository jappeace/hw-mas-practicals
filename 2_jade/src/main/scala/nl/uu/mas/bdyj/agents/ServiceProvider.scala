package nl.uu.mas.bdyj.agents

import java.io.ObjectInputStream

import akka.serialization.Serialization
import jade.core.Agent
import jade.core.behaviours.{TickerBehaviour, Behaviour}
import jade.domain.DFService
import jade.domain.FIPAAgentManagement.{ServiceDescription, DFAgentDescription}
import jade.lang.acl.{UnreadableException, ACLMessage}
import nl.uu.mas.bdyj.Agenda
import nl.uu.mas.bdyj.agents.ServiceProvider.ServiceType
import nl.uu.mas.bdyj.api.Message
import org.slf4j.LoggerFactory

import scala.util.Random


class ServiceProvider(service:ServiceType) extends Agent{
	var agenda = new Agenda()
	override def setup() = {
		val description = new DFAgentDescription
		description.setName(getAID)
		description.addServices({
			val sd = new ServiceDescription
			sd.setType(service.toString)
			sd.setName(s"${this.getName} his ${service.toString} services")
			sd
		})
		addBehaviour(new ServiceBehavior(this, 20))
		DFService.register(this, description)
	}
	override def takeDown() = DFService.deregister(this)
}
object ServiceProvider{
	sealed trait ServiceType{}
	case object HairDresser extends ServiceType{}
	case object Dentist extends ServiceType{}
}
class ServiceBehavior(server: ServiceProvider, timeMS:Long) extends TickerBehaviour(server, timeMS){
	val log = LoggerFactory.getLogger(classOf[ServiceBehavior])
	override def onTick(): Unit = for (msg <- Message.receivedFrom(server)){
		val reply = msg.createReply()
		val sender = msg.getSender
		val desiredSlot = try {
			msg.getContentObject.asInstanceOf[Int]
		}catch {
			case e:UnreadableException =>
				log.warn(s" unreadable exception: $e")
				0
			case x:Throwable => throw x
		}
		msg.getPerformative match {
			case ACLMessage.CFP =>
				reply.setContentObject(desiredSlot)
				if(server.agenda.isFree(desiredSlot)){
					server.agenda = server.agenda.reserve(desiredSlot, sender.getLocalName)
					// send confirm
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL)
				}else{
					// send no
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL)
				}
				server.send(reply)

			case ACLMessage.FAILURE =>
				server.agenda = server.agenda.unreserve(desiredSlot)
				log.info(s"$sender has unreserved because he can't")

			case ACLMessage.CONFIRM =>
				log.info(s"$sender has succesfully done a reservation at $desiredSlot")
				log.info(s"${server.getName} agenda now looks like: ${server.agenda}")
		}
	}
}
