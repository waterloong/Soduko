options("scipen" = 10)
setwd("~/Google Drive/UW/CS486/Assignments/assignment 1/Sudoku/data/")
result <- data.frame(avgNodes = numeric(), stdNodes = numeric(),
                     avgTime = numeric(), stdTime = numeric())
for(f in list.files()) {
    record <- read.csv(f)
    result[f,] <- c(mean(record$count), sd(record$count), mean(record$time), sd(record$time))
}
result