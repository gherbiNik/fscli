package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.CommandHelpModel;
import ch.supsi.fscli.frontend.model.ICommandHelpModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CreditsView;
import ch.supsi.fscli.frontend.view.HelpView;

public class HelpController {
    private static HelpController instance;
    private HelpView view;
    private I18nManager i18nManager;
    private ICommandHelpModel model;

    private HelpController(){}

    public static HelpController getInstance(HelpView view, I18nManager i18nManager, ICommandHelpModel model){
        if(instance == null){
            instance = new HelpController();
            instance.initialize(view, i18nManager, model);
        }
        return instance;
    }

    private void initialize(HelpView view, I18nManager i18nManager, ICommandHelpModel model)
    {
        this.view = view;
        this.i18nManager = i18nManager;
        this.model = model;
        updateView();
    }

    private void updateView() {
        view.setCommandDescriptions(model.getCommandDescriptions());
    }
}
