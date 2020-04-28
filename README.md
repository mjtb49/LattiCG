# JavaRandomReverser
Reverses the possible internal seed(s) of Java's java.util.Random class given information on its output in the form of a system of inequalities on various Random calls. The algorithm works by reducing the problem to finding certain vectors in a lattice, which is then solved through a branch and bound algorithm using a reduced version of the lattice.
