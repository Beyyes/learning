package external;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 多路归并 & 败者树
 * 
 * @author CGF
 *
 */
public class MultiSort {

	static String file = "/home/cgf/data/part";
	static DataOutputStream outs[] = new DataOutputStream[40];
	static DataInputStream ins[] = new DataInputStream[40];
	static int M = 32;

	public static void main(String[] args) throws IOException {
		byte[] array = new byte[8];
		int cnt = 0;
		int fileEnd = 0;

		int cycNum = fileEnd;
		for (int i = 1; i <= M; i++) {
			ins[i] = new DataInputStream(new FileInputStream(file + String.valueOf(i)));
		}

		long[] num = new long[40];
		for (int i = 1; i <= M; i++) {
			num[i] = -1;
		}
		long minValue = Long.MAX_VALUE;
		int pos = -1;
		DataOutputStream out = new DataOutputStream(new FileOutputStream("/home/cgf/data/ansout"));

		while (true) {
			int flag = 1;
			minValue = Long.MAX_VALUE;
			pos = -1;
			for (int i = 1; i <= M; i++) {
				if (num[i] == -1) {
					if (ins[i].available() != 0) {
						num[i] = ins[i].readLong();
						flag = 0;
						if (num[i] < minValue) {
							minValue = num[i];
							pos = i;
						}
					}
				} else {
					if (num[i] < minValue) {
						minValue = num[i];
						pos = i;
					}
					flag = 0;
					continue;
				}
			}
			
			if(minValue != Long.MAX_VALUE) {
				out.writeLong(minValue);
				num[pos] = -1;
				pos = -1;
			}

			if (flag == 1) {
				break;
			}
		}
		out.close();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip(); // need flip
		return buffer.getLong();
	}
}
