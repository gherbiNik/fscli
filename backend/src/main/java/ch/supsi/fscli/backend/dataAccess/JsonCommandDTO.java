package ch.supsi.fscli.backend.dataAccess;

public class JsonCommandDTO {
    private String commandName;
    private String className;
    private String synopsisKey;
    private String descriptionKey;

    public JsonCommandDTO() {}

    public String getCommandName() { return commandName; }
    public void setCommandName(String commandName) { this.commandName = commandName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getSynopsisKey() { return synopsisKey; }
    public void setSynopsisKey(String synopsisKey) { this.synopsisKey = synopsisKey; }

    public String getDescriptionKey() { return descriptionKey; }
    public void setDescriptionKey(String descriptionKey) { this.descriptionKey = descriptionKey; }
}