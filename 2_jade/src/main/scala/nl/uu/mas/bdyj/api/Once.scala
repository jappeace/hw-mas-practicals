package nl.uu.mas.bdyj.api

import jade.core.behaviours.OneShotBehaviour

class Once[E](func: =>E) extends OneShotBehaviour{
	override def action(): Unit = func
}
object Once{
	def apply[E](func: => E):Once[E] = new Once[E](func)
}
