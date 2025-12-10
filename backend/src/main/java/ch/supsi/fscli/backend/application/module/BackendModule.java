package ch.supsi.fscli.backend.application.module;

import ch.supsi.fscli.backend.business.command.business.CommandExecutor;
import ch.supsi.fscli.backend.business.command.business.CommandLoader;
import ch.supsi.fscli.backend.business.command.business.CommandParser;
import ch.supsi.fscli.backend.business.command.commands.ICommand;
import ch.supsi.fscli.backend.business.dto.FsStateMapper;
import ch.supsi.fscli.backend.business.dto.IFsStateMapper;
import ch.supsi.fscli.backend.business.filesystem.FileSystem;
import ch.supsi.fscli.backend.business.filesystem.IFileSystem;
import ch.supsi.fscli.backend.business.preferences.IPreferenceBusiness;
import ch.supsi.fscli.backend.business.preferences.PreferenceBusiness;
import ch.supsi.fscli.backend.business.service.FileSystemService;
import ch.supsi.fscli.backend.business.service.IFileSystemService;
import ch.supsi.fscli.backend.business.service.ISaveDataService;
import ch.supsi.fscli.backend.business.service.SaveDataService;
import ch.supsi.fscli.backend.dataAccess.ICommandDAO;
import ch.supsi.fscli.backend.dataAccess.JsonCommandDAO;
import ch.supsi.fscli.backend.dataAccess.filesystem.ISaveData;
import ch.supsi.fscli.backend.dataAccess.filesystem.JacksonSaveDataService;
import ch.supsi.fscli.backend.dataAccess.preferences.IPreferenceDAO;
import ch.supsi.fscli.backend.dataAccess.preferences.PreferenceDAO;
import ch.supsi.fscli.backend.util.BackendTranslator;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.List;

public class BackendModule extends AbstractModule {
    @Override
    protected void configure() {
        // --- Util ---
        bind(BackendTranslator.class).asEagerSingleton();

        // --- Data Access ---
        bind(ICommandDAO.class).to(JsonCommandDAO.class);
        bind(IPreferenceDAO.class).to(PreferenceDAO.class);
        bind(ISaveData.class).to(JacksonSaveDataService.class);

        // --- Business (Domain & Services) ---
        bind(IFileSystem.class).to(FileSystem.class);
        bind(IFileSystemService.class).to(FileSystemService.class);
        bind(ISaveDataService.class).to(SaveDataService.class);
        bind(IPreferenceBusiness.class).to(PreferenceBusiness.class);
        bind(IFsStateMapper.class).to(FsStateMapper.class);

        // Classi concrete
        bind(CommandParser.class).asEagerSingleton();
        bind(CommandLoader.class).asEagerSingleton();
        bind(CommandExecutor.class).asEagerSingleton();
    }

    /**
     * Questo metodo insegna a Guice come fornire una List<ICommand>.
     * Guice inietterà automaticamente il CommandLoader necessario!
     */
    @Provides
    @Singleton
    public List<ICommand> provideCommandList(CommandLoader loader) {
        return loader.loadCommands();
    }
}
