package randomreverser.math.lattice;

import randomreverser.math.component.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Enumerate {
    public static List<BigVector> enumerate(BigMatrix basis, BigVector lower, BigVector upper, int threads) {
        SearchInfo root = new SearchInfo();
        root.size = basis.getRowCount();
        root.depth = 0;
        root.transform = basis.inverse();
        root.offset = root.transform.multiply(lower);
        root.fixed = new BigVector(root.size);
        root.table = new BigMatrix(2 * root.size + 1, root.size + 1);
        root.results = new ConcurrentLinkedQueue<>();
        root.pool = new EnumeratePool(threads);

        for (int i = 0; i < root.size; ++i) {
            root.table.set(i + 1, i, BigFraction.ONE);
            root.table.set(i + 1, root.size, upper.get(i).subtract(lower.get(i)));
        }

        root.pool.start(root);

        return new ArrayList<>(root.results);
    }

    public static List<BigVector> enumerate(BigMatrix basis, BigVector lower, BigVector upper) {
        return enumerate(basis, lower, upper, Runtime.getRuntime().availableProcessors());
    }

    public static List<Vector> enumerate(int dimensions, Vector lower, Vector upper, Matrix basis, Vector offset) {
	    BigMatrix bigBasis = new BigMatrix(dimensions, dimensions);
        BigVector bigLower = new BigVector(dimensions);
        BigVector bigUpper = new BigVector(dimensions);

	    for (int row = 0; row < dimensions; ++row) {
            bigLower.set(row, new BigFraction(Math.round(lower.get(row) - offset.get(row))));
            bigUpper.set(row, new BigFraction(Math.round(upper.get(row) - offset.get(row))));

	        for (int col = 0; col < dimensions; ++col) {
                bigBasis.set(row, col, new BigFraction(Math.round(basis.get(row, col))));
            }
        }

	    List<Vector> results = new ArrayList<>();

	    for (BigVector bigResult : enumerate(bigBasis, bigLower, bigUpper)) {
	        Vector result = new Vector(dimensions);

	        for (int i = 0; i < dimensions; ++i) {
	            result.set(i, bigResult.get(i).toDouble());
            }
        }

	    return results;
	}

	static int calls = 0;

	static void search(SearchInfo info) {
        if (info.depth == info.size) {
            info.results.offer(info.fixed.copy());
        } else {
            BigInteger min, max;
            BigFraction offset = info.offset.get(info.depth);
            BigVector x;
            BigVector transformRow = info.transform.getRow(info.depth);

            for (int col = 0; col < info.size; ++col) {
                info.table.set(0, col, transformRow.get(col).negate());
            }

            x = Optimize.optimize(info.table, info.size, info.depth);
            min = x.dot(transformRow).add(offset).ceil();

            for (int col = 0; col < info.size; ++col) {
                info.table.set(0, col, transformRow.get(col));
            }

            x = Optimize.optimize(info.table, info.size, info.depth);
            max = x.dot(transformRow).add(offset).floor();

            for (BigInteger i = min; i.compareTo(max) <= 0; i = i.add(BigInteger.ONE)) {
                BigFraction y = offset.subtract(i);

                if (y.signum() >= 0) {
                    for (int col = 0; col < info.size; ++col) {
                        info.table.set(1 + info.size + info.depth, col, transformRow.get(col).negate());
                    }

                    info.table.set(1 + info.size + info.depth, info.size, y);
                } else {
                    for (int col = 0; col < info.size; ++col) {
                        info.table.set(1 + info.size + info.depth, col, transformRow.get(col));
                    }

                    info.table.set(1 + info.size + info.depth, info.size, y.negate());
                }

                info.fixed.set(info.depth, new BigFraction(i));
                info.depth += 1;
                info.pool.search(info);
                info.depth -= 1;
            }
        }
    }

    static class SearchInfo {
        int size;
        int depth;

        BigMatrix transform;
        BigVector offset;
        BigVector fixed;
        BigMatrix table;

        ConcurrentLinkedQueue<BigVector> results;
        EnumeratePool pool;

        public SearchInfo copy() {
            SearchInfo copy = new SearchInfo();
            copy.size = this.size;
            copy.depth = this.depth;
            copy.transform = this.transform;
            copy.offset = this.offset;
            copy.fixed = this.fixed.copy();
            copy.table = this.table.copy();
            copy.results = this.results;
            copy.pool = this.pool;

            return copy;
        }
    }
}
