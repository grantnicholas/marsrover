import java.io.{ByteArrayOutputStream, PrintStream}

import scala.reflect.internal.FatalError

object Test extends App{
  //Naive "testrunner"; did not want to take the time to integrate a real testrunner, so lets just use another main

  //Test the parsing of the example provided input
  val dummy_lines = List(
    "5 5",
    "1 2 N",
    "LMLMLMLMM",
    "3 3 E",
    "MMRMMRMRRM"
  )

  val output = Parsing.parseAllLines(dummy_lines)
  assert(output.grid_size == (5, 5))
  assert(output.rovers_and_commands.length == 2)

  val (first_rover, first_commands) = output.rovers_and_commands(0)
  assert(first_rover.x == 1)
  assert(first_rover.y == 2)
  assert(first_rover.direction == Direction.North)
  assert(first_commands.length == 9)
  assert(first_commands == List(
    Command.Left, Command.Move, Command.Left, Command.Move, Command.Left, Command.Move, Command.Left, Command.Move,
    Command.Move
  ))

  val (second_rover, second_commands) = output.rovers_and_commands(1)
  assert(second_rover.x == 3)
  assert(second_rover.y == 3)
  assert(second_rover.direction == Direction.East)
  assert(second_commands.length == 10)
  assert(second_commands == List(
    Command.Move, Command.Move, Command.Right, Command.Move, Command.Move, Command.Right, Command.Move, Command.Right,
    Command.Right, Command.Move
  ))


  //Verify moving out of bounds throws an exception
  val out_of_bounds_lines = List(
    "3 3",
    "1 1 N",
    "LRMLRMM"
  )
  try{
    val grid = Parsing.parseAllLines(out_of_bounds_lines)
    grid.navigate() //throws IndexOutOfBoundsException
    throw FatalError("Should never get here")
  }
  catch {
    case index: IndexOutOfBoundsException => ()
    case _: Throwable => assert(false)
  }


  //Do some integration testing to verify the algorithm of computing the final position works as intended
  //Must provide the path to the input and output directories
  val input_files  = new java.io.File(args(0)).listFiles.toList
  val output_files = new java.io.File(args(1)).listFiles.toList


  val file_pairs = for{
    in_file  <- input_files
    out_file <- output_files
    if in_file.getName.endsWith(".txt") && out_file.getName.endsWith(".txt") && in_file.getName == out_file.getName
  }yield (in_file, out_file)

  for {
    (in_file, out_file) <- file_pairs
  }{
    val stream = new ByteArrayOutputStream()
    val printer = new PrintStream(stream);
    Main.run_main(in_file.getAbsolutePath, printer)

    val expected_string = scala.io.Source.fromFile(out_file.getAbsolutePath).mkString
    println(s"Checking if ${in_file.getAbsolutePath} is equivalent to ${out_file.getAbsolutePath}")
    println(stream.toString)
    println(expected_string)
    assert(stream.toString == expected_string)
  }

  println("All good!")

}