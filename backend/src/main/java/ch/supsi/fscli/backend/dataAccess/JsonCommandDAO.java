package ch.supsi.fscli.backend.dataAccess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Singleton
public class JsonCommandDAO implements ICommandDAO {

    private static final String JSON_FILE_PATH = "commands.json";

    public JsonCommandDAO() {
    }

    @Override
    public List<JsonCommandDTO> getAllCommands() {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(JSON_FILE_PATH)) {
            if (inputStream == null) {
                return Collections.emptyList();
            }

            return mapper.readValue(inputStream, new TypeReference<List<JsonCommandDTO>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}