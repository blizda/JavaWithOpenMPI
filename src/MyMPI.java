import mpi.*;

import java.io.*;
import java.math.BigInteger;

public class MyMPI {
    public static void main(String args[]) throws MPIException, IOException, ClassNotFoundException {
        MPI.Init(args);
        Comm comm = MPI.COMM_WORLD;
        int size = comm.getSize();
        int me = comm.getRank();
        int fact = 100;
        BigInteger finalResult = fact(me, size, fact);
        int iter = 1;
        int arrofInt[] = new int[1];
        while (true) {
            if (iter == size / 2){
                if((size - 1) == me){
                    int colInt[] = new int[1];
                    comm.recv(colInt, 1, MPI.INT, me - 2, 1);
                    byte resultArr[] = new byte[colInt[0]];
                    comm.recv(resultArr, colInt[0], MPI.BYTE, me - 2, 2);
                    BigInteger nowRes = deserial(resultArr);
                    finalResult = finalResult.multiply(nowRes);
                    System.out.println(finalResult);
                    break;
                }
                else{
                    byte arrofByte[] = serial(finalResult);
                    arrofInt[0] = arrofByte.length;
                    comm.send(arrofInt, 1, MPI.INT, me + 2, 1);
                    comm.send(arrofByte, arrofByte.length, MPI.BYTE, me + 2, 2);
                    break;
                }
            }
            if ((me % 2) == 0) {
                byte arrofByte[] = serial(finalResult);
                arrofInt[0] = arrofByte.length;
                comm.send(arrofInt, 1, MPI.INT, me + 1, 1);
                comm.send(arrofByte, arrofByte.length, MPI.BYTE, me + 1, 2);
                break;
            }
            else {
                int colInt[] = new int[1];
                comm.recv(colInt, 1, MPI.INT, me - 1, 1);
                byte resultArr[] = new byte[colInt[0]];
                comm.recv(resultArr, colInt[0], MPI.BYTE, me - 1, 2);
                BigInteger nowRes = deserial(resultArr);
                finalResult = finalResult.multiply(nowRes);
                iter += 1;
            }
        }
        MPI.Finalize();
    }

    public static BigInteger fact(int n, int size, int  fact){
        int l = (int) Math.ceil(fact / (double)size);
        BigInteger res = BigInteger.valueOf(1);
        if (n == 0){
            for (int i = 1; i < l; i++){
                res = res.multiply(BigInteger.valueOf(i));
            }
        }
        else if (n == (size - 1)){
            for (int i = n * l ; i <= fact; i++){
                res = res.multiply(BigInteger.valueOf(i));
            }
        }
        else {
            for (int i = n * l ; i < l * (n + 1); i++){
                res = res.multiply(BigInteger.valueOf(i));
            }
        }
        return res;
    }

    public static byte[] serial(BigInteger b) throws IOException {
        ByteArrayOutputStream bys = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bys);
        oos.writeObject(b);
        return bys.toByteArray();
    }

    public static BigInteger deserial(byte[] a) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bys = new ByteArrayInputStream(a);
        ObjectInputStream ois = new ObjectInputStream(bys);
        return (BigInteger) ois.readObject();
    }
}
