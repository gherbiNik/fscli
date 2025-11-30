package ch.supsi.fscli.frontend.controller.mapper;

import ch.supsi.fscli.frontend.model.mapper.IFsStateMapperModel;
import ch.supsi.fscli.frontend.view.DataView;

import java.io.File;
import java.util.List;


public class FsStateMapperController implements IFsStateMapperController {
    private static FsStateMapperController instance;
    private IFsStateMapperModel fsStateMapperModel;
    private List<DataView> views;

    private FsStateMapperController() {
    }

    public static FsStateMapperController getInstance(IFsStateMapperModel fsStateMapperModel, List<DataView> views) {
        if (instance == null) {
            instance = new FsStateMapperController();
            instance.initialize(fsStateMapperModel, views);
        }
        return instance;
    }

    private void initialize(IFsStateMapperModel fsStateMapperModel, List<DataView> views) {
        this.fsStateMapperModel = fsStateMapperModel;
        this.views = views;
    }

    @Override
    public void save() {
        fsStateMapperModel.save();
        views.forEach(dataView -> dataView.update("DA TRADURRE: fs salvato"));
    }

    @Override
    public void open(String fileName) {
        fsStateMapperModel.open(fileName);
        views.forEach(dataView -> dataView.update("DA TRADURRE: fs open"));
    }

    @Override
    public void saveAs(File file) {
        fsStateMapperModel.saveAs(file);
        views.forEach(dataView -> dataView.update("DA TRADURRE: fs salvato come..."));
    }
}