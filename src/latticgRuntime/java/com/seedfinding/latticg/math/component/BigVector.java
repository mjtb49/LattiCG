package com.seedfinding.latticg.math.component;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;

/**
 * A vector with {@link BigFraction} elements
 */
public final class BigVector {
    public static final BigVector LOG_TABLE = staticLogTable();

    BigFraction[] numbers;
    // to support views over a backing array, for internal use by BigMatrix
    int startPos = 0;
    int step = 1;
    private int dimension;

    /**
     * Constructs the zero vector of the given dimension
     *
     * @param dimension The dimension of the vector to create
     */
    public BigVector(int dimension) {
        this.dimension = dimension;
        this.numbers = new BigFraction[this.dimension];
        Arrays.fill(numbers, BigFraction.ZERO);
    }

    /**
     * Constructs a vector with the given integer elements
     *
     * @param numbers The elements of the vector
     */
    public BigVector(long... numbers) {
        this(toBigFractions(numbers));
    }

    /**
     * Constructs a vector with the given elements
     *
     * @param numbers The elements of the vector
     */
    public BigVector(BigFraction... numbers) {
        this.dimension = numbers.length;
        this.numbers = numbers;
    }

    private static BigFraction[] toBigFractions(long[] numbers) {
        BigFraction[] fractions = new BigFraction[numbers.length];
        for (int i = 0; i < numbers.length; i++) {
            fractions[i] = new BigFraction(numbers[i]);
        }
        return fractions;
    }

    // for internal use by BigMatrix
    static BigVector createView(BigFraction[] array, int dimension, int startPos, int step) {
        BigVector vec = new BigVector(array);
        vec.dimension = dimension;
        vec.startPos = startPos;
        vec.step = step;
        return vec;
    }

    /**
     * Gets the dimension of the vector
     *
     * @return The dimension of the vector
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Gets the element at the given index in the vector
     *
     * @param i The index
     * @return The element at the given index
     * @throws IndexOutOfBoundsException If {@code i} is out of bounds
     */
    public BigFraction get(int i) {
        if (i < 0 || i >= dimension) {
            throw new IndexOutOfBoundsException("Index " + i + ", dimension " + dimension);
        }
        return this.numbers[step * i + startPos];
    }

    /**
     * Sets the element at the given index in the vector
     *
     * @param i     The index
     * @param value The value to put in that index
     * @throws IndexOutOfBoundsException If {@code i} is out of bounds
     */
    public void set(int i, BigFraction value) {
        if (i < 0 || i >= dimension) {
            throw new IndexOutOfBoundsException("Index " + i + ", dimension " + dimension);
        }
        this.numbers[step * i + startPos] = value;
    }

    /**
     * Returns the square of the magnitude (norm) of this vector.
     * Note: there is no {@code magnitude()} method in {@code BigVector}, as that can result in an irrational number.
     * If the un-squared magnitude is desired, the caller should convert the result to a decimal and square root it
     * themselves.
     *
     * @return The square of the magnitude of this vector
     */
    public BigFraction magnitudeSq() {
        BigFraction magnitude = BigFraction.ZERO;

        for (int i = 0; i < this.getDimension(); i++) {
            magnitude = magnitude.add(this.get(i).multiply(this.get(i)));
        }

        return magnitude;
    }

    /**
     * Returns whether this vector is the zero vector
     *
     * @return Whether this vector is the zero vector
     */
    public boolean isZero() {
        for (int i = 0; i < this.getDimension(); i++) {
            if (this.get(i).signum() != 0) return false;
        }

        return true;
    }

    /**
     * Adds the given vector to this vector, stores the result in a new vector and returns that vector
     *
     * @param a The vector to add
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector add(BigVector a) {
        return copy().addAndSet(a);
    }

    /**
     * Subtracts the given vector from this vector, stores the result in a new vector and returns that vector
     *
     * @param a The vector to subtract
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector subtract(BigVector a) {
        return copy().subtractAndSet(a);
    }

    /**
     * Multiplies this vector by the given scalar, stores the result in a new vector and returns that vector
     *
     * @param scalar The scalar to multiply by
     * @return A new vector containing the result
     */
    public BigVector multiply(BigFraction scalar) {
        return copy().multiplyAndSet(scalar);
    }


    /**
     * Multiplies this vector by the given scalar, stores the result in a new vector and returns that vector
     *
     * @param scalar The scalar to multiply by
     * @return A new vector containing the result
     */
    public BigVector multiply(BigInteger scalar) {
        return copy().multiplyAndSet(scalar);
    }

