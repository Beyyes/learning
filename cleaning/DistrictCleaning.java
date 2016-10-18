package cleaning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import operation.CSVFileReader;
import operation.CSVFileWriter;

/**
 * district±Ì«Âœ¥
 * 
 * @author cgf
 *
 */
public class DistrictCleaning {
	private static String dataFilePath = "data/district.csv";
	private static String errorFilePath = "data/district_error.csv";
	
	private static CSVFileWriter errorCSVWriter = new CSVFileWriter(errorFilePath);
	
	public String readOneline() {
		return "X";
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(dataFilePath)); 
		String line = "";
		
		Set<Integer> primaryKeys = new HashSet<Integer>();
		line = reader.readLine();
		while((line = reader.readLine()) != null) {
			String[] splits = line.split(",");
			String distrcit_id = splits[0];
			String hab_number = splits[3];
			String city_number = splits[4];
			String ave_salary = splits[5];
			String uemploy_rate = splits[6];
			String crime_number = splits[7];
			if(!Verifier.isInteger(distrcit_id) || !Verifier.isInteger(hab_number) || !Verifier.isInteger(city_number)
					|| !Verifier.isInteger(ave_salary) || !Verifier.isDouble(uemploy_rate) || !Verifier.isInteger(crime_number)) {
				writeToCSV(distrcit_id, hab_number, city_number, ave_salary, uemploy_rate, crime_number);
				System.out.print("aa");
				continue;
			}
			
			int district_id = Integer.valueOf(splits[0]);
			if(primaryKeys.contains(distrcit_id)) {
				writeToCSV(distrcit_id, hab_number, city_number, ave_salary, uemploy_rate, crime_number);
			}
			
		}
		
		errorCSVWriter.close();
		
	}
	
	public static void writeToCSV(String distrcit_id, String hab_number, String city_number, String ave_salary,
			String uemploy_rate, String crime_number) {
		errorCSVWriter.writeLine(distrcit_id + " " + hab_number + " " + city_number + " " + ave_salary
				+ " " + uemploy_rate + " " + crime_number);
	}
}
