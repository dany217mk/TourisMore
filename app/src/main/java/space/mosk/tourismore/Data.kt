package space.mosk.tourismore

data class MarkerInfo(val string: String, val lng : Double, val lat : Double)
data class Route(var points : ArrayList<MarkerInfo>, val l : Int)