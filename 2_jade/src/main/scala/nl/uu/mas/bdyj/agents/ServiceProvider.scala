package nl.uu.mas.bdyj.agents

import jade.core.Agent
import jade.domain.DFService
import jade.domain.FIPAAgentManagement.{ServiceDescription, DFAgentDescription}
import nl.uu.mas.bdyj.agents.ServiceProvider.ServiceType

import scala.util.Random


class ServiceProvider(service:ServiceType) extends Agent{
	override def setup() = {
		val description = new DFAgentDescription
		description.setName(getAID)
		description.addServices({
			val sd = new ServiceDescription
			sd.setType(service.toString)
			sd.setName(s"${this.getName} his ${service.toString} services")
			sd
		})
		DFService.register(this, description)
	}
	override def takeDown() = DFService.deregister(this)
}
object ServiceProvider{
	sealed trait ServiceType{}
	case object HairDresser extends ServiceType{}
	case object Dentist extends ServiceType{}
}
