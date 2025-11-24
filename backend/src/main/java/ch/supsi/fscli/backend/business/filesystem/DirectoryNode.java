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
        System.out.println(children.keySet());
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

    public String getINodeName(Inode inode) { // <<< NOTE: Return type changed from 'void'
        // Iterate over all entries (name, node) in the children map
        for (Map.Entry<String, Inode> entry : children.entrySet()) {

            // Check if the value (the Inode) matches the one we're looking for
            if (entry.getValue().equals(inode)) {

                // If it matches, return the corresponding key (the name)
                return entry.getKey();
            }
        }

        // If no match is found after checking all children, return null
        return null;
    }

    public Map<String, Inode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return super.toString()+"{" +
                "children=" + children +
                '}';
    }
}
