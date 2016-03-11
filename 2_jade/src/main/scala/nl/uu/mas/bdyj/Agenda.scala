package nl.uu.mas.bdyj

case class Agenda(appointments:Map[Int,String] = Map()) {
	def reserve(timeSlot:Int, reason:String) = Agenda(appointments + (timeSlot -> reason))
	def unreserve(timeSlot:Int) = Agenda(appointments - timeSlot)
	def isFree(timeSlot:Int) = !appointments.keySet.contains(timeSlot)
	def hasReservation(what:String) = appointments.values.exists(_ == what)
	def getFreeSpot = appointments.keys.size
}
