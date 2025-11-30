package ch.supsi.fscli.backend.business.dto;

import ch.supsi.fscli.backend.business.filesystem.Inode;
import ch.supsi.fscli.backend.business.filesystem.InodeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("FILE")
public class FileNodeDto extends InodeDto implements IFileNodeDto {
    public FileNodeDto() {
        super(-1, InodeType.FILE); // uid will be overwritten during rebuild phase
    }

    @JsonCreator
    public FileNodeDto(@JsonProperty("uid") int uid) {
        super(uid, InodeType.FILE);
    }

    public FileNodeDto(int uid, InodeType type) {
        super(uid, type);
    }
}

