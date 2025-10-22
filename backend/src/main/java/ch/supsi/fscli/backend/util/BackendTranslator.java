package ch.supsi.fscli.backend.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class BackendTranslator {
    private static final String BUNDLE_BASE_NAME = "i18n.responses";

    public static String getString(String key, Locale locale, Object... args) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            String pattern = bundle.getString(key);

            if (args.length > 0) {
                return MessageFormat.format(pattern, args);
            } else {
                return pattern;
            }
        } catch (Exception e) {
            System.err.println("Key not found inside the backend bundle: " + key);
            return "!" + key + "!";
        }
    }
}
