package external;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class gedata {
	public static void main(String[] args) throws IOException {
		DataOutputStream out = new DataOutputStream(new FileOutputStream("/home/cgf/data/haha2"));
		out.writeLong(10L);
		for (long i = 5;i <= 20;i++) {
			out.writeLong(i);
		}
		out.close();
	}
}
