import java.util.Scanner;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        MatrixInverseParallel matrixInverseParallel = new MatrixInverseParallel();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите размерность матрицы: ");
        int matrixSize = scanner.nextInt();
        System.out.println("Сгенерированная Квадратная матрица с порядком - " + matrixSize);

        double [][] matrixInArray = matrixInverseParallel.generateMatrix(matrixSize);
        matrixInverseParallel.printMatrix(matrixInArray);
        double [][] identityMatrix = matrixInverseParallel.getInverseMatrix(matrixInArray);
        System.out.println("Обратная матрица:");
        matrixInverseParallel.printMatrix(identityMatrix);
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Время выполнения: " + executionTime + " мс");
    }

}

class MatrixInverseParallel {

    public static void main() {
    }

    public static double[][] getInverseMatrix(double [][] matrix) {
        double determinant = calculateDeterminant(matrix);
        if(determinant == 0){
            System.out.println("Детерминант матрицы равняется " + determinant + ". Попробуйте снова.");
            System.exit(0);
        }

        int size = matrix.length;
        // Инициализация единичной матрицы
        double[][] identityMatrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                identityMatrix[i][j] = (i == j) ? 1 : 0;
            }
        }
        // Создание пула потоков для параллельной обработки
        ExecutorService executorService = Executors.newFixedThreadPool(size);

        // Преобразование матрицы в верхнетреугольную форму (метод Жордана-Гаусса)
        for (int k = 0; k < size; k++) {
            final int index = k;
            executorService.execute(() -> {
                double pivot = matrix[index][index];
                for (int j = 0; j < size; j++) {
                    matrix[index][j] /= pivot;
                    identityMatrix[index][j] /= pivot;
                }

                for (int i = 0; i < size; i++) {
                    if (i != index) {
                        double factor = matrix[i][index];
                        for (int j = 0; j < size; j++) {
                            matrix[i][j] -= factor * matrix[index][j];
                            identityMatrix[i][j] -= factor * identityMatrix[index][j];
                        }
                    }
                }
            });
        }

        // Завершение работы потоков
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.yield();
        }
        return identityMatrix;
    }

    public static double calculateDeterminant(double[][] matrix) {
        int n = matrix.length;

        if (n == 1) {
            return matrix[0][0];
        } else if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else {
            double determinant = 0;

            for (int i = 0; i < n; i++) {
                determinant += matrix[0][i] * getCofactor(matrix, 0, i);
            }

            return determinant;
        }
    }

    public static double getCofactor(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] subMatrix = new double[n - 1][n - 1];
        int subRow = 0;
        int subCol = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != row && j != col) {
                    subMatrix[subRow][subCol] = matrix[i][j];
                    subCol++;

                    if (subCol == n - 1) {
                        subCol = 0;
                        subRow++;
                    }
                }
            }
        }

        return Math.pow(-1, row + col) * calculateDeterminant(subMatrix);
    }

//Метод генерации матрица от -99 до 99
    public static double[][] generateMatrix(int size) {
        double[][] matrix = new double[size][size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(199) - 99;
            }
        }
        return matrix;
    }
// Метод вывода матрицы
    public static void printMatrix(double [][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(Math.round(matrix[i][j]*100.0)/100.0 + "\t");
            }
            System.out.println();
        }
    }
}