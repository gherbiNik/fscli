package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapper;
import ch.supsi.fscli.frontend.model.IPreferenceModel;
import javafx.stage.FileChooser;

import java.io.File;

public class SaveAsView implements ShowView{

    public static SaveAsView instance;
    private IFsStateMapper iFsStateMapper;
    private IPreferenceModel preferencesModel;

    private SaveAsView() {}
    public static SaveAsView getInstance(IFsStateMapper iFsStateMapper,  IPreferenceModel preferencesModel) {
        if (instance == null) {
            instance = new SaveAsView();
            instance.initialize(iFsStateMapper, preferencesModel);
        }
        return instance;
    }

    private void initialize(IFsStateMapper iFsStateMapper,  IPreferenceModel preferencesModel){
        this.iFsStateMapper = iFsStateMapper;
        this.preferencesModel = preferencesModel;
    }

    @Override
    public void showView() {
        FileChooser fileChooser = new FileChooser();

        String userPreferencesPath = preferencesModel.getUserPreferencesDirectoryPath().toString();
        File initialDirectory = new File(userPreferencesPath);
        if (initialDirectory.exists() && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );


        File selectedfile = fileChooser.showSaveDialog(null);

        if (selectedfile != null) {
            iFsStateMapper.saveAs(selectedfile);
        } else {
            System.out.println("VA STAMPATO ANCHE NEL LOG??\nNessun file selezionato");
            iFsStateMapper.save();
        }
    }
}

