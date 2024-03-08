# JavaRandomReverser
Reverses the possible internal seed(s) of Java's java.util.Random class given information on its output in the form of a system of inequalities on various Random calls. The algorithm works by reducing the problem to finding certain vectors in a lattice, which is then solved through a branch and bound algorithm using a reduced version of the lattice.


# Use the repository

All the packages are uploaded here through Jenkins CI: https://maven.latticg.com

You can browse it by hands here : https://nexus.seedfinding.com/#browse/browse:maven-latticg

The javadocs can be seen here : https://latticg.com

To use the libs you need first to declare the maven `maven { url "https://maven.latticg.com/"}` :

Gradle Groovy DSL :

`implementation 'com.seedfinding:latticg:1.07@jar'`

Apache Maven:
```
<dependency>
  <groupId>com.seedfinding</groupId>
  <artifactId>latticg</artifactId>
  <version>1.07</version>
</dependency>
```

Scala SBT

`libraryDependencies += "com.seedfinding" % "latticg" % "1.07"`
