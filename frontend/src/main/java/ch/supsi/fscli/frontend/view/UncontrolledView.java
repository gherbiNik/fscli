package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.PreferenceController;
import ch.supsi.fscli.frontend.util.I18nManager;

public interface UncontrolledView extends DataView, LocalizedView{
    void initialize(I18nManager i18n, PreferenceController  preferenceController);
}
