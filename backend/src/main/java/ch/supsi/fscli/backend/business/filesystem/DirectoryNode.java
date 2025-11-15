package ch.supsi.fscli.backend.business.filesystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DirectoryNode extends Inode implements IDirectoryNode {

    private final Map<String, Inode>  children;

    public DirectoryNode(DirectoryNode parent) {
        super(parent, InodeType.DIRECTORY);
        this.children = new HashMap<>();
    }

    public Set<String> getChildNames() {
        return children.keySet();
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
    public Inode getChild(String childName) {
        return children.get(childName);
    }

    @Override
    public void addChild(String childName, Inode newNode) {
        children.put(childName, newNode);
    }



    @Override
    public String toString() {
        return super.toString()+"DirectoryNode{" +
                "children=" + children +
                '}';
    }
}
