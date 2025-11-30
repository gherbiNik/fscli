package ch.supsi.fscli.backend.business.dto;

import ch.supsi.fscli.backend.business.filesystem.InodeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("SOFTLINK")
public class SoftLinkDto extends InodeDto implements ISoftLinkDto {
    private String targetPath;

    public SoftLinkDto() {
        super(-1, InodeType.SOFTLINK);
    }

    @JsonCreator
    public SoftLinkDto(@JsonProperty("uid") int uid,
                       @JsonProperty("targetPath") String targetPath) {
        super(uid, InodeType.SOFTLINK);
        this.targetPath = targetPath;
    }

    public SoftLinkDto(int uid, InodeType type, String targetPath) {
        super(uid, type);
        this.targetPath = targetPath;
    }

    @Override
    public String getTargetPath() {
        return targetPath;
    }
}
