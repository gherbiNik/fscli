package ch.supsi.fscli.frontend.module;

import ch.supsi.fscli.backend.module.BackendModule;
import ch.supsi.fscli.frontend.controller.*;
import ch.supsi.fscli.frontend.controller.filesystem.FileSystemController;
import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.controller.mapper.FsStateMapperController;
import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.model.*;
import ch.supsi.fscli.frontend.model.filesystem.FileSystemModel;
import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.model.mapper.FsStateMapperModel;
import ch.supsi.fscli.frontend.model.mapper.IFsStateMapperModel;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.*;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class FrontendModule extends AbstractModule {
    @Override
    protected void configure() {
        // 1. CARICAMENTO DEL BACKEND
        // Questo comando dice a Guice: "Leggi anche tutte le istruzioni del BackendModule"
        install(new BackendModule());

        // 2. UTILITY 🛠️
        bind(I18nManager.class).in(Singleton.class);

        // 3. MODEL (Dati)
        // Colleghiamo Interfaccia -> Implementazione
        bind(ITranslationModel.class).to(TranslationModel.class).in(Singleton.class);
        bind(IPreferenceModel.class).to(PreferenceModel.class).in(Singleton.class);
        bind(ICommandHelpModel.class).to(CommandHelpModel.class).in(Singleton.class);
        bind(IFileSystemModel.class).to(FileSystemModel.class).in(Singleton.class);
        bind(IFsStateMapperModel.class).to(FsStateMapperModel.class).in(Singleton.class);
        bind(IExitModel.class).to(ExitModel.class).in(Singleton.class);

        // 4. CONTROLLER (Logica)
        // Interfacce -> Implementazioni
        bind(IPreferenceController.class).to(PreferenceController.class).in(Singleton.class);
        bind(IFileSystemController.class).to(FileSystemController.class).in(Singleton.class);
        bind(IFsStateMapperController.class).to(FsStateMapperController.class).in(Singleton.class);
        bind(IExitController.class).to(ExitController.class).in(Singleton.class);

        // Controller concreti (senza interfaccia)
        bind(HelpController.class).in(Singleton.class);
        bind(CreditsController.class).in(Singleton.class);

        // 5. VIEW (Grafica)
        // Le view sono classi concrete, le registriamo come Singleton
        // così possiamo iniettarle ovunque servano.
        bind(MenuBarView.class).in(Singleton.class);
        bind(CommandLineView.class).in(Singleton.class);
        bind(OutputView.class).in(Singleton.class);
        bind(LogView.class).in(Singleton.class);

        bind(HelpView.class).in(Singleton.class);
        bind(CreditsView.class).in(Singleton.class);
        bind(ExitView.class).in(Singleton.class);
        bind(PreferenceView.class).in(Singleton.class);
        bind(OpenView.class).in(Singleton.class);
        bind(SaveAsView.class).in(Singleton.class);
    }
}
