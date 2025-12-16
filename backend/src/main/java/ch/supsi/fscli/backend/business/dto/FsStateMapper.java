package ch.supsi.fscli.backend.business.dto;

import ch.supsi.fscli.backend.business.filesystem.*;
import ch.supsi.fscli.backend.business.service.ISaveDataService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class FsStateMapper implements IFsStateMapper{
    private final ISaveDataService saveDataService;
    private final FileSystem fileSystem;

    @Inject
    public FsStateMapper(ISaveDataService saveDataService, FileSystem fileSystem) {
        this.saveDataService = saveDataService;
        this.fileSystem = fileSystem;
    }

    @Override
    public void toDTO() {
        IFsStateDto state = mapper();
        saveDataService.save(state);
    }

    @Override
    public void fromDTO(String fileName) {
        IFsStateDto state = saveDataService.load(fileName);
        reconstructFileSystem(state);
    }

    @Override
    public void toDTOas(File file) {
        IFsStateDto state = mapper();
        saveDataService.saveAs(state, file);
    }

    @Override
    public String getCurrentFileAbsolutePath() {
        return saveDataService.getCurrentFileAbsolutePath();
    }

    /**
     * SERIALIZATION:
     * Maps the FileSystem to a FsStateDto
     */
    private IFsStateDto mapper(){
        Map<Integer, InodeDto> inodeTable = new HashMap<>();
        DirectoryNode root = fileSystem.getRoot();
        int currUid = Inode.getIdCounter();

        // Recursively convert all inodes starting from root
        DirectoryNodeDto rootDto = convertDirectoryToDto(root, inodeTable);
        return new FsStateDto(
                rootDto,
                fileSystem.getCurrentDirectory().getUid(),
                currUid,
                inodeTable);
    }

    /**
     * DESERIALIZATION:
     * Reconstructs the entire FileSystem from a DTO
     */
        private void reconstructFileSystem(IFsStateDto state) {
            Map<Integer, Inode> reconstructedInodes = new HashMap<>();

            // 1) creates all inodes without relationships
            for(Map.Entry<Integer, InodeDto> entry : state.getInodeTable().entrySet()) {
                int uid = entry.getKey();
                InodeDto dto = entry.getValue();
                Inode inode = createInodeFromDto(dto);
                reconstructedInodes.put(inode.getUid(), inode);
            }

            // 2) rebuilds all relationships (parent-child for directories)
            for (Map.Entry<Integer, InodeDto> entry : state.getInodeTable().entrySet()) {
                InodeDto dto = entry.getValue();

                if (dto instanceof IDirectoryNodeDto) { // DIR
                    IDirectoryNodeDto dirDto = (IDirectoryNodeDto) dto;
                    DirectoryNode directory = (DirectoryNode) reconstructedInodes.get(dto.getUid());

                    // Set parent reference
                    if (dirDto.getParentUid() != null) {
                        DirectoryNode parent = (DirectoryNode) reconstructedInodes.get(dirDto.getParentUid());
                        directory.setParent(parent);
                    }

                    // Add all children to the directory
                    for (Map.Entry<String, Integer> childEntry : dirDto.getChildrenUids().entrySet()) {
                        String name = childEntry.getKey();
                        Integer childUid = childEntry.getValue();
                        Inode child = reconstructedInodes.get(childUid);

                        if (child != null) {
                            directory.addChild(name, child);
                        }
                    }

                    // Adding  "."  and ".."
                    directory.addChild(".", directory);
                    if (directory.getParent() != null) {
                        directory.addChild("..", directory.getParent());
                    }
                }
            }

            // 3: Restore the FileSystem state using reflection
            DirectoryNode root = (DirectoryNode) reconstructedInodes.get(state.getRoot().getUid());
            DirectoryNode currentDir = (DirectoryNode) reconstructedInodes.get(state.getCurrentDirectoryUid());

            try {
                // Setting final fields in FileSystem
                Field rootField = FileSystem.class.getDeclaredField("root");
                rootField.setAccessible(true);
                rootField.set(fileSystem, root);

                fileSystem.setCurrentDirectory(currentDir);

                Field idCounterField = Inode.class.getDeclaredField("idCounter");
                idCounterField.setAccessible(true);
                idCounterField.set(null, state.getNextInodeId());
            } catch (Exception e) {
                throw new RuntimeException("Failed to restore FileSystem state", e);
            }
        }


    // Creates an Inode object from a DTO (without relationships)
    private Inode createInodeFromDto(InodeDto dto) {
        Inode inode = null;
        if(dto instanceof IFileNodeDto) {
            inode = new FileNode();
        } else if(dto instanceof IDirectoryNodeDto) {
            inode = new DirectoryNode(null);
        } else if(dto instanceof ISoftLinkDto) {
            String targetPath = ((ISoftLinkDto) dto).getTargetPath();
            inode = new SoftLink(targetPath);

        }

        // Override uid and type using reflection
        try {
            Field uidField = Inode.class.getDeclaredField("uid");
            uidField.setAccessible(true);
            uidField.set(inode, dto.getUid());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set UID or type", e);
        }
        inode.setType(dto.getType());
        return inode;
    }
    private DirectoryNodeDto convertDirectoryToDto(DirectoryNode dir, Map<Integer, InodeDto> inodeTable) {
        // Skip if already converted
        if (inodeTable.containsKey(dir.getUid())) {
            return (DirectoryNodeDto) inodeTable.get(dir.getUid());
        }

        Map<String, Integer> childrenUids = new HashMap<>();
        Integer parentUid = dir.getParent() != null ? dir.getParent().getUid() : null;

        // Create DTO for current directory
        DirectoryNodeDto dirDto = new DirectoryNodeDto(dir.getUid(), dir.getType(), childrenUids, parentUid);
        inodeTable.put(dir.getUid(), dirDto);

        // Process children
        for (Map.Entry<String, Inode> entry : dir.getChildren().entrySet()) {
            String name = entry.getKey();
            Inode child = entry.getValue();

            // Skip special entries
            if (name.equals(".") || name.equals("..")) {
                continue;
            }

            childrenUids.put(name, child.getUid());

            // Recursively convert children
            if (child instanceof DirectoryNode) {
                convertDirectoryToDto((DirectoryNode) child, inodeTable);
            } else if (child instanceof FileNode) {
                if (!inodeTable.containsKey(child.getUid())) {
                    inodeTable.put(child.getUid(), new FileNodeDto(child.getUid(), child.getType()));
                }
            } else if (child instanceof SoftLink) {
                if (!inodeTable.containsKey(child.getUid())) {
                    SoftLink link = (SoftLink) child;
                    String targetPath = link.getTargetPath();
                    inodeTable.put(child.getUid(), new SoftLinkDto(child.getUid(), child.getType(), targetPath));
                }
            }
        }

        return dirDto;
    }
}