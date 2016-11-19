package external;


import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class generateData {
    
    static long a = 3L, c = 5L, m = 1125899906842679L;  
    static long X1 = 2016213617;
    static String fileName = "/home/cgf/data/testData16G";
    
    public static void main(String[] args) throws IOException {
        DataOutputStream outStream = new DataOutputStream(new FileOutputStream(fileName));
        outStream.writeLong(X1);
        for (long i = 2;i <= 1L<<31; i++) {
            X1 = (a*X1 + c) % m;
            outStream.writeLong(X1);
        }
        outStream.close();
    }
}

