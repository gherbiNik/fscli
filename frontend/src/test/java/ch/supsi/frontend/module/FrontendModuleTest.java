package ch.supsi.frontend.module;

import ch.supsi.fscli.backend.application.IPreferenceApplication;
import ch.supsi.fscli.backend.application.filesystem.IFileSystemApplication;
import ch.supsi.fscli.backend.application.mapper.IFsStateMapperApplication;
import ch.supsi.fscli.frontend.controller.IExitController;
import ch.supsi.fscli.frontend.controller.IPreferenceController;
import ch.supsi.fscli.frontend.controller.filesystem.IFileSystemController;
import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.model.*;
import ch.supsi.fscli.frontend.model.filesystem.IFileSystemModel;
import ch.supsi.fscli.frontend.model.mapper.IFsStateMapperModel;
import ch.supsi.fscli.frontend.module.FrontendModule;
import ch.supsi.fscli.frontend.view.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class FrontendModuleTest {


    //Verifica che i Controller si colleghino correttamente ai Model reali e al Backend reale.
    // I Mock delle View servono per evitare l'inizializzazione del toolkit grafico JavaFX.

    @Test
    void testInjectorConfigurationWithMockViews() {
        // 1. Definiamo un modulo che sovrascrive le View reali con dei Mock
        AbstractModule mockViewModule = new AbstractModule() {
            @Override
            protected void configure() {
                // Binding dei Mock per tutte le classi View
                bind(MenuBarView.class).toInstance(mock(MenuBarView.class));
                bind(CommandLineView.class).toInstance(mock(CommandLineView.class));
                bind(OutputView.class).toInstance(mock(OutputView.class));
                bind(LogView.class).toInstance(mock(LogView.class));

                bind(HelpView.class).toInstance(mock(HelpView.class));
                bind(CreditsView.class).toInstance(mock(CreditsView.class));
                bind(ExitView.class).toInstance(mock(ExitView.class));
                bind(PreferenceView.class).toInstance(mock(PreferenceView.class));
                bind(OpenView.class).toInstance(mock(OpenView.class));
                bind(SaveAsView.class).toInstance(mock(SaveAsView.class));
            }
        };

        // 2. Creiamo l'Injector sovrascrivendo il FrontendModule originale
        Injector injector = Guice.createInjector(Modules.override(new FrontendModule()).with(mockViewModule));

        // 3. VERIFICA: Richiediamo i Controller principali
        IFileSystemController fsController = injector.getInstance(IFileSystemController.class);
        assertNotNull(fsController, "Il FileSystemController dovrebbe essere risolto correttamente");

        IPreferenceController prefController = injector.getInstance(IPreferenceController.class);
        assertNotNull(prefController, "Il PreferenceController dovrebbe essere risolto correttamente");

        IFsStateMapperController mapperController = injector.getInstance(IFsStateMapperController.class);
        assertNotNull(mapperController, "Il FsStateMapperController dovrebbe essere risolto correttamente");

        IExitController exitController = injector.getInstance(IExitController.class);
        assertNotNull(exitController, "L'ExitController dovrebbe essere risolto correttamente");
    }

     // Verifica che i Controller possano essere istanziati se i Model sono presenti (anche se finti).
     // Utile per testare la logica di iniezione dei Controller isolandoli dalla business logic.
    @Test
    void testInjectorConfigurationWithMockModels() {
        // 1. Definiamo un modulo che sovrascrive Model e View con Mock
        AbstractModule mockLayerModule = new AbstractModule() {
            @Override
            protected void configure() {
                // Mock dei Model (Dipendenze dei Controller)
                bind(IFileSystemModel.class).toInstance(mock(IFileSystemModel.class));
                bind(IPreferenceModel.class).toInstance(mock(IPreferenceModel.class));
                bind(IFsStateMapperModel.class).toInstance(mock(IFsStateMapperModel.class));
                bind(IExitModel.class).toInstance(mock(IExitModel.class));
                bind(ICommandHelpModel.class).toInstance(mock(ICommandHelpModel.class));
                bind(ITranslationModel.class).toInstance(mock(ITranslationModel.class));

                // Mock delle View (necessari per i Controller che le aggiornano, es. LogView o per rompere cicli)
                bind(MenuBarView.class).toInstance(mock(MenuBarView.class));
                bind(CommandLineView.class).toInstance(mock(CommandLineView.class));
                bind(OutputView.class).toInstance(mock(OutputView.class));
                bind(LogView.class).toInstance(mock(LogView.class));

                bind(HelpView.class).toInstance(mock(HelpView.class));
                bind(CreditsView.class).toInstance(mock(CreditsView.class));
                bind(ExitView.class).toInstance(mock(ExitView.class));
                bind(PreferenceView.class).toInstance(mock(PreferenceView.class));
                bind(OpenView.class).toInstance(mock(OpenView.class));
                bind(SaveAsView.class).toInstance(mock(SaveAsView.class));
            }
        };

        // 2. Creiamo l'Injector
        Injector injector = Guice.createInjector(Modules.override(new FrontendModule()).with(mockLayerModule));

        // 3. VERIFICA: I Controller vengono creati correttamente?
        assertNotNull(injector.getInstance(IFileSystemController.class));
        assertNotNull(injector.getInstance(IPreferenceController.class));
        assertNotNull(injector.getInstance(IFsStateMapperController.class));
        assertNotNull(injector.getInstance(IExitController.class));
    }

     // Verifica che i Model possano essere istanziati se il Backend è presente (anche se finto).
     // Utile per testare che il FrontendModule bindi correttamente i Model alle interfacce del Backend.
    @Test
    void testInjectorConfigurationWithMockBackend() {
        // 1. Sovrascriviamo le interfacce del Backend (I...Application)
        AbstractModule mockBackendModule = new AbstractModule() {
            @Override
            protected void configure() {
                // Sostituiamo il Backend reale con dei Mock.
                // Questo impedisce al test di creare file o leggere preferenze reali su disco.
                bind(IFileSystemApplication.class).toInstance(mock(IFileSystemApplication.class));
                bind(IPreferenceApplication.class).toInstance(mock(IPreferenceApplication.class));
                bind(IFsStateMapperApplication.class).toInstance(mock(IFsStateMapperApplication.class));
            }
        };

        // 2. Creiamo l'Injector
        // Nota: FrontendModule installa BackendModule, ma .with(mockBackendModule) vince sulle definizioni del Backend.
        Injector injector = Guice.createInjector(Modules.override(new FrontendModule()).with(mockBackendModule));

        // 3. VERIFICA: I Model vengono creati correttamente?
        assertNotNull(injector.getInstance(IFileSystemModel.class), "FileSystemModel non creato");
        assertNotNull(injector.getInstance(IFsStateMapperModel.class), "FsStateMapperModel non creato");
        assertNotNull(injector.getInstance(IExitModel.class), "ExitModel non creato");
        assertNotNull(injector.getInstance(ICommandHelpModel.class), "CommandHelpModel non creato");

        // Questo model è il più importante da verificare in questo contesto, perché dipende pesantemente dal backend
        assertNotNull(injector.getInstance(IPreferenceModel.class), "PreferenceModel non creato");
    }
}