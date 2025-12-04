package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.ICommandHelpModel;
import ch.supsi.fscli.frontend.view.HelpView;

public class HelpController {
    private static HelpController instance;
    private HelpView view;
    private ICommandHelpModel model;

    private HelpController(){}

    public static HelpController getInstance(HelpView view, ICommandHelpModel model){
        if(instance == null){
            instance = new HelpController();
            instance.initialize(view, model);
        }
        return instance;
    }

    private void initialize(HelpView view, ICommandHelpModel model)
    {
        this.view = view;
        this.model = model;
        updateView();
    }

    private void updateView() {
        if (view != null && model != null) {
            // Recupera le stringhe dal model (che le prende dal backend) e le setta nella view
            view.setCommandDescriptions(model.getCommandDescriptions());
        }
    }
}