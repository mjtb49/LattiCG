# JavaRandomReverser
Reverses the possible internal seed(s) of Java's java.util.Random class given information on its output in the form of a system of inequalities on various Random calls. The algorithm works by reducing the problem to finding certain vectors in a lattice, which is then solved through a branch and bound algorithm using a reduced version of the lattice.

Currently enumerate.java is essentially a java port of the algorithm found here https://github.com/rjb3977/Lattice/blob/master/Lattice.cs, of which a much faster version can be found here: https://github.com/rjb3977/lattice-c.

To run this as of right now you need to have Z3 (https://github.com/Z3Prover/z3) installed (I'd recommend using their prebuilt binaries) and the relevant dll and jar somewhere java can find them. Eventually this dependancy will be removed.
