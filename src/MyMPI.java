import mpi.*;
import java.math.BigInteger;

public class MyMPI {
    public static void main(String args[]) throws MPIException {
        MPI.Init(args);
        Comm comm = MPI.COMM_WORLD;
        int size = comm.getSize();
        int me = comm.getRank();
        int fact = 102;
        BigInteger finalResult = BigInteger.valueOf(1);
        finalResult = fact(me, size, fact);
        int iter = 1;
        Character character = Character.MIN_VALUE;
        while (true) {
            if (iter == size / 2){
                if((size - 1) == me){
                    char res[] = new char[10000];
                    Status status = comm.recv(res, 10000, MPI.CHAR, me - 2, 0);
                    StringBuilder sb = new StringBuilder();
                    for (char aResive : res) {
                        if (aResive != character)
                            sb.append(aResive);
                    }
                    finalResult = finalResult.multiply(new BigInteger(sb.toString()));
                    System.out.println(finalResult);
                    break;
                }
                else{
                    String charInt = finalResult.toString();
                    comm.send(charInt.toCharArray(), charInt.length(), MPI.CHAR, me + 2, 0);
                    break;
                }
            }
            if ((me % 2) == 0) {
                String charInt = finalResult.toString();
                comm.send(charInt.toCharArray(), charInt.length(), MPI.CHAR, me + 1, 0);
                break;
            }
            else {
                char res[] = new char[10000];
                Status status = comm.recv(res, 10000, MPI.CHAR, me - 1, 0);
                StringBuilder sb = new StringBuilder();
                for (char re : res) {
                    if (re != character)
                        sb.append(re);
                }
                finalResult = finalResult.multiply(new BigInteger(sb.toString()));
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
}
