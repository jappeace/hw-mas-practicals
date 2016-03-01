package nl.uu.mas.bdyj

import jade.core.{Agent}
import nl.uu.mas.bdyj.ServiceProvider.{Dentist, HairDresser, ServiceType}


class ServiceProvider(service:ServiceType) extends Agent{}
object ServiceProvider{
	sealed trait ServiceType{}
	case object HairDresser extends ServiceType{}
	case object Dentist extends ServiceType{}
}
