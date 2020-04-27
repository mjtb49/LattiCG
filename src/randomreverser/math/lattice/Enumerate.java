package randomreverser.math.lattice;

import randomreverser.math.component.*;

import java.math.BigInteger;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.LongStream;

public class Enumerate {
    public static LongStream enumerate(BigMatrix basis, BigVector lower, BigVector upper, BigVector offset, int threads) {
        SearchInfo root = new SearchInfo();
        root.size = basis.getRowCount();
        root.depth = 0;
        root.transform = basis.inverse();
        root.offset = root.transform.multiply(lower.subtract(offset));
        root.fixed = new BigVector(root.size);
        root.table = new BigMatrix(2 * root.size + 1, root.size + 1);
        root.reverseTransform = basis.getRow(0).copy();
        root.reverseOffset = BigFraction.ZERO; //offset.get(0);
        root.results = new ConcurrentLinkedQueue<>();
        root.pool = new EnumeratePool(threads);

        for (int i = 0; i < root.size; ++i) {
            root.table.set(i + 1, i, BigFraction.ONE);
            root.table.set(i + 1, root.size, upper.get(i).subtract(lower.get(i)));
        }

        root.pool.start(root);

        return root.results.stream().mapToLong(Long::longValue);
    }

    public static LongStream enumerate(BigMatrix basis, BigVector lower, BigVector upper, BigVector offset) {
        return enumerate(basis, lower, upper, offset, Runtime.getRuntime().availableProcessors());
    }

    static void search(SearchInfo info) {
        if (info.depth == info.size) {
            info.results.offer(info.reverseOffset.add(info.reverseTransform.dot(info.fixed)).getNumerator().longValue());
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

        BigVector reverseTransform;
        BigFraction reverseOffset;

        ConcurrentLinkedQueue<Long> results;
        EnumeratePool pool;

        public SearchInfo copy() {
            SearchInfo copy = new SearchInfo();
            copy.size = this.size;
            copy.depth = this.depth;
            copy.transform = this.transform;
            copy.offset = this.offset;
            copy.fixed = this.fixed.copy();
            copy.table = this.table.copy();
            copy.reverseTransform = this.reverseTransform;
            copy.reverseOffset = this.reverseOffset;
            copy.results = this.results;
            copy.pool = this.pool;

            return copy;
        }
    }
}
