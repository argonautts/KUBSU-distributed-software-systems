package Task4; // Блокирующий
import mpi.*;

import java.util.Random;

public class Task4_Reduce {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int N = 50000; // Размерность векторов
        int[] A = new int[N];
        int[] B = new int[N];

        Random random = new Random();

        if (rank == 0) {
            for (int i = 0; i < N; i++) {
                A[i] = random.nextInt(100);
                B[i] = random.nextInt(100);
            }
        }

        int[] localResult = new int[1];
        localResult[0] = 0;

        long startTime = System.currentTimeMillis();

        for (int i = rank; i < N; i += size) {
            localResult[0] += A[i] * B[i];
        }

        int[] globalResult = new int[1];

        // Суммируем локальные результаты с помощью Reduce
        MPI.COMM_WORLD.Reduce(localResult, 0, globalResult, 0, 1, MPI.INT, MPI.SUM, 0);

        if (rank == 0) {
            System.out.println("Скалярное произведение векторов A и B: " + globalResult[0]);

            long endTime = System.currentTimeMillis(); // Засекаем конечное время
            long elapsedTime = endTime - startTime;
            System.out.println("Время выполнения программы: " + elapsedTime + " миллисекунд");
        }

        MPI.Finalize();
    }
}
