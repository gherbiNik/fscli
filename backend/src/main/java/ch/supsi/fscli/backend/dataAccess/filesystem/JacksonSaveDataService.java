package ch.supsi.fscli.backend.dataAccess.filesystem;

import ch.supsi.fscli.backend.business.dto.FsStateDto;
import ch.supsi.fscli.backend.business.dto.IFsStateDto;
import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class JacksonSaveDataService implements ISaveData {

    private static JacksonSaveDataService myself;
    private final ObjectMapper objectMapper;
    private final String saveDirectory = "saves";
    private final String saveFileName = "filesystemSaved";
    private PreferenceDAO preferenceDAO;

    // Campo aggiunto per tracciare il file corrente
    private File currentFile;

    private JacksonSaveDataService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static JacksonSaveDataService getInstance(PreferenceDAO preferenceDAO) {
        if (myself == null) {
            myself = new JacksonSaveDataService();
            myself.preferenceDAO = preferenceDAO;
        }
        return myself;
    }

    private void createSaveDirectoryIfNotExists() {
        try {
            Path userPrefsPath = preferenceDAO.getUserPreferencesFilePath();
            if (userPrefsPath == null) {
                return; // Postpone directory creation
            }

            Path baseDir = userPrefsPath.getParent();
            if (baseDir == null) {
                throw new IllegalStateException("Cannot determine parent directory of preferences file");
            }

            Path savesPath = baseDir.resolve(saveDirectory);
            if (!Files.exists(savesPath)) {
                Files.createDirectories(savesPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossibile creare la directory di salvataggio", e);
        }
    }

    @Override
    public void save(IFsStateDto iFsStateDto) {
        // Se abbiamo un file corrente, sovrascriviamo quello
        if (this.currentFile != null) {
            try {
                objectMapper.writeValue(this.currentFile, iFsStateDto);
                System.out.println("Filesystem salvato su: " + this.currentFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Errore durante il salvataggio: " + e.getMessage());
            }
        } else {
            // Se non c'è un file corrente (es. Nuovo FS), usiamo la logica originale (timestamp)
            createSaveDirectoryIfNotExists();
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String formattedDateTime = localDateTime.format(formatter);
            String completeFileName = saveFileName + "_" + formattedDateTime + ".json";

            try {
                Path userPrefsPath = preferenceDAO.getUserPreferencesFilePath();
                Path baseDir = userPrefsPath.getParent(); // Directory che contiene preferences.properties
                Path saveFilePath = baseDir.resolve(saveDirectory).resolve(completeFileName);

                objectMapper.writeValue(saveFilePath.toFile(), iFsStateDto);

                // IMPORTANTE: Impostiamo questo nuovo file come corrente per i futuri save
                this.currentFile = saveFilePath.toFile();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    @Override
    public void saveAs(IFsStateDto iFsStateDto, File file) {
        createSaveDirectoryIfNotExists();
        try {
            // Salva sul file specificato
            objectMapper.writeValue(file, iFsStateDto);
            // Aggiorna il file corrente
            this.currentFile = file;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public IFsStateDto load(String fileName) {
        try {
            Path saveFilePath = Paths.get(fileName);
            if (!Files.exists(saveFilePath)) {
                throw new NoFilesystemSavedEx("Nessun salvataggio presente");
            }
            // Aggiorna il file corrente quando carichiamo
            this.currentFile = saveFilePath.toFile();
            return objectMapper.readValue(saveFilePath.toFile(), FsStateDto.class);
        } catch (IOException e) {
            return null;
        }


    }

    @Override
    public String getCurrentFileAbsolutePath() {
        return currentFile.getAbsolutePath();
    }
}