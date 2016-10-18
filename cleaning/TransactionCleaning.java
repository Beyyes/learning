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
 * district表清洗
 * 
 * @author cgf
 *
 */
public class TransactionCleaning {
	private static String dataFilePath = "data/trans.csv";
	private static String accountFilePath = "data/account_right.csv";
	private static String errorFilePath = "data/trans_error.csv";

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
			String trans = splits[0];
			String account_id = splits[1];
			String date = splits[2];
			String type = splits[3];
			String operation = splits[4];
			String amount = splits[5];
			String balance = splits[6];
			String k_symbol = splits[7];
			String bank = splits[8];
			String account = splits[9];
			
			// 主键为数字且不为空
			if (Verifier.isInteger(trans) == false) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				continue;
			} else {
				int key = Integer.valueOf(trans);
				if (primaryKeys.contains(key)) {
					writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
					continue;
				}
				primaryKeys.add(key);
			}

			// 外键 数字
			if (Verifier.isInteger(account_id) == false || !accountSet.contains(Integer.parseInt(account_id))) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				System.out.println("account_id:" + account_id);
				continue;
			}

			if (Verifier.isDateSS(date) == false) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				System.out.println("date" + "!!");
				continue;
			} 

			if (!type.equals("VYDAJ") && !type.equals("PRIJEM")) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				System.out.println(type + " - type");
				continue;
			}
			
			if (!operation.equals("VYBER KARTOU") && !operation.equals("PREVOD NA UCET")
					&& !operation.equals("PREVOD Z UCTU") && !operation.equals("VYBER")
					&& !operation.equals("PREVOD NA UCET")) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				System.out.println(operation + " - operation");
				continue;
			}
			
			if (Verifier.isInteger(balance) == false) {
				System.out.println(balance + " - balance");
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				continue;
			}
			
			if (Verifier.isInteger(amount) == false) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				System.out.println(amount + " - amount");
				continue;
			} else {
				int key = Integer.valueOf(amount);
				if(key < 0) {
					writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				}
			}
			
			if (!bank.equals("") && bank.matches("[A-Z][A-Z]") == false) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				// System.out.println(bank + " - bank");
				System.out.println(line);
				continue;
			}

			if (Verifier.isInteger(account) == false) {
				writeToCSV(trans, account_id, date, type, operation, amount, balance, k_symbol, bank, account);
				continue;
			} else {
				
			}
		}

		errorCSVWriter.close();

	}

	public static void writeToCSV(String trans, String account_id, String date, String type, String operation,
			String amount, String balance, String k_symbol, String bank, String account) {
		errorCSVWriter.writeLine(trans + "," + account_id + "," + date + "," + type + "," + operation + "," + amount
				+ "," + balance + "," + k_symbol + "," + bank + "," + account);
	}
}
