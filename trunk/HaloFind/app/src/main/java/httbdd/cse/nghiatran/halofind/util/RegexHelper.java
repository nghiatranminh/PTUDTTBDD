package httbdd.cse.nghiatran.halofind.util;

/**
 * Created by TranMinhNghia_512023 on 10/15/2015.
 */
public class RegexHelper {
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (email.matches(emailRegex)) {
            return true;
        }
        return false;
    }

    public static boolean isValidBirthday(String date) {
        String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (date.matches(dateRegex)) {
            return true;
        }
        return false;
    }
}
