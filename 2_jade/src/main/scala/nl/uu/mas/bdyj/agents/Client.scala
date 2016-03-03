package nl.uu.mas.bdyj.agents

import java.io.Serializable

import jade.core.behaviours._
import jade.core.{AID, Agent}
import jade.domain.DFService
import jade.domain.FIPAAgentManagement.{DFAgentDescription, ServiceDescription}
import jade.lang.acl.ACLMessage
import nl.uu.mas.bdyj.Agenda
import nl.uu.mas.bdyj.agents.ServiceProvider.ServiceType
import nl.uu.mas.bdyj.api._
import org.slf4j.LoggerFactory

import scala.util.Random


class Client(servicesNeeded:Seq[ServiceType]) extends Agent{
	val log = LoggerFactory.getLogger(classOf[Client])
	var agenda = new Agenda()
	override def setup() = {
		log.info(s"wanting: $servicesNeeded")
		val aids = servicesNeeded.map(x=>new AID(x.getClass.getName, AID.ISLOCALNAME))
		val agent = this
		addBehaviour(Once{
			for(behaviour <- servicesNeeded.map(x=>new NegotionBehavior(agent, x))){
				agent.addBehaviour(behaviour)
			}
		})
	}
}
class NegotionBehavior(client: Client, desiredService:ServiceType) extends Behaviour{

	val log = LoggerFactory.getLogger(classOf[NegotionBehavior])
	val reservationString = desiredService.toString
	val serviceProvider = DFService.search(client, {
		val temp = new DFAgentDescription()
		temp.addServices({
			val ser = new ServiceDescription()
			ser.setType(desiredService.toString)
			ser
		})
		temp
	}).head
	private def demandService(slot:Int) = {
		val message = new ACLMessage(ACLMessage.CFP)
		message.addReceiver(serviceProvider.getName)
		message.setReplyWith("blah")
		message.setContentObject(slot)
		client.send(message)
	}
	demandService(client.agenda.getFreeSpot)
	override def action(): Unit = {
		if(done()){
			block()
			return
		}
		for (msg <- Message.receivedFrom(client)){
			val reply = msg.createReply()
			reply.setContentObject(0)
			val sender =  msg.getSender
			val desiredSlot = msg.getContentObject.asInstanceOf[Int]
			msg.getPerformative match {
				case ACLMessage.ACCEPT_PROPOSAL =>
					reply.setContentObject(desiredSlot)
					if(client.agenda.isFree(desiredSlot)){
						client.agenda = client.agenda.reserve(desiredSlot, reservationString)
						reply.setPerformative(ACLMessage.CONFIRM)
					}else{
						reply.setPerformative(ACLMessage.FAILURE)
						demandService(client.agenda.getFreeSpot)
					}
					client.send(reply)
				case ACLMessage.REJECT_PROPOSAL =>
					var newSlot = desiredSlot + 1
					while(!client.agenda.isFree(newSlot)){
						newSlot += 1
					}
					demandService(newSlot)
			}
		}
	}
	override def done(): Boolean = client.agenda.hasReservation(reservationString)
	override def onEnd() = {
		val result = super.onEnd()
		client.doDelete()
		result
	}
}

