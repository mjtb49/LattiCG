package randomreverser.decomposition;

import org.junit.Test;
import randomreverser.math.component.Matrix;
import randomreverser.math.decomposition.LUDecomposition;
import randomreverser.math.decomposition.LUResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LUDecompositionTest {

	@Test
	public void testLU1() {
		Matrix m = Matrix.identityMatrix(4);
		LUResult result = LUDecomposition.decompose(m);
		assertTrue(m.equals(result.getL().multiply(result.getU()), 0.0001F));
	}

	@Test
	public void testLU2() {
		Matrix m = Matrix.fromString("{{1, 5}, {2, -3}}");
		LUResult result = LUDecomposition.decompose(m);
		assertTrue(m.equals(result.getL().multiply(result.getU()), 0.0001F));
	}

	@Test
	public void testLU3() {
		Matrix m = Matrix.fromString("{{4, 3, 2, 1}, {1, 10, 3, 4}, {5, 3, 2, -4}, {4, 8, 7, 9}}");
		LUResult result = LUDecomposition.decompose(m);
		assertTrue(m.equals(result.getL().multiply(result.getU()), 0.0001F));
	}

	@Test
	public void testDet1() {
		Matrix m = Matrix.identityMatrix(4);
		LUResult result = LUDecomposition.decompose(m);
		assertEquals(result.getDet(), 1.0D, 0.0D);
	}

	@Test
	public void testDet2() {
		Matrix m = Matrix.fromString("{{1, 5}, {2, -3}}");
		LUResult result = LUDecomposition.decompose(m);
		assertEquals(result.getDet(), -13.0D, 0.0D);
	}

	@Test
	public void testDet3() {
		Matrix m = Matrix.fromString("{{4, 3, 2, 1}, {1, 10, 3, 4}, {5, 3, 2, -4}, {4, 8, 7, 9}}");
		LUResult result = LUDecomposition.decompose(m);
		assertEquals(result.getDet(), 602.0D, 0.0D);
	}

}
