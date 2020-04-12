# JavaRandomReverser
Reverses the possible internal seed(s) of JavaRandom given information on its output in the form of a system of inequalities on various Random calls. Works by reducing the problem to finding certain vectors in a lattice, which is then solved through a branch and bound algorithm using a reduced version of the lattice.
Currently enumerate.java is essentially a java port of the algorithm found here https://github.com/rjb3977/Lattice/blob/master/Lattice.cs, of which a much faster version can be found here: https://github.com/rjb3977/lattice-c.
