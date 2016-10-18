package cleaning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.HTMLReader.BlockAction;

import operation.CSVFileReader;
import operation.CSVFileWriter;

/**
 * loan表清洗
 * 
 * @author cgf
 *
 */
public class LoanCleaning {
	private static String dataFilePath = "data/loan.csv";
	private static String accountFilePath = "data/account_right.csv";
	private static String errorFilePath = "data/loan_error.csv";

	private static CSVFileWriter errorCSVWriter = new CSVFileWriter(errorFilePath);

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(dataFilePath));
		String line = "";
		Set<Integer> primaryKeys = new HashSet<Integer>();
		line = reader.readLine();
		Set<Integer> accountSet = new HashSet<Integer>();

		BufferedReader accountReader = new BufferedReader((new FileReader(accountFilePath)));
		line = accountReader.readLine();
		while ((line = accountReader.readLine()) != null) {
			String[] str = line.split(",");
			accountSet.add(Integer.parseInt(str[0]));
		}
		accountReader.close();

		while ((line = reader.readLine()) != null) {
			String[] splits = line.split(",");
			String loan_id = splits[0];
			String account_id = splits[1];
			String date = splits[2];
			String amount = splits[3];
			String duration = splits[4];
			String payments = splits[5];
			String status = splits[6];
			String payduration = splits[7];
			
			// 主键为数字且不为空
			if (Verifier.isInteger(loan_id) == false) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				continue;
			} else {
				int key = Integer.valueOf(loan_id);
				if (primaryKeys.contains(key)) {
					writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
					continue;
				}
				primaryKeys.add(key);
			}

			// 外键 数字
			if (Verifier.isInteger(account_id) == false || !accountSet.contains(Integer.parseInt(account_id))) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				System.out.println("account_id:" + account_id);
				continue;
			}
			
			if (Verifier.isDate(date) == false) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				System.out.println("date" + "!!" + date);
				continue;
			} 
			
			if (Verifier.isInteger(amount) == false) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				System.out.println("amount:" + amount);
				continue;
			} else {
				int key = Integer.valueOf(amount);
				if (key < 0) {
					writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				}
			}

			if (Verifier.isInteger(duration) == false) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				System.out.println("duration:" + duration);
				continue;
			} else {
				int key = Integer.valueOf(duration);
				if (key < 0) {
					writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				}
			}

			if (Verifier.isInteger(payments) == false) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				System.out.println("amount:" + amount);
				continue;
			} else {
				int key = Integer.valueOf(payments);
				if (key < 0 || key * Integer.valueOf(duration) != Integer.valueOf(amount)) {
					writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				}
			}
			
			if (!status.equals("A") && !status.equals("B") && !status.equals("C") && !status.equals("D")) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				System.out.println(status + " - status");
				continue;
			}
			
			if (Verifier.isInteger(payduration) == false) {
				writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				System.out.println("payduration:" + payduration);
				continue;
			} else {
				int key = Integer.valueOf(payduration);
				if (key < 0) {
					writeToCSV(loan_id, account_id, date, amount, duration, payments, status, payduration);
				}
			}
		}

		errorCSVWriter.close();

	}

	public static void writeToCSV(String loan_id, String account_id, String date, String amount,
			String duration, String payments, String status, String payduration) {
		errorCSVWriter.writeLine(
				loan_id + "," + account_id + "," + date + "," + amount + "," + duration + "," + payments + "," + status + "," + payduration);
	}
}
