package nl.uu.mas.bdyj

import jade.Boot
import jade.core.Agent
import org.slf4j.LoggerFactory
import org.slf4j.Logger

import scala.util.Random

object Main {
	private val log: Logger = LoggerFactory.getLogger("Main")

	def main(args: Array[String]) {
		log.info("the JADI returns!")

		val args = s"-gui -local-port ${(Random.nextFloat()*10000).toInt}".split(" ")
		import jade.core.ProfileImpl
		import jade.core.Runtime
		import jade.util.leap.Properties
		val iae:ProfileImpl  = if(args.nonEmpty) {
			if(args(0).startsWith("-")) {
				val pp:Properties  = Boot.parseCmdLineArgs(args)
				if(pp == null) {
					return
				}
				new ProfileImpl(pp)
			} else {
				new ProfileImpl(args(0))
			}
		} else {
			new ProfileImpl("leap.properties")
		}

		val inst = Runtime.instance()
		inst.setCloseVM(true)

		val container = if(iae.getBooleanProperty("main", true)) {
			inst.createMainContainer(iae)
		} else {
			inst.createAgentContainer(iae)
		}
		container.acceptNewAgent("dentist", new ServiceProvider(ServiceProvider.Dentist))
		container.acceptNewAgent("hairdresser", new ServiceProvider(ServiceProvider.HairDresser))
		container.acceptNewAgent("initbaby", new Initializer)
	}
}