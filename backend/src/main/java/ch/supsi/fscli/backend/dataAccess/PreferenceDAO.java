package ch.supsi.fscli.backend.dataAccess;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PreferenceDAO implements IPreferenceDAO {
    private static String userHomeDirectory = System.getProperty("user.home");
    private static final String preferencesDirectory = ".fscli";
    private static final String preferencesFile = "preferences.properties";
    
    private static PreferenceDAO instance;
    private Properties preferences;

    private PreferenceDAO() {}
    
    public static PreferenceDAO getInstance() {
        if (instance == null) {
            instance = new PreferenceDAO();
        }
        return instance;
    }

    public Path getUserPreferencesFilePath() {
        return Path.of(userHomeDirectory, preferencesDirectory, preferencesFile);
    }

    public Path getPreferencesDirectoryPath() {
        return Path.of(userHomeDirectory, preferencesDirectory);
    }

    private boolean preferencesDirectoryExists() {
        return Files.exists(this.getPreferencesDirectoryPath());
    }

    private boolean preferencesFileExists() {
        return Files.exists(this.getPreferencesFilePath());
    }

    private Path createPreferencesDirectory() {
        try {
            return Files.createDirectories(this.getPreferencesDirectoryPath());
        } catch (IOException e) {
            System.err.println("Errore nella creazione della directory delle preferenze: " + e.getMessage());
            return null;
        }
    }

    private Path getPreferencesFilePath() {
        return Path.of(userHomeDirectory, preferencesDirectory, preferencesFile);
    }

    private boolean createPreferencesFile(Properties defaultPreferences) {
        if (defaultPreferences == null) {
            return false;
        }

        if (!preferencesDirectoryExists()) {
            if (this.createPreferencesDirectory() == null) {
                return false;
            }
        }

        if (!preferencesFileExists()) {
            try {
                // create user preferences file (with default preferences)
                try (FileOutputStream outputStream = new FileOutputStream(this.getPreferencesFilePath().toFile())) {
                    defaultPreferences.store(outputStream, "User Preferences");
                }
                return true;
            } catch (IOException e) {
                System.err.println("Errore nella creazione del file delle preferenze: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    private Properties loadDefaultPreferences() {
        Properties defaultPreferences = new Properties();

        // Create basic default preferences
        defaultPreferences.setProperty("language-tag", "it-IT");
        defaultPreferences.setProperty("column", "80");
        defaultPreferences.setProperty("output-area-row", "10");
        defaultPreferences.setProperty("log-area-row", "5");
        defaultPreferences.setProperty("font-command-line", "Arial");
        defaultPreferences.setProperty("font-output-area", "Arial");
        defaultPreferences.setProperty("font-log-area", "Arial");


        return defaultPreferences;
    }

    private Properties loadPreferences(Path path) {
        Properties preferences = new Properties();
        try {
            try (FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                preferences.load(fileInputStream);
            }
        } catch (IOException e) {
            System.err.println("Errore nel caricamento delle preferenze: " + e.getMessage());
            return null;
        }
        return preferences;
    }


    @Override
    public Properties getPreferences() {
        // Se le preferenze sono già caricate in memoria, restituiscile
        if (preferences != null) {
            return preferences;
        }

        // Prova a caricare il file delle preferenze esistente
        preferences = this.loadPreferences(this.getPreferencesFilePath());

        // Se il caricamento fallisce (es. il file non esiste), crealo
        if (preferences == null) {
            System.out.println("File delle preferenze non trovato. Verrà creato con i valori di default.");

            // Carica i valori di default in un oggetto Properties
            Properties defaultPreferences = this.loadDefaultPreferences();

            // Usa il tuo metodo (precedentemente non utilizzato) per creare directory e file
            boolean isFileCreated = this.createPreferencesFile(defaultPreferences);

            if (isFileCreated) {
                // Se la creazione ha successo, usa queste preferenze
                preferences = defaultPreferences;
            } else {
                // Altrimenti, usa le preferenze di default solo in memoria come fallback
                System.err.println("ERRORE: Impossibile creare il file delle preferenze. Verranno usate le impostazioni di default solo per questa sessione.");
                return defaultPreferences;
            }
        }

        return preferences;
    }

    @Override
    public void setPreference(String key, String value) {

        getPreferences().setProperty(key, value);

        savePreferences();
    }

    private void savePreferences() {
        if (preferences == null) {
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(getUserPreferencesFilePath().toFile())) {
            preferences.store(fos, "User Preferences");
            System.out.println("Preferences saved successfully");
        } catch (IOException e) {
            System.err.println("Failed to save preferences: " + e.getMessage());
        }
    }
    
}
