object Command{
  //Command Enum; Represents a Left, Right, or Move command
  //This command is the command that the rover receives at every step (either to move or rotate)
  sealed trait Command {def name: Char}
  case object Left extends Command {val name = 'L'}
  case object Right extends Command {val name = 'R'}
  case object Move extends Command {val name = 'M'}

  val commands = Seq(Left, Right, Move)

  def toCommand(name: Char): Option[Command] = commands.find(c => c.name == name)
}

object Direction{
  //Direction Enum; Represents a North, East, South, or West
  //This direction is the direction that the rover is facing
  sealed trait Direction {def name: String}

  case object North extends Direction {val name = "N"}
  case object East extends Direction {val name = "E"}
  case object South extends Direction {val name = "S"}
  case object West extends Direction {val name = "W"}

  val directions = Seq(North, East, South, West)

  def toDirection(name: String): Option[Direction] = directions.find(d => d.name == name)
}

class Grid(grid_size: (Int,Int), rovers_and_commands: List[(Rover,List[Command.Command])]){
  //The Grid object says how big the plateau is and contains all the rovers and their list of instructions
  def navigate(): Unit ={
    //Actually moves the rovers, updating their state internally
    this.rovers_and_commands.foreach(tup => {
      val (rover, commands) = tup
      commands.foreach(comm => rover.runCommand(comm, this.grid_size))
    })
  }

  def write(filename: Option[String]=None): Unit ={
    //Writes the output to either stdout or a file (depending on if a filepath is provided)
    val printer = new java.io.PrintStream(
      filename match{
        case Some(str) => new java.io.FileOutputStream(new java.io.File(str))
        case None => System.out
      }
    )

    for{
      (rover, _) <- this.rovers_and_commands
    }printer.println(rover.to_output)
  }
}

class Rover(var x: Int, var y: Int, var direction: Direction.Direction){
  //The Rover object; represents the position of the Rover as well as the direction it is facing
  //Note: the position (x,y) coordinates as well as the direction are mutable
  override def toString = s"Rover(x=${this.x}, y=${this.y}, direction=${this.direction})"

  def runCommand(command: Command.Command, grid_size: (Int,Int)) = {
    //Runs a command on the rover, mutating it's state
    //Chose this design (versus an immutable Rover) because otherwise we create a bunch of short-lived objects
    val updated_direction = command match {
      case Command.Left => direction match {
        case Direction.North => Direction.West
        case Direction.East  => Direction.North
        case Direction.South => Direction.East
        case Direction.West  => Direction.South
      }
      case Command.Right => direction match {
        case Direction.North => Direction.East
        case Direction.East  => Direction.South
        case Direction.South => Direction.West
        case Direction.West  => Direction.North
      }
      case Command.Move => direction
    }
    val (updated_x,updated_y) = command match {
      case Command.Left  => (x, y)
      case Command.Right => (x, y)
      case Command.Move  => direction match {
        case Direction.North => (x, y+1)
        case Direction.East  => (x+1, y)
        case Direction.South => (x, y-1)
        case Direction.West  => (x-1, y)
      }
    }

    val (x_bound, y_bound) = grid_size
    if(updated_x < 0 || updated_x > x_bound || updated_y < 0 || updated_y > y_bound){
      throw new IndexOutOfBoundsException("Cannot move rover off of the plateau")
    }
    this.direction = updated_direction
    this.x  = updated_x
    this.y  = updated_y
  }

  def to_output = s"${this.x} ${this.y} ${this.direction.name}"
}

def parseAllLines(lines: List[String]):Grid = {
  //Parses the List[String] input and returns a populated Grid() object
  //Note there is a lot of error checking I'm glossing over here: mainly a bunch of forms of invalid input
  //IE) What if someone uses floats instead of ints, what if someone doesn't supply all the needed inputs, etc
  val grid_line::rover_lines = lines
  val grid_size = {
    val sizes = grid_line.split(" ")
    (sizes(0).toInt, sizes(1).toInt)
  }

  val rover_and_commands = {
    val rover_paired_lines = rover_lines.grouped(2).map(l => (l(0), l(1))).toList
    rover_paired_lines.map(pair =>{
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

//The entrypoint to the script
//Do some basic validations (ie: a file of instructions is provided and the file has at least 3 lines)
val infile_path = args.lift(0) match {
  case Some(str) => str
  case None => throw new IllegalArgumentException("Must supply file_path as the first argument to the scala script")
}
val outfile_path = args.lift(1)

val file_lines  = scala.io.Source.fromFile(infile_path).getLines().toList
if(file_lines.length <= 2){
  throw new IllegalArgumentException(s"The file ${infile_path} is empty or malformed; please supply correct file")
}

//Parse the List[String] into a grid, navigate the grid (updating the state of the rovers), and then write the output
val grid = parseAllLines(file_lines)
grid.navigate()
grid.write(outfile_path)