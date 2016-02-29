package nl.uu.mas.bdyj

import jade.core.{Agent, BaseService}
import nl.uu.mas.bdyj.ServiceProvider.{HairDresser, ServiceType}


class ServiceProvider(offeredService:ServiceType) extends BaseService{
	override def getName: String = offeredService.getClass.getSimpleName
}
object ServiceProvider{
	sealed trait ServiceType{}
	case object HairDresser extends ServiceType{}
	case object Dentist extends ServiceType
}
