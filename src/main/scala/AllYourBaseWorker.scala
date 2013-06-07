package main.scala

import collection.mutable

/**
 * @author Administrator
 * @version 3/23/13 7:05 PM
 */
object AllYourBaseWorker {
  def process (data: Array[String]): Seq[Long] = {

    val results = new mutable.Queue[Long]
    val map = mutable.HashMap[Char, Int]()
    for (symbols <- data) {
      if (symbols.isEmpty) {
        sys.error("Symbols cannot be empty")
      }

      // Generate a map from symbol to integer that will generate the lowest interpretation of the symbols as a number
      map.clear()
      // The first symbol should be a 1, the smallest non-zero integer
      map(symbols(0)) = 1
      // Then find the next differing symbol to make it a 0
      val rest = symbols.dropWhile(symbols(0)==)
      if (rest.length > 0) {
        map(rest(0)) = 0
      }
      // Then assign the next lowest integer available to the remaining symbols
      for (symbol <- rest.drop(1)) {
        if (!map.contains(symbol)) {
          map(symbol) = map.size
        }
      }

      // The smallest base is 2
      val base = math.max(map.size, 2)
      var place = 1l
      var minSeconds = 0l
      for (symbol <- symbols.reverse) {
        if (!map.contains(symbol)) {
          sys.error("All symbols should be in the map; '%s' is not in the map for %s".format(symbol, symbols))
        }
        minSeconds += map(symbol) * place
        place *= base
      }
      results.enqueue(minSeconds)
    }
    results
  }
}
