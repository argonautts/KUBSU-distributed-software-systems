package Task3;

import mpi.MPI;
import mpi.Request;

import java.util.Arrays;
import java.util.Random;

public class Task3 {// np -10
    public static void main(String[] args) {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int SIZE = 100;
        int[] arrayToSort = new Random().ints(SIZE, 0, SIZE).toArray(); // Создание случайного массива для сортировки

        if (rank == 0) {
            var result = trySplit(arrayToSort, rank + 1, size); // сортировки и объединения
            System.err.println("Result is: " + Arrays.toString(result));
        } else {
            int[] buffer = new int[SIZE]; // буфера для приема данных
            var status = MPI.COMM_WORLD.Recv(buffer, 0, SIZE, MPI.INT, MPI.ANY_SOURCE, MPI.ANY_TAG); // Получение данных
            var tag = status.tag;
            var source = status.source; // Получение источника сообщения
            int[] receivedBuffer = Arrays.copyOfRange(buffer, 0, tag); // Создание массива из полученных данных
            var result = trySplit(receivedBuffer, rank, size); // Вызов функции для сортировки и объединения
            MPI.COMM_WORLD.Send(result, 0, result.length, MPI.INT, source, tag); // Отправка результата
        }
        MPI.Finalize();
    }

    private static int[] trySplit(int[] source, int rank, int size) {
        if (rank * 2 > size || rank * 2 + 1 > size) { // Если текущий процесс не может разделить массив
            Arrays.sort(source); // Простая сортировка массива
            System.err.println("* " + rank + " -> " + source.length);
            return source;
        }
        int[] a = Arrays.copyOfRange(source, 0, source.length / 2); // Разделение массива на две части
        var reqA = MPI.COMM_WORLD.Isend(a, 0, a.length, MPI.INT, rank * 2, a.length);

        int[] b = Arrays.copyOfRange(source, source.length / 2, source.length); // Получение второй половины
        var reqB = MPI.COMM_WORLD.Isend(b, 0, b.length, MPI.INT, rank * 2 + 1, b.length);

        Request.Waitall(new Request[] {reqA, reqB}); // Ожидание завершения отправки

        var respA = MPI.COMM_WORLD.Irecv(a, 0, a.length, MPI.INT, rank * 2, a.length); // Прием первой половины
        var respB = MPI.COMM_WORLD.Irecv(b, 0, b.length, MPI.INT, rank * 2 + 1, b.length); // Прием второй половины

        Request.Waitall(new Request[]{respA, respB}); // Ожидание завершения приема

        System.out.println("& " + rank + " -> " + (a.length + b.length)); // Вывод информации о процессе

        return merge(a, b); // Объединение и сортировка двух половин
    }

    private static int[] merge(int[] a, int[] b) {
        int[] c = new int[a.length + b.length]; // Создание массива для объединенных данных
        int ic = 0;
        int ia = 0;
        int ib = 0;
        while (ic != c.length) {
            if (ia == a.length) {
                c[ic] = b[ib];
                ib++;
            } else if (ib == b.length) {
                c[ic] = a[ia];
                ia++;
            } else {
                if (a[ia] > b[ib]) {
                    c[ic] = b[ib];
                    ib++;
                } else {
                    c[ic] = a[ia];
                    ia++;
                }
            }
            ic++;
        }
        return c; // Возвращение объединенного и отсортированного массива
    }
}