    /**
     * Computes {@code this * m}, interpreting this vector as a row vector, stores the result in a new vector and
     * returns that vector
     *
     * @param m The matrix to right-multiply by
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of this vector is not equal to the number of rows in the given
     *                                  matrix
     */
    public BigVector multiply(BigMatrix m) {
        if (this.getDimension() != m.getRowCount()) {
            throw new IllegalArgumentException("Vector dimension should equal the number of matrix rows");
        }

        BigVector v = new BigVector(m.getColumnCount());

        for (int i = 0; i < v.getDimension(); i++) {
            v.set(i, this.dot(m.getColumn(i)));
        }

        return v;
    }

    /**
     * Divides this vector by the given scalar, stores the result in a new vector and returns that vector
     *
     * @param scalar The scalar to divide by
     * @return A new vector containing the result
     */
    public BigVector divide(BigFraction scalar) {
        return copy().divideAndSet(scalar);
    }

    /**
     * Swaps the two numbers at the given indices, stores the result in a new vector and returns that matrix
     *
     * @param i The row to swap with {@code j}
     * @param j The row to swap with {@code i}
     * @return A new vector containing the result
     * @throws IndexOutOfBoundsException If {@code i} or {@code j} is out of bounds
     */
    public BigVector swapNums(int i, int j) {
        return copy().swapNumsAndSet(i, j);
    }

    /**
     * Adds the given vector to this vector, modifying this vector
     *
     * @param a The vector to add to this vector
     * @return This vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector addAndSet(BigVector a) {
        assertSameDimension(a);

        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).add(a.get(i)));
        }

        return this;
    }

    /**
     * Subtracts the given vector from this vector, modifying this vector
     *
     * @param a The vector to subtract from this vector
     * @return This vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector subtractAndSet(BigVector a) {
        assertSameDimension(a);

        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).subtract(a.get(i)));
        }

        return this;
    }

    /**
     * Place the element at endIndex before the one at startIndex and shifts all the elements in between
     *
     * @param startIndex The starting index to swap
     * @param endIndex   The ending index to swap
     * @return This matrix
     * @throws IllegalArgumentException If {@code startIndex} is greater than {@code endIndex}
     */
    public BigVector shiftElements(int startIndex, int endIndex) {
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("The ending index should be greater or equals to the starting one");
        }
        if (startIndex == endIndex) {
            return this;
        }
        BigFraction last = this.get(endIndex);
        for (int row = endIndex; row > startIndex; row--) {
            this.set(row, this.get(row - 1));
        }
        this.set(startIndex, last);

