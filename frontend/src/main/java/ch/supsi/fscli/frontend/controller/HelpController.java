package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.model.ICommandHelpModel;
import ch.supsi.fscli.frontend.view.HelpView;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class HelpController {
    private final HelpView helpView;
    private final ICommandHelpModel commandHelpModel;

    @Inject
    public HelpController(HelpView helpView, ICommandHelpModel commandHelpModel) {
        this.helpView = helpView;
        this.commandHelpModel = commandHelpModel;
        updateView();
    }

    private void updateView() {
        if (helpView != null && helpView != null) {
            // Recupera le stringhe dal model (che le prende dal backend) e le setta nella view
            helpView.setCommandDescriptions(commandHelpModel.getCommandDescriptions());
        }
    }
}