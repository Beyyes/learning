package external;


import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

public class verifySort {
    static String fileName = "/home/cgf/data/ansout";
    static long a = 3L, c = 5L, m = 1125899906842679L;
    
    public static void main(String[] args) throws IOException {
        FileInputStream in = new FileInputStream(fileName);
        byte[] array = new byte[8];
        long H1 = -1L;
        while (in.available() != 0) {
        	in.read(array, 0, 8);
        	//System.out.println(bytesToLong(array));
        	if (H1 == -1L)
                H1 = bytesToLong(array);
            else {
                H1 = (a*H1+bytesToLong(array)) % m;
            }
        }
        System.out.println(H1);
        in.close();
    }
    
    public static long bytesToLong(byte[] bytes) {  
        ByteBuffer buffer = ByteBuffer.allocate(8); 
        buffer.put(bytes, 0, bytes.length);  
        buffer.flip();  //need flip   
        return buffer.getLong();  
    }
}
955861956440439
