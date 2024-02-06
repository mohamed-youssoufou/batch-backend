package ci.yoru.hackathon.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FilesUtils {

    public final String productDepositFileName = "fullname";

    public Boolean regex(String filename) {
        String regex = "^CLT-[A-Za-z0-9]{3}-\\d{2}\\d{2}\\d{4}\\.csv$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }
}
