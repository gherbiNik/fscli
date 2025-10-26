package ch.supsi.fscli.backend.dataAccess;

import javafx.scene.text.Font;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class PreferenceDAO implements IPreferenceDAO {
    private static String userHomeDirectory = System.getProperty("user.home");



    ////////////////
    /// CONSTANT ///
    ////////////////
    private static final String preferencesDirectory = ".fscli";
    private static final String preferencesFile = "preferences.properties";
    private static final double DEFAULT_FONT_SIZE = Font.getDefault().getSize();
    private static final String DEFAULT_FONT_NAME = Font.getDefault().getName();
    private static final int DEFAULT_COLUMN = 80;
    private static final int DEFAULT_OUTPUT_AREA_ROW = 10;
    private static final int DEFAULT_LOG_AREA_ROW = 5;
    
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
        defaultPreferences.setProperty("column", DEFAULT_COLUMN+"");
        defaultPreferences.setProperty("output-area-row", DEFAULT_OUTPUT_AREA_ROW + "");
        defaultPreferences.setProperty("log-area-row", DEFAULT_LOG_AREA_ROW + "");
        defaultPreferences.setProperty("font-command-line", DEFAULT_FONT_NAME);
        defaultPreferences.setProperty("font-output-area", DEFAULT_FONT_NAME);
        defaultPreferences.setProperty("font-log-area", DEFAULT_FONT_NAME);


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

    private void checkFileLoadedPreferences() {

        if (preferences != null) {
            if (preferences.getProperty("language-tag") == null ||
                preferences.getProperty("column") == null ||
                preferences.getProperty("output-area-row") == null ||
                preferences.getProperty("log-area-row") == null ||
                preferences.getProperty("font-command-line") == null ||
                preferences.getProperty("font-output-area") == null ||
                preferences.getProperty("font-log-area") == null
            )
                preferences = loadDefaultPreferences();
        }
    }


    @Override
    public Properties getPreferences() {
        // Se le preferenze sono già caricate in memoria, restituiscile
        if (preferences != null) {
            return preferences;
        }

        // Prova a caricare il file delle preferenze esistente
        preferences = this.loadPreferences(this.getPreferencesFilePath());
        checkFileLoadedPreferences();


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

    @Override
    public Font getCommandLineFont() {
        String font = DEFAULT_FONT_NAME;
        if (preferences != null) {
            font =  preferences.getProperty("font-command-line") == null ? DEFAULT_FONT_NAME : preferences.getProperty("font-command-line");
        }

        return new Font(font,DEFAULT_FONT_SIZE);
    }

    @Override
    public Font getOutputAreaFont() {
        String font = DEFAULT_FONT_NAME;
        if (preferences != null) {
            font =  preferences.getProperty("font-output-area") == null ? DEFAULT_FONT_NAME : preferences.getProperty("font-output-area");
        }

        return new Font(font,DEFAULT_FONT_SIZE);
    }

    @Override
    public Font getLogAreaFont() {
        String font = DEFAULT_FONT_NAME;
        if (preferences != null) {
            font =  preferences.getProperty("font-log-area") == null ? DEFAULT_FONT_NAME : preferences.getProperty("font-log-area");
        }

        return new Font(font,DEFAULT_FONT_SIZE);
    }

    private boolean isValueValidNumberFormat(String valoreStringa) {
        if (valoreStringa == null || valoreStringa.trim().isEmpty()) {
            return false;
        }
        try {
            //    usiamo .trim() per rimuovere eventuali spazi bianchi (es. " 25 ")
            Integer.parseInt(valoreStringa.trim());
        } catch (NumberFormatException e) {
            //    Stampa un messaggio di errore (utile per il debug) e restituisci il default.
            System.err.println("ATTENZIONE: Valore non numerico per 'output-area-row': \"" + valoreStringa + "\". Verrà usato il valore di default.");
            return false;
        }
        return true;
    }


    @Override
    public int getOutputAreaRow() {
        if (preferences != null) {
            String value = preferences.getProperty("output-area-row").trim();
            if (isValueValidNumberFormat(value)) {
                return Integer.parseInt(value);
            }
            return DEFAULT_OUTPUT_AREA_ROW;
        }
        System.err.println("Preferenze nulle, verranno caricate preferenze di default");
        loadDefaultPreferences();
        return DEFAULT_OUTPUT_AREA_ROW;
    }



    @Override
    public int getLogAreaRow() {
        if (preferences != null) {
            String value = preferences.getProperty("log-area-row").trim();
            if (isValueValidNumberFormat(value)) {
                return Integer.parseInt(value);
            }
            return DEFAULT_LOG_AREA_ROW;
        }
        System.err.println("Preferenze nulle, verranno caricate preferenze di default");
        loadDefaultPreferences();
        return DEFAULT_LOG_AREA_ROW;
    }

    @Override
    public int getColumn() {
        if (preferences != null) {
            String value = preferences.getProperty("column").trim();
            if (isValueValidNumberFormat(value)) {
                return Integer.parseInt(value);
            }
            return DEFAULT_COLUMN;
        }
        System.err.println("Preferenze nulle, verranno caricate preferenze di default");
        loadDefaultPreferences();
        return DEFAULT_COLUMN;
    }

}
