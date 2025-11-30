package ch.supsi.fscli.backend.business.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class FsStateDto implements IFsStateDto {

    private DirectoryNodeDto root;
    private int currentDirectoryUid;
    private int nextInodeId;
    private Map<Integer, InodeDto> inodeTable;

    @JsonCreator
    public FsStateDto(@JsonProperty("root") DirectoryNodeDto root,
                      @JsonProperty("currentDirectoryUid") int currentDirectoryUid,
                      @JsonProperty("nextInodeId") int nextInodeId,
                      @JsonProperty("inodeTable") Map<Integer, InodeDto> inodeTable) {
        this.root = root;
        this.currentDirectoryUid = currentDirectoryUid;
        this.nextInodeId = nextInodeId;
        this.inodeTable = inodeTable;
    }

    @Override
    public DirectoryNodeDto getRoot() {
        return root;
    }

    @Override
    public int getCurrentDirectoryUid() {
        return currentDirectoryUid;
    }

    @Override
    public int getNextInodeId() {
        return nextInodeId;
    }

    @Override
    public Map<Integer, InodeDto> getInodeTable() {
        return inodeTable;
    }
}
