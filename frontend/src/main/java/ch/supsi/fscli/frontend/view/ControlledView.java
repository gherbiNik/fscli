package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.util.I18nManager;

public interface ControlledView extends DataView, LocalizedView{
    void initialize(I18nManager i18n, ExitView exitView, CreditsView creditsView, HelpView helpView, PreferenceView preferenceView);
}
