package ch.supsi.fscli.backend.business.dto;

import ch.supsi.fscli.backend.business.filesystem.InodeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;
import java.util.Map;

@JsonTypeName("DIRECTORY")
public class DirectoryNodeDto extends InodeDto implements IDirectoryNodeDto {

    private Map<String, Integer> childrenUids;
    private Integer parentUid;

    public DirectoryNodeDto() {
        super(-1, InodeType.DIRECTORY);
        this.childrenUids = new HashMap<>();
    }

    @JsonCreator
    public DirectoryNodeDto(@JsonProperty("uid") int uid,
                            @JsonProperty("parentUid") Integer parentUid,
                            @JsonProperty("childrenUids") Map<String, Integer> childrenUids) {
        super(uid, InodeType.DIRECTORY);
        this.parentUid = parentUid;
        this.childrenUids = childrenUids != null ? new HashMap<>(childrenUids) : new HashMap<>();
    }

    public DirectoryNodeDto(int uid, InodeType type, Map<String, Integer> childrenUids, Integer parentUid) {
        super(uid, type);
        this.childrenUids = childrenUids;
        this.parentUid = parentUid;
    }

    @Override
    public Map<String, Integer> getChildrenUids() {
        return childrenUids;
    }

    @Override
    public Integer getParentUid() {
        return parentUid;
    }
}
