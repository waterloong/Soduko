import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by William on 2015-05-21.
 */
public class Grid implements Comparable<Grid>{

    private int x;
    private int y;

    private Grid[] row;
    private Grid[] column;
    private Grid[][] subsquare;
    private List<Grid> neighbors;

    private int value;
    private boolean isFixed;

    public Grid(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public List<Grid> getNeighbors() {
        if (neighbors == null) {
            Stream.Builder<Grid> builder = Stream.builder();
            for (int i = 0; i < 9; i++) {
                builder.accept(row[i]);
                builder.accept(column[i]);
                builder.accept(subsquare[i / 3][i % 3]);
            }
            neighbors = builder.build().distinct().collect(Collectors.toList());
        }
        return neighbors;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isRepeated(Grid g) {
        return g != this && g.getValue() == this.getValue();
    }

    public boolean isLegal() {
        return getNeighbors().stream().noneMatch(this::isRepeated);
    }

    @Override
    public String toString() {
        return "" + getValue();
    }

    /**
     * Forward checking
     * @param value
     * @return false if any neighbor wont be able to a valid value after this grid is assigned value
     */
    public boolean hasLegalValues(int value) {
        return getNeighbors().stream().allMatch(g -> g.hasAvailable(value));
    }

    /**
     * forward checking:
     * not using stream.match because we need to modify "used", not using stream.reduce because we need to stop early if all values are used
     * @param value the value that a neighbor is about to be assigned with
     * @return whether this grid can still have legal values, after the neighbor assigns this value
     */
    public boolean hasAvailable(int value) {
        if (this.value > 0) return true;
        int used = 1 << (value - 1);
        List<Grid> neighbors = getNeighbors();
        for (int i = 0; i < 21 && used < 0b111_111_111; i ++) {
            Grid g = neighbors.get(i);
            if (g.value > 0) {
                used |= (1 << (g.value - 1));
            }
        }
        return used != 0b111_111_111;
    }

    /**
     * "most constrained variable"
     * not using stream.match because we need to modify "used", not using stream.reduce because we need to stop early if all values are used
     * @return (9 - Number of constraints)
     */
    public int getAvailableCount() {
        int used = 0;
        for (Grid g : getNeighbors()) {
            if (g.value > 0) {
                used |= (1 << (g.value - 1));
                if (used == 0b111_111_111) return 0;
            }
        }
        return 9 - Integer.bitCount(used);
    }

    public int getConstrainingCount () {
        return getNeighbors().stream().reduce(
                0,
                (count, g) -> g.value == 0 ? count + 1 : count,
                (count1, count2) -> count1 + count2
        );
    }

    public int getConstrainingCount(int value) {
        return getNeighbors().stream().reduce(
                0,
                (count, g) -> {
                    if (g.value > 0 || g.getNeighbors().stream().anyMatch(n -> n.value == value)) return count;
                    return count + 1;
                },
                (c1, c2) -> c1 + c2);
    }

    /**
     * "Least Constraining Value"
     * @return
     */
    public List<Integer> getValuesByConstrainingCount() {
        return IntStream.rangeClosed(1, 9).boxed().sorted(
                (i1, i2) -> this.getConstrainingCount(i2) - getConstrainingCount(i1)
        ).collect(Collectors.toList());
    }

    public Grid[] getRow() {
        return row;
    }

    public void setRow(Grid[] row) {
        this.row = row;
    }

    public Grid[] getColumn() {
        return column;
    }

    public void setColumn(Grid[] column) {
        this.column = column;
    }

    public Grid[][] getSubsquare() {
        return subsquare;
    }

    public void setSubsquare(Grid[][] subsquare) {
        this.subsquare = subsquare;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    @Override
    /**
     * first compare by "most constrained variable", then compare by "most constraining variable"
     */
    public int compareTo(Grid g) {
        int count1 = this.getAvailableCount();
        int count2 = g.getAvailableCount();
        if ( count1 != count2) {
            return count1 - count2;
        }
        return this.getConstrainingCount() - g.getConstrainingCount();
    }
}
