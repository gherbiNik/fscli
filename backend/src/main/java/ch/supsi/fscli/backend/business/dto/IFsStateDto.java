package ch.supsi.fscli.backend.business.dto;

import java.util.Map;

public interface IFsStateDto {
    DirectoryNodeDto getRoot();
    int getCurrentDirectoryUid();
    int getNextInodeId();
    Map<Integer, InodeDto> getInodeTable();
}
