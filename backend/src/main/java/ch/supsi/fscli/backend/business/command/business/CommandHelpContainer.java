
package ch.supsi.fscli.backend.business.command.business;

import ch.supsi.fscli.backend.util.BackendTranslator;

import java.util.*;

public class CommandHelpContainer {
    private static CommandHelpContainer instance;
    private BackendTranslator translator;

    private Map<Locale, List<String>> cachedDescriptions = new HashMap<>();

    // Structure:
    // Map: {command name -> ( synopsis , description ) }
    // so, each command is associated with its synopsis and description
    private Map<String, CommandDetails> commandDetailsMap = new HashMap<>();

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

    public Map<String, CommandDetails> getCommandDetailsMap() {
        getCommandDescriptions();
        return commandDetailsMap;
    }

    public List<String> getCommandDescriptions() {
        Locale locale = translator.getCurrentLocale();
        System.out.println(locale); // Stampa US

        if (cachedDescriptions.containsKey(locale)) {
            return cachedDescriptions.get(locale);
        }

        ResourceBundle rb = translator.getResourceBundle();
        System.out.println(rb.getLocale()); // Stampa IT

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

        List<String> descriptions = new ArrayList<>();
        commandDetailsMap.clear();

        for (String id : commandIds) {
            try {
                String commandName = rb.getString("c." + id);
                String synopsis = rb.getString("c." + id + ".synopsis");
                String desc = rb.getString("d." + id);

                // Full info map
                CommandDetails details = new CommandDetails(synopsis,desc);
                commandDetailsMap.put(commandName, details);

                // Frontend Description
                String fullInfo = synopsis + " : " + desc;
                descriptions.add(fullInfo);
            } catch (MissingResourceException e) {}
        }

        cachedDescriptions.put(locale, descriptions);

        return descriptions;
    }
}

