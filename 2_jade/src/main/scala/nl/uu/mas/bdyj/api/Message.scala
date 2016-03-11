package nl.uu.mas.bdyj.api

import jade.core.Agent

object Message {
	def receivedFrom(actor: Agent) = {
			val result = actor.receive()
			if(result == null){
				None
			}else{
				Option(result)
			}
		}
}
