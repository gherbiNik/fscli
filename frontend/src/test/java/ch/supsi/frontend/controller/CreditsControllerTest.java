package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.CreditsController;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CreditsView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// Abbiamo rimosso import java.lang.reflect.Field; e static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreditsControllerTest {

    @Mock
    private I18nManager i18nManager;
    @Mock
    private CreditsView creditsView;

    private CreditsController creditsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock I18nManager returns
        when(i18nManager.getString(anyString())).thenAnswer(inv -> "translated_" + inv.getArgument(0));

        // FIX: Chiamiamo il costruttore direttamente per iniettare i mock.
        creditsController = new CreditsController(i18nManager, creditsView);
    }

    @Test
    void testInitializationUpdatesView() {
        // L'inizializzazione avviene nel costruttore (@Inject). Verifichiamo le interazioni.
        verify(creditsView).setStageTitle("translated_credits.name");
        verify(creditsView).setAppName("translated_credits.appname");
        verify(creditsView).setFrontendVersion("translated_credits.version");
        verify(creditsView).setFrontendBuildDate("translated_app.buildDate");
        verify(creditsView).setBackendVersion("translated_backend.version");
        verify(creditsView).setBackendBuildDate("translated_backend.buildDate");
        verify(creditsView).setAuthorLabel("translated_credits.devteam");
    }
}