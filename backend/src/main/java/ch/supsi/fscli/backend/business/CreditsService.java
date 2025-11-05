package ch.supsi.fscli.backend.business;

//TODO: maybe create an interface. this can be used similar for the command list
import java.io.InputStream;
import java.util.Properties;
public class CreditsService {
    private static CreditsService instance;
    private String version;
    private String buildDate;
    private CreditsService() {
        loadBuildProperties();
    }
    public static CreditsService getInstance() {
        if (instance == null) {
            instance = new CreditsService();
        }
        return instance;
    }
    private void loadBuildProperties() {
        Properties buildProperties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("build.properties")) {
            if (input != null) {
                buildProperties.load(input);
                this.version = buildProperties.getProperty("version", "N/A");
                this.buildDate = buildProperties.getProperty("buildDate", "N/A");
            } else {
                this.version = "N/A";
                this.buildDate = "N/A";
            }
        } catch (Exception e) {
            System.err.println("Cannot load build properties: " + e.getMessage());
            this.version = "N/A";
            this.buildDate = "N/A";
        }
    }
    public String getVersion() {
        return version;
    }
    public String getBuildDate() {
        return buildDate;
    }
}