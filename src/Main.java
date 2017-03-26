import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by William on 2015-05-21.
 */
public class Main {

    public static void main(String[] args) {
        try {
//            test(ProblemSets.easy, Algorithm.backtrack, "easy");
//            test(ProblemSets.medium, Algorithm.backtrack, "medium");
            test(ProblemSets.hard, Algorithm.backtrack, "hard");
            test(ProblemSets.evil, Algorithm.backtrack, "evil");
//            test(ProblemSets.easy, Algorithm.ForwardCheck, "easy");
//            test(ProblemSets.medium, Algorithm.ForwardCheck, "medium");
//            test(ProblemSets.hard, Algorithm.ForwardCheck, "hard");
//            test(ProblemSets.evil, Algorithm.ForwardCheck, "evil");
//            test(ProblemSets.easy, Algorithm.AStarSearch, "easy");
//            test(ProblemSets.medium, Algorithm.AStarSearch, "medium");
//            test(ProblemSets.hard, Algorithm.AStarSearch, "hard");
//            test(ProblemSets.evil, Algorithm.AStarSearch, "evil");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    static void test(int[][] level, Algorithm algorithm, String fileName) throws FileNotFoundException {

        PrintWriter printWriter = new PrintWriter(new File("data/" + algorithm + "_" + fileName + ".csv"));
        printWriter.println("count, time");

        int iter = 50;
        System.out.println("\nalgorithm: " + algorithm + "\n");
        for (int i = 0; i < iter; i ++) {
            long startTime = System.currentTimeMillis();
            Sudoku sudoku = new Sudoku(level);

            switch (algorithm) {
                case backtrack:
                    if (!sudoku.backtrack()) {
                        System.err.print("No solution found.\n");
                    }
                    break;
                case ForwardCheck:
                    if (!sudoku.forwardCheck()) {
                        System.err.print("No solution found.\n");
                    }
                    break;
                case AStarSearch:
                    if (!sudoku.aStarSearch()) {
                        System.err.print("No solution found.\n");
                    }
                    break;
            }
//            sudoku.printLatex();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            sudoku.print();
            printWriter.printf("%d, %d\n", sudoku.count, duration);
        }
        printWriter.close();

    }

    enum Algorithm {
        backtrack,
        ForwardCheck,
        AStarSearch
    }
}
