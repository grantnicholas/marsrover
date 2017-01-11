# Necessary prereqs
Java:
    java -version
    java version "1.8.0_112"
    Java(TM) SE Runtime Environment (build 1.8.0_112-b15)
    Java HotSpot(TM) 64-Bit Server VM (build 25.112-b15, mixed mode)

Scala:
    scala -version
    Scala code runner version 2.12.0 -- Copyright 2002-2016, LAMP/EPFL and Lightbend, Inc.


# Compiling the program

I used intellij during my development, but I tested using the scala compiler from the command line directly
and it works as well
```
cd marsrover
mkdir out
scalac src/marsrover.scala src/models.scala src/parsing.scala src/test/test_marsrover.scala -d out/
```

# Running the tests

```
scala -cp out/ Test <test input files dir> <test output files dir>
```

# Example run of tests
```
Checking if C:\hg\scala_projects\marsrover\src\test\inputs\all_left.txt is equivalent to C:\hg\scala_projects\marsrover\src\test\outputs\all_left.txt
0 0 N

0 0 N

Checking if C:\hg\scala_projects\marsrover\src\test\inputs\edges.txt is equivalent to C:\hg\scala_projects\marsrover\src\test\outputs\edges.txt
10 10 E
10 10 N

10 10 E
10 10 N

Checking if C:\hg\scala_projects\marsrover\src\test\inputs\example.txt is equivalent to C:\hg\scala_projects\marsrover\src\test\outputs\example.txt
1 3 N
5 1 E

1 3 N
5 1 E

All good!
```

# Running the program

```
scala -cp out/ Main <your sample file to input> <optional sample file to output; if not present will print to stdout>
```

# Example run

```
scala -cp out/ Main src/test/inputs/example.txt
1 3 N
5 1 E
```