package ch.supsi.fscli.backend.business.dto;

import ch.supsi.fscli.backend.business.filesystem.InodeType;

public interface IInodeDto {
    int getUid();
    InodeType getType();
}
