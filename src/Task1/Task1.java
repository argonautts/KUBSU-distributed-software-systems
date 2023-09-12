package Task1;

import mpi.*;

public class Task1 {
    public static void main(String[] args) {

        // 1. Инициализация MPI
        MPI.Init(args);

        // 2. Определяем ранг процесса
        int rank = MPI.COMM_WORLD.Rank();

        // 3. Определяем количество процессов
        int size = MPI.COMM_WORLD.Size();

        // 4. Создаем массивы sendBuffer и receiveBuffer для отправки данных в другой процесс  и хранения данных из других процессов
        int[] sendBuffer = {rank};
        int[] receiveBuffer = new int[1];

        // 5. Делаем проверку ранга процесса
        if ((rank % 2) == 0) {
            // Если ранг процесса четный, пытаемся отправить сообщение следующему процессу (если он существует)
            if((rank + 1) != size) {
                MPI.COMM_WORLD.Send(sendBuffer, 0, 1, MPI.INT, rank + 1, 0);
            }
        } else {
            // Если ранг процесса нечетный, пытаемся принять сообщение от предыдущего процесса (если он существует)
            MPI.COMM_WORLD.Recv(receiveBuffer, 0, 1, MPI.INT, rank - 1, MPI.ANY_TAG);
            System.out.printf("Received: %d from %d to %d\n", receiveBuffer[0], rank - 1, rank);
        }

        // System.out.println("Hi from <" + rank + ">");

        // 6. Завершение работы с MPI
        MPI.Finalize();
    }

}
