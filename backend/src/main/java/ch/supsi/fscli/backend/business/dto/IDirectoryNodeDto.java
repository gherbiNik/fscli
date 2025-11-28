package ch.supsi.fscli.backend.business.dto;

import java.util.Map;

public interface IDirectoryNodeDto {
    Map<String, Integer> getChildrenUids();
    Integer getParentUid();
}
