package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.util.I18nManager;

public interface ControlledView extends DataView, LocalizedView{
    // FIXME: capire se metterli a default o toglierli
    // MenuBarView
    //void initialize(I18nManager i18n, ExitView exitView, CreditsView creditsView, HelpView helpView, PreferenceView preferenceView);
    // CommandLineView
    //void initaliaze(I18nManager i18n, PreferenceController preferenceController);

}
