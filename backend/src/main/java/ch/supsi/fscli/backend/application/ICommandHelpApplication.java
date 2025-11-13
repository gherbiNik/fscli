package ch.supsi.fscli.backend.application;

import java.util.Locale;
import java.util.Map;

public interface ICommandHelpApplication {
    Map<String, String> getCommandDescriptions(Locale locale);

}