import java.io.PrintStream

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

class Grid(val grid_size: (Int,Int), val rovers_and_commands: List[(Rover,List[Command.Command])]){
  //The Grid object says how big the plateau is and contains all the rovers and their list of instructions
  def navigate(): Unit ={
    //Actually moves the rovers, updating their state internally
    this.rovers_and_commands.foreach(tup => {
      val (rover, commands) = tup
      commands.foreach(comm => rover.runCommand(comm, this.grid_size))
    })
  }

  def write(printer: PrintStream): Unit ={
    //Writes the output to either stdout or a file (or anything really, as long as its a printstream)
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