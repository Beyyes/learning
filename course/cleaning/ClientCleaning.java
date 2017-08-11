package cleaning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import operation.CSVFileWriter;

/**
 * client表清洗
 * 
 * @author cgf
 *
 */
public class ClientCleaning {
	private static String dataFilePath = "data/client.csv";
	private static String districtFilePath = "data/district_right.csv";
	private static String errorFilePath = "data/client_error.csv";

	private static CSVFileWriter errorCSVWriter = new CSVFileWriter(errorFilePath);

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(dataFilePath));
		String line = "";
		Set<Integer> primaryKeys = new HashSet<Integer>();
		line = reader.readLine();
		Set<Integer> accountSet = new HashSet<Integer>();

		BufferedReader accountReader = new BufferedReader((new FileReader(districtFilePath)));
		line = accountReader.readLine();
		while ((line = accountReader.readLine()) != null) {
			String[] str = line.split(",");
			accountSet.add(Integer.parseInt(str[0]));
		}
		accountReader.close();

		while ((line = reader.readLine()) != null) {
			String[] splits = line.split(",");
			String client_id = splits[0];
			String birth_num = splits[1];
			String district_id = splits[2];

			// 主键为数字且不为空
			if (Verifier.isInteger(client_id) == false) {
				writeToCSV(client_id, birth_num, district_id);
				continue;
			} else {
				int key = Integer.valueOf(client_id);
				if (primaryKeys.contains(key)) {
					writeToCSV(client_id, birth_num, district_id);
					continue;
				}
				primaryKeys.add(key);
			}

			// 外键 数字
			if (Verifier.isInteger(district_id) == false || !accountSet.contains(Integer.parseInt(district_id))) {
				writeToCSV(client_id, birth_num, district_id);
				System.out.println("district_id:" + district_id);
				continue;
			}

			if (Verifier.isInteger(birth_num) == false) {
				writeToCSV(client_id, birth_num, district_id);
				System.out.println("birth_num:" + birth_num);
				continue;
			} else {
				int key = Integer.valueOf(birth_num);
				if (key < 0) {
					writeToCSV(client_id, birth_num, district_id);
				}
				String sex = "";
				int v = Integer.valueOf(birth_num);
				int MM = v / 100 % 100;
				if (MM > 12) {
					sex = "famale";
					MM -= 50;
				}
				v = v % 100 + MM * 100 + v / 10000 * 10000;
				SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
				sdf.setLenient(false);
				try {
					sdf.parse(String.valueOf(v));
					String birthday = "19" + (v / 10000) + "-" + (v / 100 % 100) + "-" + v % 100;
					int age = 100 - v / 10000;
					int ageRange = age / 10;
					int month = v / 100 % 100;
					int day = v % 100;
				} catch (ParseException e) {
					System.out.println("birth_num!!:" + birth_num);
					writeToCSV(client_id, birth_num, district_id);
					e.printStackTrace();
				}
			}
		}
		errorCSVWriter.close();
	}
	

	public static void writeToCSV(String client_id, String birth_num, String district_id) {
		errorCSVWriter.writeLine(client_id + "," + birth_num + "," + district_id);
	}
}
