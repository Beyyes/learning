package cleaning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import operation.CSVFileReader;
import operation.CSVFileWriter;

/**
 * account����ϴ
 * 
 * @author CGF
 *
 */
public class AccountCleaning {

	private static String dataFilePath = "data/account.csv";
	private static String errorFilePath = "data/account_error.csv";

	private static CSVFileWriter errorCSVWriter = new CSVFileWriter(errorFilePath);

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(dataFilePath));
		String line = "";
		Set<Integer> primaryKeys = new HashSet<Integer>();
		line = reader.readLine();
		while ((line = reader.readLine()) != null) {
			String[] splits = line.split(",");
			String account_id = splits[0];
			String district_id = splits[1];
			String frequency = splits[2];
			String date = splits[3];

			// �����������ظ�
			if (Verifier.isInteger(account_id) == false) {
				writeToCSV(account_id, district_id, frequency, date);
				continue;
			} else {
				int key = Integer.valueOf(account_id);
				if (primaryKeys.contains(key)) {
					writeToCSV(account_id, district_id, frequency, date);
					continue;
				}
				primaryKeys.add(key);
			}

			// district_idΪ���������Ϊ��ֵ���ͣ���1~77֮��
			if (Verifier.isInteger(district_id) == false) {
				writeToCSV(account_id, district_id, frequency, date);
				int key = Integer.valueOf(district_id);
				if (key >= 1 && key <= 77) {
					continue;
				} else {
					writeToCSV(account_id, district_id, frequency, date);
				}
			}

			// Ƶ�ʱ���Ϊ�����������֮һ
			if (!frequency.equals("POPLATEK MESICNE") || !frequency.equals("POPLATEK PO OBRATU")
					|| !frequency.equals("POPLATEK TYDNE")) {
				writeToCSV(account_id, district_id, frequency, date);
			} else {
				continue;
			}

			// dateʱ������
			if (Verifier.isDate(date)) {
				// System.out.println(date);
				continue;
			} else {
				writeToCSV(account_id, district_id, frequency, date);
			}
		}
	}

	public static void writeToCSV(String account_id, String district_id, String frequency, String date) {
		errorCSVWriter.writeLine(account_id + "," + district_id + "," + frequency + "," + date);
	}
}
