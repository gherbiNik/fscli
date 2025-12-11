package ch.supsi.fscli.frontend.controller.mapper;

import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.model.mapper.IFsStateMapperModel;
import ch.supsi.fscli.frontend.view.LogView;
import ch.supsi.fscli.frontend.view.MenuBarView;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.io.File;

@Singleton
public class FsStateMapperController implements IFsStateMapperController {

    private final IFsStateMapperModel fsStateMapperModel;
    private final IFileSystemModel fileSystemModel; // Usiamo l'interfaccia!

    // LogView non crea dipendenze circolari, quindi può stare nel costruttore
    private final LogView logView;

    // MenuBarView crea dipendenza circolare, la iniettiamo col setter
    private Provider<MenuBarView> menuBarViewProvider;

    @Inject
    public FsStateMapperController(IFsStateMapperModel fsStateMapperModel, IFileSystemModel fileSystemModel, LogView logView) {
        this.fsStateMapperModel = fsStateMapperModel;
        this.fileSystemModel = fileSystemModel;
        this.logView = logView;
    }

    // 3. Setter Injection per rompere il ciclo con MenuBarView
    @Inject
    public void setMenuBarView(Provider<MenuBarView> menuBarViewProvider) {
        this.menuBarViewProvider = menuBarViewProvider;
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