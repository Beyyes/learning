package cleaning;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * 
 * @author CGF
 *
 */
public class Verifier {

	/**
	 * �ж��Ƿ�Ϊ������int��
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * �ж��Ƿ�Ϊ������
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDouble(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

	/**
	 * �ж��ֶ��Ƿ�Ϊ��
	 * 
	 * @param pra
	 * @return
	 */
	public static boolean isNotNull(String pra) {
		if (pra.equals(""))
			return false;
		return true;
	}

	public static boolean isDate(String pra) {
		boolean convertSuccess = true;
		// ָ�����ڸ�ʽΪ��λ��/��λ�·�/��λ���ڣ�ע��yyyy/MM/dd���ִ�Сд��
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		try {
			// ����lenientΪfalse.
			// ����SimpleDateFormat��ȽϿ��ɵ���֤���ڣ�����2007/02/29�ᱻ���ܣ���ת����2007/03/01
			format.setLenient(false);
			format.parse(pra);
		} catch (ParseException e) {
			// e.printStackTrace();
			// ���throw java.text.ParseException����NullPointerException����˵����ʽ����
			convertSuccess = false;
		}
		return convertSuccess;
	}
	
	/**
	 * @param pra
	 * @return
	 */
	public static boolean isDateSS(String pra) {
		boolean convertSuccess = true;
		// ָ�����ڸ�ʽΪ��λ��/��λ�·�/��λ���ڣ�ע��yyyy/MM/dd���ִ�Сд��
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			// ����lenientΪfalse.
			// ����SimpleDateFormat��ȽϿ��ɵ���֤���ڣ�����2007/02/29�ᱻ���ܣ���ת����2007/03/01
			format.setLenient(false);
			format.parse(pra);
		} catch (ParseException e) {
			// e.printStackTrace();
			// ���throw java.text.ParseException����NullPointerException����˵����ʽ����
			convertSuccess = false;
		}
		return convertSuccess;
	}
	
	public static void main(String[] args) {
		String x = "";
		System.out.print(isDateSS("1998-07-12 00:00:00"));
		System.out.print(x.length());
	}
}
