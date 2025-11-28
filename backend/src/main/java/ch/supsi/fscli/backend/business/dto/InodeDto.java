package ch.supsi.fscli.backend.business.dto;

import ch.supsi.fscli.backend.business.filesystem.InodeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileNodeDto.class, name = "FILE"),
        @JsonSubTypes.Type(value = DirectoryNodeDto.class, name = "DIRECTORY"),
        @JsonSubTypes.Type(value = SoftLinkDto.class, name = "SOFTLINK")
})
public abstract class InodeDto implements IInodeDto{
    private final int uid;
    private final InodeType type;

    @JsonCreator
    protected InodeDto(@JsonProperty("uid") int uid,
                       @JsonProperty("type") InodeType type) {
        this.uid = uid;
        this.type = type;
    }

    @Override
    public int getUid() {
        return uid;
    }

    @Override
    public InodeType getType() {
        return type;
    }
}
