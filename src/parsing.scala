object Parsing {
  def parseAllLines(lines: List[String]): Grid = {
    //Parses the List[String] input and returns a populated Grid() object
    //Note there is a lot of error checking I'm glossing over here: mainly a bunch of forms of invalid input
    //IE) What if someone uses floats instead of ints, what if someone doesn't supply all the needed inputs, etc
    val grid_line :: rover_lines = lines
    val grid_size = {
      val sizes = grid_line.split(" ")
      (sizes(0).toInt, sizes(1).toInt)
    }

    val rover_and_commands = {
      val rover_paired_lines = rover_lines.grouped(2).map(l => (l(0), l(1))).toList
      rover_paired_lines.map(pair => {
        val rover = {
          val split = pair._1.split(" ")
          new Rover(split(0).toInt, split(1).toInt, Direction.toDirection(split(2)).get)
        }
        val commands = pair._2.toCharArray.toList.flatMap(Command.toCommand)
        (rover, commands)
      })
    }

    new Grid(grid_size, rover_and_commands)
  }
}