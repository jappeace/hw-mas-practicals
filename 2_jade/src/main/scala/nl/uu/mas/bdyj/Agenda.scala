package nl.uu.mas.bdyj

case class Agenda(appointments:Map[Int,String] = Map()) {
	def reserve(timeSlot:Int, reason:String) = Agenda(appointments + (timeSlot -> reason))
	def isFree(timeSlot:Int) = !appointments.keySet.contains(timeSlot)
}
