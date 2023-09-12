package Task4; // Блокирующий

import mpi.*;
import java.util.Random;

public class Task4_ScatGath {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int N = 500000;
        int localN = N / size;
        int[] a = new int[N];
        int[] b = new int[N];

        Random random = new Random();

        if (rank == 0) {
            for (int i = 0; i < N; i++) {
                a[i] = random.nextInt(100);
                b[i] = random.nextInt(100);
            }
        }

        long startTime = System.currentTimeMillis();

        int[] localA = new int[localN];
        int[] localB = new int[localN];
        MPI.COMM_WORLD.Scatter(a, 0, localN, MPI.INT, localA, 0, localN, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(b, 0, localN, MPI.INT, localB, 0, localN, MPI.INT, 0);

        int localResult = 0;
        for (int i = 0; i < localN; i++) {
            localResult += localA[i] * localB[i];
        }

        // Синхронизация всех процессов
        MPI.COMM_WORLD.Barrier();

        // Сбор результатов на мастере
        int[] results = new int[size];
        MPI.COMM_WORLD.Gather(new int[]{localResult}, 0, 1, MPI.INT, results, 0, 1, MPI.INT, 0);

        if (rank == 0) {
            int scalarProduct = 0;
            for (int result : results) {
                scalarProduct += result;
            }
            System.out.println("Скалярное произведение: " + scalarProduct);

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            System.out.println("Время выполнения программы: " + elapsedTime + " миллисекунд");
        }

        MPI.Finalize();
    }
}
