package ch.supsi.fscli.backend.business.command;

import ch.supsi.fscli.backend.util.BackendTranslator;

import java.util.*;

public class CommandHelpContainer {
    private static CommandHelpContainer instance;
    private BackendTranslator translator;
    private Map<Locale, Map<String, String>> cachedDescriptions = new HashMap<>();

    private CommandHelpContainer() {}

    public static CommandHelpContainer getInstance(BackendTranslator translator) {
        if (instance == null) {
            instance = new CommandHelpContainer();
            instance.initialize(translator);
        }
        return instance;
    }

    private void initialize(BackendTranslator translator){
        this.translator = translator;
    }

    public Map<String, String> getCommandDescriptions(Locale locale) {
        if (cachedDescriptions.containsKey(locale)) {
            return cachedDescriptions.get(locale);
        }

        ResourceBundle rb;
        try {
            rb = ResourceBundle.getBundle("i18n.responses", locale);
        } catch (Exception e) {
            rb = ResourceBundle.getBundle("i18n.responses", Locale.ROOT);
        }

        Set<String> allKeys = new HashSet<>();
        Enumeration<String> keysEnum = rb.getKeys();
        while (keysEnum.hasMoreElements()) {
            allKeys.add(keysEnum.nextElement());
        }

        Set<String> commandIds = new HashSet<>();
        for (String key : allKeys) {
            if (key.startsWith("c.") && !key.endsWith(".synopsis")) {
                commandIds.add(key.substring(2));
            }
        }

        Map<String, String> descriptions = new HashMap<>();
        for (String id : commandIds) {
            try {
                String commandName = rb.getString("c." + id);
                String synopsis = rb.getString("c." + id + ".synopsis");
                String desc = rb.getString("d." + id);
                String fullInfo = synopsis + " : " + desc;
                descriptions.put(commandName, fullInfo);
            } catch (MissingResourceException e) {
                // Skip if any part is missing
            }
        }

        cachedDescriptions.put(locale, descriptions);

        return descriptions;
    }
}