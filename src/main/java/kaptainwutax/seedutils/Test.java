package kaptainwutax.seedutils;

import kaptainwutax.seedutils.math.component.Basis;
import kaptainwutax.seedutils.math.component.Vector;
import kaptainwutax.seedutils.math.component.number.BigNumber;
import kaptainwutax.seedutils.math.lattice.LLL;

public class Test {

	public static void main(String[] args) {
		LLL<BigNumber> bigNumberLLL = new LLL<>();

		LLL.Parameters<BigNumber> params = new LLL.Parameters<BigNumber>().setDelta(new BigNumber(0.75D)).setDebug(false);

		Basis<BigNumber> basis = new Basis<>(
				new Vector<BigNumber>(new BigNumber(1), new BigNumber(0x5dEECE66DL)),
				new Vector<BigNumber>(new BigNumber(0), new BigNumber(1L << 48))
		);

		Basis<BigNumber> result = bigNumberLLL.reduce(basis, params);
		System.out.println(result);
	}

}
