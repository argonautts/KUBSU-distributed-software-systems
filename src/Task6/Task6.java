package Task6;

import mpi.*;
import java.util.Random;

public class Task6 {

    public static void main(String[] args) throws MPIException {
        long startTime = System.currentTimeMillis();
        MPI.Init(args);

        int[][] adjacencyMatrix = new int[5000][5000];
        Random random = new Random();

        for (int i = 0; i < 5000; i++) {
            for (int j = 0; j < 5000; j++) {
                adjacencyMatrix[i][j] = random.nextInt(2);
            }
        }

        int numNodes = MPI.COMM_WORLD.Size();
        int myRank = MPI.COMM_WORLD.Rank();

        // Проверка регулярности графа
        boolean isRegular = isRegularGraph(adjacencyMatrix, numNodes, myRank);

        if (myRank == 0) {
            if (isRegular) {
                System.out.println("Граф является регулярным.");
            } else {
                System.out.println("Граф не является регулярным.");
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Время выполнения: " + (endTime - startTime) + " миллисекунд");
        }

        MPI.Finalize();
    }

    public static boolean isRegularGraph(int[][] adjacencyMatrix, int numNodes, int myRank) throws MPIException {
        int[] degreeCounts = new int[adjacencyMatrix.length];
        int[] localDegreeCounts = new int[adjacencyMatrix.length];

        // Рассылаю локальные степени вершин асинхронно
        Request[] sendRequests = new Request[numNodes];
        Request[] recvRequests = new Request[numNodes];

        for (int i = 0; i < numNodes; i++) {
            sendRequests[i] = MPI.COMM_WORLD.Isend(localDegreeCounts, myRank, 1, MPI.INT, i, 0);
            recvRequests[i] = MPI.COMM_WORLD.Irecv(degreeCounts, i, 1, MPI.INT, i, 0);
        }

        // Ожидаем завершения отправки и приема сообщений
        Status[] statuses = new Status[numNodes];
        Request.Waitall(sendRequests);
        Request.Waitall(recvRequests);

        // Проверка являются ли все степени вершин одинаковыми
        int globalDegreeSum = 0;
        for (int i = 0; i < degreeCounts.length; i++) {
            globalDegreeSum += degreeCounts[i];
        }

        int localDegreeSum = 0;
        for (int i = myRank; i < adjacencyMatrix.length; i += numNodes) {
            localDegreeSum += localDegreeCounts[i];
        }

        int expectedDegreeSum = globalDegreeSum / numNodes;

        return localDegreeSum == expectedDegreeSum;
    }
}
