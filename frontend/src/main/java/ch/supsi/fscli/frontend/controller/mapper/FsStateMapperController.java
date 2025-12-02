package ch.supsi.fscli.frontend.controller.mapper;

import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import ch.supsi.fscli.frontend.model.mapper.IFsStateMapperModel;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.MenuBarView;

import java.io.File;


public class FsStateMapperController implements IFsStateMapperController {
    private static FsStateMapperController instance;
    private IFsStateMapperModel fsStateMapperModel;
    private FileSystemModel fileSystemModel;
    private LogView logView;
    private MenuBarView menuBarView;

    private FsStateMapperController() {
    }

    public static FsStateMapperController getInstance(IFsStateMapperModel fsStateMapperModel,FileSystemModel fileSystemModel) {
        if (instance == null) {
            instance = new FsStateMapperController();
            instance.initialize(fsStateMapperModel, fileSystemModel);
        }
        return instance;
    }

    private void initialize(IFsStateMapperModel fsStateMapperModel, FileSystemModel fileSystemModel) {
        this.fsStateMapperModel = fsStateMapperModel;
        this.fileSystemModel = fileSystemModel;
    }

    public void initialize(LogView logView, MenuBarView menuBarView){
        this.logView = logView;
        this.menuBarView = menuBarView;
    }

    @Override
    public void save() {
        fsStateMapperModel.save();
        fileSystemModel.setDataToSave(false); // mods has been saved

    }

    @Override
    public void open(String fileName) {
        fsStateMapperModel.open(fileName);
        fileSystemModel.setDataToSave(true);

    }

    @Override
    public void saveAs(File file) {
        fsStateMapperModel.saveAs(file);
        fileSystemModel.setDataToSave(false); // mods has been saved

    }
}