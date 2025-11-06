package ch.supsi.fscli.backend.business.command;

import java.util.List;
import java.util.Map;

public class CommandContext {
    private int currentWorkingDirectory; // id
    private List<String> arguments;
    private Map<String, String> options; // for example: "-s" / "-i"

}
