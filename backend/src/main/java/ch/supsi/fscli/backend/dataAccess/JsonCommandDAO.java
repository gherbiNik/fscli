package ch.supsi.fscli.backend.dataAccess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class JsonCommandDAO implements ICommandDAO {

    private final String jsonFilePath;

    public JsonCommandDAO(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    @Override
    public List<JsonCommandDTO> getAllCommands() {
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath)) {
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