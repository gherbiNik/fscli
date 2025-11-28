package ch.supsi.fscli.backend.dataAccess.filesystem;

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

    private JacksonSaveDataService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static JacksonSaveDataService getInstance(PreferenceDAO preferenceDAO) {
        if (myself == null) {
            myself = new JacksonSaveDataService();
            myself.preferenceDAO = preferenceDAO;
            myself.createSaveDirectoryIfNotExists();
        }
        return myself;
    }

    private void createSaveDirectoryIfNotExists() {
        try {
            Path path = Paths.get(preferenceDAO.getUserPreferencesFilePath().toString(), saveDirectory);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossibile creare la directory di salvataggio", e);
        }
    }

    @Override
    public void save(IFsStateDto iFsStateDto) {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = localDateTime.format(formatter);
        String completeFileName = saveFileName + "_" + formattedDateTime + ".json";

        try {
            Path saveFilePath = Paths.get(preferenceDAO.getUserPreferencesFilePath().toString(), saveDirectory, completeFileName);
            objectMapper.writeValue(saveFilePath.toFile(), iFsStateDto);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void saveAs(IFsStateDto iFsStateDto, File file) {

        try {
            Path saveFilePath = Paths.get(file.getPath());
            objectMapper.writeValue(saveFilePath.toFile(), iFsStateDto);
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
            return objectMapper.readValue(saveFilePath.toFile(), IFsStateDto.class);
        } catch (IOException e) {
            return null;
        }
    }

}