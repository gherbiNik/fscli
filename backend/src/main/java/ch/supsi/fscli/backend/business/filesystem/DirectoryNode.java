package ch.supsi.fscli.backend.business.filesystem;

import java.util.*;

public class DirectoryNode extends Inode implements IDirectoryNode {

    private final Map<String, Inode>  children;

    public DirectoryNode(DirectoryNode parent) {
        super(InodeType.DIRECTORY);
        this.children = new HashMap<>();
        children.put(".", this);
        children.put("..", Objects.requireNonNullElse(parent, this));
    }

    public Set<String> getChildNames() {
        return Collections.unmodifiableSet(children.keySet());
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
    public DirectoryNode getCurrentDirectory() {
        return (DirectoryNode) children.get(".");
    }

    public DirectoryNode getParent() {
        Inode parent =  children.get("..");
        if (parent == this) return null;
        return (DirectoryNode) parent;
    }

    public Map<String, Inode> getChildren() {
        return children;
    }



    @Override
    public String toString() {
        // Usiamo uno StringBuilder per efficienza
        StringBuilder sb = new StringBuilder();

        // Aggiungiamo i dati del nodo corrente (definiti in Inode)
        sb.append(super.toString());

        sb.append(" { content=[ ");

        // Iteriamo sui figli per stamparli
        if (children != null) {
            String childrenString = children.entrySet().stream()
                    // . e ..
                    .filter(entry -> !entry.getKey().equals(".") && !entry.getKey().equals(".."))
                    // Per gli altri, chiamiamo il loro toString() (ricorsione sicura)
                    .map(entry -> entry.getKey() + "=" + entry.getValue().toString()+"}")
                    .collect(java.util.stream.Collectors.joining(", "));

            sb.append(childrenString);
        }

        sb.append(" ] }");
        return sb.toString();
    }

    public void setParent(DirectoryNode targetDir) {
        children.put("..", targetDir);
    }
}