        return this;
    }

    /**
     * Multiplies this vector by the given scalar, modifying this vector
     *
     * @param scalar The scalar to multiply this vector by
     * @return This vector
     */
    public BigVector multiplyAndSet(BigFraction scalar) {
        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).multiply(scalar));
        }

        return this;
    }


    /**
     * Multiplies this vector by the given scalar, modifying this vector
     *
     * @param scalar The scalar to multiply this vector by
     * @return This vector
     */
    public BigVector multiplyAndSet(BigInteger scalar) {
        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).multiply(scalar));
        }
        return this;
    }

    /**
     * Divides this vector by the given scalar, modifying this vector
     *
     * @param scalar The scalar to divide this vector by
     * @return This vector
     */
    public BigVector divideAndSet(BigFraction scalar) {
        for (int i = 0; i < this.getDimension(); i++) {
            this.set(i, this.get(i).divide(scalar));
        }

        return this;
    }

    /**
     * Swaps the two numbers at the given indices, modifying the vector
     *
     * @param i The row to swap with {@code j}
     * @param j The row to swap with {@code i}
     * @return This vector
     * @throws IndexOutOfBoundsException If {@code i} or {@code j} is out of bounds
     */
    public BigVector swapNumsAndSet(int i, int j) {
        BigFraction temp = this.get(i);
        this.set(i, this.get(j));
        this.set(j, temp);
        return this;
    }

    /**
     * Calculates the dot product of this vector and the given vector
     *
     * @param v The vector to find the dot product with
     * @return The dot product of this vector and the given vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigFraction dot(BigVector v) {
        assertSameDimension(v);

        BigFraction dot = BigFraction.ZERO;

        for (int i = 0; i < this.getDimension(); i++) {
            dot = dot.add(this.get(i).multiply(v.get(i)));
        }

        return dot;
    }

    /**
     * Returns the Gram-Schmidt coefficient when this vector is projected onto the given vector, that is, the ratio of
     * the length of the projection of this vector onto the given vector, to the given vector's length. In other words,
     * multiplying the given vector by the Gram-Schmidt coefficient gives the projection of this vector onto the given
     * vector
     *
     * @param v The vector to project onto
     * @return The Gram-Schmidt coefficient when this vector is projected onto the given vector
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigFraction gramSchmidtCoefficient(BigVector v) {
        return this.dot(v).divide(v.magnitudeSq());
    }

    /**
     * Calculates the projection of this vector onto the given vector, stores the result in a new vector and returns
     * that vector
     *
     * @param v The vector to project onto
     * @return A new vector containing the result
     * @throws IllegalArgumentException If the dimension of the given vector is not the same as the dimension of this
     *                                  vector
     */
    public BigVector projectOnto(BigVector v) {
        return v.multiply(this.gramSchmidtCoefficient(v));
    }

    /**
     * Creates a copy of this vector
     *
     * @return A copy of this vector
     */
    public BigVector copy() {
        if (step == 1) {
            return new BigVector(Arrays.copyOfRange(numbers, startPos, startPos + dimension));
        }

        BigVector v = new BigVector(this.getDimension());
        for (int i = 0; i < v.getDimension(); i++) {
            v.set(i, this.get(i));
        }
        return v;
    }

    private void assertSameDimension(BigVector other) {
        if (other.dimension != this.dimension) {
            throw new IllegalArgumentException("The other vector is not the same dimension");
        }
    }

    @Override
    public int hashCode() {
        int h = 0;
        for (int i = 0; i < dimension; i++) {
            h = 31 * h + get(i).hashCode();
        }
        return h;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != BigVector.class) return false;
        BigVector that = (BigVector) other;
        if (this.dimension != that.dimension) {
            return false;
        }
        for (int i = 0; i < dimension; i++) {
            if (!this.get(i).equals(that.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (int i = 0; i < this.getDimension(); i++) {
            sb.append(this.get(i)).append(i == this.getDimension() - 1 ? "" : ", ");
        }

        return sb.append("}").toString();
    }

    public String toApproximateString() {
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < this.getDimension(); i++) {
            sb.append(this.get(i).toBigDecimal(MathContext.UNLIMITED)).append(i == this.getDimension() - 1 ? "" : " ");
        }

        return sb.append("]").toString();
    }

    /**
     * Create a basis vector of length 1.
     *
     * @param size dimension of the vector
     * @param i    index of the 1
     * @return a basis vector
     */
    public static BigVector basis(int size, int i) {
        return basis(size, i, BigFraction.ONE);
    }

    /**
     * Create a basis vector of the specified length.
     *
     * @param size  dimension of the vector
     * @param i     index of the provided component
     * @param scale the length of the vector
     * @return a basis vector
     */
    public static BigVector basis(int size, int i, BigFraction scale) {
        BigVector vector = new BigVector(size);
        vector.set(i, scale);

        return vector;
    }

    private static BigVector staticLogTable() {
        return new BigVector(BigFraction.ZERO, new BigFraction(14282501006512111L, 20605293373586651L), new BigFraction(7721996864960839L, 7028864454376817L), new BigFraction(18947212500888319L, 13667524756850476L), new BigFraction(11490246797073147L, 7139291741733233L), new BigFraction(27600155808545969L, 15403940251219637L), new BigFraction(17374815494814916L, 8928888881765645L), new BigFraction(20502116332347055L, 9859433853468573L), new BigFraction(15443993729921678L, 7028864454376817L), new BigFraction(152469287047331902L, 66216570024379193L), new BigFraction(12775734666695301L, 5327895180253588L), new BigFraction(24193039801868281L, 9735995436260074L), new BigFraction(22924068231375809L, 8937435027591796L), new BigFraction(37008982245842416L, 14023561303701523L), new BigFraction(39526206520846803L, 14595817501743190L), new BigFraction(25166827826723263L, 9077014425130005L), new BigFraction(25253017326904331L, 8913207111593805L), new BigFraction(27532819812108053L, 9525701922907163L), new BigFraction(25746950425326692L, 8744263544770609L), new BigFraction(22487874113222690L, 7506636795198047L), new BigFraction(36090505322915715L, 11854241859325172L), new BigFraction(53622551616547946L, 17347724085215591L), new BigFraction(37113160649965908L, 11836462801117961L), new BigFraction(24976008177481085L, 7858900292682146L), new BigFraction(22980493594146294L, 7139291741733233L), new BigFraction(25317821444266631L, 7770740108161798L), new BigFraction(20762514840624566L, 6299618483786113L), new BigFraction(23634071281331821L, 7092623279622524L), new BigFraction(40883341689096899L, 12141297870244184L), new BigFraction(24625584617713869L, 7240269191810155L), new BigFraction(33578969914929451L, 9778420219816721L), new BigFraction(25166827826723263L, 7261611540104004L), new BigFraction(37793732024841266L, 10808994792789777L), new BigFraction(31400397035703483L, 8904477241197953L), new BigFraction(36546502684297245L, 10279303756546162L), new BigFraction(55949256641554669L, 15612937339647304L), new BigFraction(63076605912030180L, 17468302364658317L), new BigFraction(36482898058399802L, 10029425134261011L), new BigFraction(71842430160295012L, 19609996254926577L), new BigFraction(22956108088224064L, 6223057265431323L), new BigFraction(15569865111753239L, 4192692327517172L), new BigFraction(22882004199333731L, 6121997537557358L), new BigFraction(178786543983549443L, 47534440732776948L), new BigFraction(18695752129549890L, 4940490286738553L), new BigFraction(15830045660718842L, 4158510428297511L), new BigFraction(39621864367767604L, 10348805297905747L), new BigFraction(21552572125121241L, 5597856070647416L), new BigFraction(31548933778767740L, 8149650118883583L), new BigFraction(34749630989629832L, 8928888881765645L), new BigFraction(35514902455543013L, 9078398160303286L), new BigFraction(27650245631518325L, 7032419088320487L), new BigFraction(47408771338734661L, 11998442696861869L), new BigFraction(12388583489197979L, 3120320560538890L), new BigFraction(42658728056085091L, 10694133533280786L), new BigFraction(27460869679514567L, 6852654473731144L), new BigFraction(51975388483603957L, 12912011788493368L), new BigFraction(24211403591263179L, 5988398857042136L), new BigFraction(23142735495796205L, 5699559244074172L), new BigFraction(29096705010063551L, 7135852315360448L), new BigFraction(48869263694378727L, 11935796548558235L), new BigFraction(53734097455158251L, 13071210460495137L), new BigFraction(51257826382635351L, 12419713438062746L), new BigFraction(24703483363798753L, 5962510271857434L), new BigFraction(25166827826723263L, 6051342950086670L), new BigFraction(57691570598783912L, 13820368563989571L), new BigFraction(71167963992895363L, 16986593973725218L), new BigFraction(33457824377315073L, 7957258093734641L), new BigFraction(50101638187772865L, 11873811280475386L), new BigFraction(38311899425098843L, 9048402392216868L), new BigFraction(17881103827525977L, 4208808721391110L), new BigFraction(24823940090237947L, 5823552508350217L), new BigFraction(1258466288251003L, 294263394248916L), new BigFraction(109904636817790535L, 25616053088331558L), new BigFraction(8874718243267571L, 2061938667535525L), new BigFraction(35295986881402935L, 8175120800157379L), new BigFraction(52568090040060907L, 12138380710502326L), new BigFraction(13351184457591221L, 3073614759634816L), new BigFraction(45414675128454281L, 10424078572852948L), new BigFraction(37351395312307652L, 8548310123719359L), new BigFraction(38684334453160456L, 8827955117173635L), new BigFraction(28484511705585405L, 6481929976433789L), new BigFraction(35784004747778663L, 8120327785799793L), new BigFraction(32966176282123694L, 7460367822265009L), new BigFraction(9327322731568165L, 2105102321992439L), new BigFraction(37969321007774502L, 8546545478289505L), new BigFraction(25035186888797417L, 5620394015943521L), new BigFraction(42640820777268387L, 9548073906660337L), new BigFraction(54385428581940449L, 12146825408818073L), new BigFraction(31001376880525273L, 6906635852610909L), new BigFraction(75779770008787222L, 16840661174725939L), new BigFraction(52203984458580169L, 11572957300745222L), new BigFraction(83882612002395599L, 18550759411475656L), new BigFraction(30988075203067508L, 6836711527210097L), new BigFraction(32557422035215106L, 7166037775551991L), new BigFraction(36404043895529062L, 7994077302062115L), new BigFraction(10183409690782753L, 2231076434926385L), new BigFraction(54988600262830642L, 12020125538252073L), new BigFraction(34030981643803114L, 7422295098518457L), new BigFraction(39100402892202571L, 8509114923532915L), new BigFraction(315175181323719809L, 68439421040874173L));
    }
}
