import sun.util.resources.cldr.ebu.CurrencyNames_ebu;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by William on 2015-05-21.
 */
public class Sudoku {

    PriorityQueue<Grid> nodes;
    Stack<Grid> unfilled = new Stack<>();
    Stack<Grid> assignments = new Stack<>();
    Grid[][] grids = new Grid[9][9];
    int size;
    int count;

    public static List<Integer> getOneToNine() {
        List<Integer> oneToNine = IntStream
                .rangeClosed(1, 9)
                .boxed()
//            .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        Collections.shuffle(oneToNine);
        return oneToNine;
    }

    /**
     * should use heapify instead
     */
    void updateNodes() {
        PriorityQueue<Grid> updated = new PriorityQueue<>();
        nodes.forEach(g -> updated.offer(g));
        nodes = updated;
    }

    boolean aStarSearch() {
        if (assignments.size() == size) {
            return true;
        }
        Grid current = nodes.poll();
        for (int i : current.getValuesByConstrainingCount()) {
            current.setValue(i);
            if (current.isLegal()) {
                if (current.hasLegalValues(i)) {
                    assignments.push(current);
                    updateNodes();
                    count ++;
                    if(aStarSearch()) {
                        return true;
                    }
                    assignments.pop();
                }
            }
            current.setValue(0);
        }
        nodes.offer(current);
        return false;
    }

    boolean forwardCheck() {
        if (assignments.size() == size) {
            return true;
        }
        Grid current = unfilled.pop();
        for (int i : getOneToNine()) {
            current.setValue(i);
            if (current.isLegal()) {
                if (current.hasLegalValues(i)) {
                    assignments.push(current);
                    count ++;
                    if(forwardCheck()) {
                        return true;
                    }
                    assignments.pop();
                }
            }
            current.setValue(0);
        }
        unfilled.push(current);
        return false;
    }

    boolean backtrack() {
        if (assignments.size() == size) {
            return true;
        }
        Grid current = unfilled.pop();
        for (int i : getOneToNine()) {
            current.setValue(i);
            if (current.isLegal()) {
                assignments.push(current);
                count ++;
                if(backtrack()) {
                    return true;
                }
                assignments.pop();
            }
            current.setValue(0);
        }
        unfilled.push(current);
        return false;
    }

    void print() {
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                System.out.print(grids[i][j] + " ");
            }
            System.out.println();
        }
        System.out.print("\n\n");
    }

    void printLatex() {
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                Grid g = grids[i][j];
                if (g.isFixed()) {
                    System.out.printf("%d ", g.getValue());
                } else {
                    System.out.printf("\\textcolor{blue}{%d} ", g.getValue());
                }
                if (j < 8) {
                    System.out.print(" & ");
                }
            }
            System.out.println("\\\\ \n\\hline");
        }
        System.out.print("\n\n");
    }

    public Sudoku(int[][] problem) {

        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j ++) {
                Grid grid = new Grid(i, j);
                grids[i][j] = grid;
                if (problem[i][j] == 0) {
                    unfilled.add(grid);
                } else {
                    grid.setIsFixed(true);
                    grid.setValue(problem[i][j]);
                }
            }
        }
        Collections.shuffle(unfilled);
        size = unfilled.size();

        // link with neighbors
        linkWithNeighbors();

        // A* search only
        nodes = new PriorityQueue<>(unfilled);
    }

    void linkWithNeighbors() {
        for (int i = 0; i < 3; i ++) {
            for (int j = 0; j < 3; j ++) {
                Grid[][] subsquare = new Grid[3][3];
                for (int k = 0; k < 3; k ++) {
                    for (int l = 0; l < 3; l ++) {
                        grids[i * 3 + k][j * 3 + l].setSubsquare(subsquare);
                        subsquare[k][l] = grids[i * 3 + k][j * 3 + l];
                    }
                }
            }
        }
        for (int i = 0; i < 9; i ++) {
            Grid[] row = new Grid[9];
            Grid[] column = new Grid[9];
            for (int j = 0; j < 9; j ++) {
                grids[i][j].setRow(row);
                row[j] = grids[i][j];
                grids[j][i].setColumn(column);
                column[j] = grids[j][i];
            }
        }
    }

}
