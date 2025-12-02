package ch.supsi.fscli.backend.dataAccess;

import java.util.List;

public interface ICommandDAO {
    // Restituisce i DTO grezzi letti dal file
    List<JsonCommandDTO> getAllCommands();
}