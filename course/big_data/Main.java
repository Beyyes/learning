package algorithm_exp1;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 * @author CGF
 *
 */
public class Main {
    
    static int SIZE = 10;
    static String fileName = "data/small_data";
    //long[] tmp
            
    public static void main(String[] args) throws IOException {
        //BufferedReader reader = new BufferedReader(new FileReader("test100.txt"));
        //BufferedWriter writer = new BufferedWriter(new FileWriter("out100.txt"));
        //DataOutputStream outStream = new DataOutputStream(new FileOutputStream(fileName));
        //DataInputStream inStream = new DataInputStream(new FileInputStream(fileName));
        FileInputStream in = new FileInputStream(fileName);
        int num, offset = 0;
        byte[] array = new byte[8];
        
        for (int i = 0;i < 32;i+=8) {
            in.read(array, 0, 8);
            System.out.println(bytesToLong(array));
        }
        in.close();
    }
    
    public static long bytesToLong(byte[] bytes) {  
        ByteBuffer buffer = ByteBuffer.allocate(8); 
        buffer.put(bytes, 0, bytes.length);  
        buffer.flip();  //need flip   
        return buffer.getLong();  
    } 
}
