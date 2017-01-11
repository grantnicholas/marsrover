import java.io.PrintStream

import Parsing.parseAllLines

object Main extends App{
  //The entrypoint to the script
  //Do some basic validations (ie: a file of instructions is provided and the file has at least 3 lines)
  //If an outfile_path is provided, output the stuff there; otherwise output to stdout
  val infile_path = args.lift(0) match {
    case Some(str) => str
    case None => throw new IllegalArgumentException("Must supply file_path as the first argument to the scala script")
  }
  val outfile_path = args.lift(1)
  val printer = new PrintStream(
    outfile_path match{
      case Some(str) => new java.io.FileOutputStream(new java.io.File(str))
      case None => System.out
    }
  )

  def run_main(infile_path: String, printer: PrintStream) = {
    val file_lines  = scala.io.Source.fromFile(infile_path).getLines().toList
    if(file_lines.length <= 2){
      throw new IllegalArgumentException(s"The file ${infile_path} is empty or malformed; please supply correct file")
    }

    //Parse the List[String] into a grid, navigate the grid (updating the state of the rovers), and then write the output
    val grid = parseAllLines(file_lines)
    grid.navigate()
    grid.write(printer)
  }

  run_main(infile_path, printer)

}
