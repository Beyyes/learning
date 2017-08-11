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
 * order表清洗
 * 
 * @author cgf
 *
 */
public class OrderCleaning {
	private static String dataFilePath = "data/order.csv";
	private static String accountFilePath = "data/account_right.csv";
	private static String errorFilePath = "data/order_error.csv";

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
			String order_id = splits[0];
			String account_id = splits[1];
			String bank_to = splits[2];
			String account_to = splits[3];
			String amount = splits[4];
			String k_symbol = splits[5];

			// 主键为数字且不为空
			if (Verifier.isInteger(order_id) == false) {
				writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
				continue;
			} else {
				int key = Integer.valueOf(order_id);
				if (primaryKeys.contains(key)) {
					writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
					continue;
				}
				primaryKeys.add(key);
			}

			// 外键 数字
			if (Verifier.isInteger(account_id) == false || !accountSet.contains(Integer.parseInt(account_id))) {
				writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
				System.out.println("account_id:" + account_id);
				continue;
			}

			if (!k_symbol.trim().equals("") && k_symbol.equals("POJISTNE")==false && k_symbol.equals("SIPO")==false
					&& k_symbol.equals("UVER") == false
					&& k_symbol.equals("LEASING") == false) {
				writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
				System.out.println("k_symbol:" + k_symbol + " " +k_symbol.length());
				continue;
			}

			if (Verifier.isInteger(amount) == false) {
				writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
				System.out.println("amount:" + amount);
				continue;
			} else {
				int key = Integer.valueOf(amount);
				if (key < 0) {
					writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
				}
			}

			if (!bank_to.equals("") && bank_to.matches("[A-Z][A-Z]") == false) {
				writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
				System.out.println(bank_to + " - bank_to");
				continue;
			}

			if (Verifier.isInteger(account_to) == false) {
				writeToCSV(order_id, account_id, bank_to, account_to, amount, k_symbol);
				continue;
			}
		}

		errorCSVWriter.close();

	}

	public static void writeToCSV(String order_id, String account_id, String bank_to, String account_to, String amount,
			String k_symbol) {
		errorCSVWriter.writeLine(
				order_id + "," + account_id + "," + bank_to + "," + account_to + "," + amount + "," + k_symbol);
	}
}
