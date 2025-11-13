package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.CommandHelpModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CreditsView;
import ch.supsi.fscli.frontend.view.HelpView;

public class HelpController {
    private static HelpController instance;
    private HelpView view;
    private I18nManager i18nManager;
    private CommandHelpModel model;

    private HelpController(){}

    public HelpController getInstance(HelpView view, I18nManager i18nManager){
        if(instance == null){
            instance = new HelpController();
            instance.initialize(view, i18nManager);
            updateView();
        }
        return instance;
    }

    private void initialize(HelpView view, I18nManager i18nManager)
    {
        this.view = view;
        this.i18nManager = i18nManager;
    }

    private void updateView() {
        view.setCommandDescriptions(model.getCommandDescriptions());
    }
}
