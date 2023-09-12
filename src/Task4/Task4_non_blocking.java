package Task4; // Не блокирующий

import mpi.*;
import java.util.Random;

public class Task4_non_blocking {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Размер векторов
        int vectorSize = 500000;

        // Создаем векторы a и b
        int[] a = new int[vectorSize];
        int[] b = new int[vectorSize];

        Random random = new Random(); // Создаем генератор случайных чисел

        if (rank == 0) {
            // Инициализация векторов a и b на мастере
            for (int i = 0; i < vectorSize; i++) {
                a[i] = random.nextInt(100);
                b[i] = random.nextInt(100);
            }
        }
        long startTime = System.currentTimeMillis();

        // Вычисляю часть скалярного произведения для каждого процесса
        int localResult = 0;
        for (int i = 0; i < vectorSize; i++) {
            localResult += a[i] * b[i];
        }

        // Создаю массив для сбора результатов от всех процессов
        int[] globalResults = new int[size];

        // Отправляю локальные результаты на все процессы
        Request[] sendRequests = new Request[size - 1];
        int requestIndex = 0;
        for (int i = 0; i < size; i++) {
            if (i != rank) {
                sendRequests[requestIndex] = MPI.COMM_WORLD.Isend(new int[]{localResult}, 0, 1, MPI.INT, i, 0);
                requestIndex++;
            }
        }

        // Принимаем результаты от всех процессов
        Request[] recvRequests = new Request[size - 1];
        requestIndex = 0;
        for (int i = 0; i < size; i++) {
            if (i != rank) {
                recvRequests[requestIndex] = MPI.COMM_WORLD.Irecv(globalResults, i, 1, MPI.INT, i, 0);
                requestIndex++;
            }
        }

        // Завершаем отправку и прием всех сообщений
        Request.Waitall(sendRequests);
        Request.Waitall(recvRequests);

        // Процесс 0 суммирует результаты
        if (rank == 0) {
            int totalResult = localResult;
            for (int i = 0; i < size; i++) {
                if (i != rank) {
                    totalResult += globalResults[i];
                }
            }
            System.out.println("Скалярное произведение: " + totalResult);

            long endTime = System.currentTimeMillis(); // Засекаем конечное время
            long elapsedTime = endTime - startTime;
            System.out.println("Время выполнения программы: " + elapsedTime + " миллисекунд");
        }

        MPI.Finalize();
    }
}
