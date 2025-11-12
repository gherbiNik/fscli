package ch.supsi.fscli.backend.business.filesystem;

import java.util.HashMap;
import java.util.Map;

public class DirectoryNode extends Inode implements IDirectoryNode {

    private final Map<String, Inode>  children;

    public DirectoryNode(DirectoryNode parent) {
        super(parent, InodeType.DIRECTORY);
        this.children = new HashMap<>();
    }


    @Override
    public boolean removeChild(String childName, Inode nodeToRemove) {
        return children.remove(childName, nodeToRemove);
    }


    @Override
    public int getNumChild() {
        return children.size();
    }

    @Override
    public Inode getChild(String directoryName) {
        return children.get(directoryName);
    }

    @Override
    public void addChild(String directoryName, Inode newNode) {
        children.put(directoryName, newNode);
    }

    @Override
    public String toString() {
        return "DirectoryNode{" +
                "children=" + children +
                '}';
    }
}
