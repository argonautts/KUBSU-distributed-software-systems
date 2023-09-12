package Task3;

import mpi.*; // Импортируем библиотеку MPI для параллельных вычислений
import java.util.Arrays; // Импортируем библиотеку для работы с массивами

public class Task3_1 {
    public static void main(String[] args) {
        int[] data = new int[1]; // Создаем массив 'data' для передачи данных
        int[] buf = {1,3,5};
        int count, TAG = 0;
        Status st; // Создаем объект 'st' для хранения информации о статусе сообщения

        data[0] = 2023;

        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();

        if(rank == 0)
        {
            MPI.COMM_WORLD.Send(data, 0, 1, MPI.INT, 2, TAG); // Отправляем данные процессу с номером 2
        }
        else if(rank == 1) // Если текущий процесс имеет номер 1
        {
            MPI.COMM_WORLD.Send(buf, 0, buf.length, MPI.INT, 2, TAG); // Отправляем данные процессу с номером 2
        }
        else if(rank == 2)
        {
            st = MPI.COMM_WORLD.Probe(0, TAG); // Проверяем наличие сообщения от процесса 0
            count = st.Get_count(MPI.INT); // Получаем количество элементов в сообщении

            int[] back_buf = new int[count]; // Создаем массив для приема данных
            MPI.COMM_WORLD.Recv(back_buf, 0, count, MPI.INT, 0, TAG); // Принимаем данные от процесса 0

            System.out.print("Rank = 0 ");
            print(back_buf);

            st = MPI.COMM_WORLD.Probe(1, TAG); // Проверяем наличие сообщения от процесса 1
            count = st.Get_count(MPI.INT); // Получаем количество элементов в сообщении

            int[] back_buf2 = new int[count];
            MPI.COMM_WORLD.Recv(back_buf2, 0, count, MPI.INT, 1, TAG); // Принимаем данные от процесса 1

            System.out.print("Rank = 1 ");
            print(back_buf2);
        }

        MPI.Finalize(); // Завершаем работу MPI
    }

    public static void print(int[] arr) {
        System.out.println(Arrays.toString(arr));
    }
}
