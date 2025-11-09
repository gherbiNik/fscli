package ch.supsi.fscli.backend.business.filesystem;

import java.util.HashMap;
import java.util.Map;

public class DirectoryNode extends Inode implements IDirectoryNode {

    private final Map<String, Inode>  children;

    public DirectoryNode(DirectoryNode parent) {
        super(parent, InodeType.DIRECTORY);
        this.children = new HashMap<>();
    }

    public void addChildren(String name, Inode child){
        this.children.put(name, child);
    }

    @Override
    public String toString() {
        return super.toString()+"DirectoryNode{" +
                "children=" + children +
                '}';
    }
}
