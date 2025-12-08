package ch.supsi.frontend.controller;

import ch.supsi.fscli.frontend.controller.CreditsController;
import ch.supsi.fscli.frontend.util.I18nManager;
import ch.supsi.fscli.frontend.view.CreditsView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CreditsControllerTest {

    @Mock
    private I18nManager i18nManager;
    @Mock
    private CreditsView creditsView;

    private CreditsController creditsController;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        resetSingleton(CreditsController.class, "instance");

        // Mock I18nManager returns
        when(i18nManager.getString(anyString())).thenAnswer(inv -> "translated_" + inv.getArgument(0));

        creditsController = CreditsController.getInstance(i18nManager, creditsView);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) throws Exception {
        Field instance = clazz.getDeclaredField(fieldName);
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testGetInstance() {
        assertNotNull(creditsController);
        assertSame(creditsController, CreditsController.getInstance(i18nManager, creditsView));
    }

    @Test
    void testInitializationUpdatesView() {
        // Initialization happens in getInstance. Verify interactions.
        verify(creditsView).setStageTitle("translated_credits.name");
        verify(creditsView).setAppName("translated_credits.appname");
        verify(creditsView).setFrontendVersion("translated_credits.version");
        verify(creditsView).setFrontendBuildDate("translated_app.buildDate");
        verify(creditsView).setBackendVersion("translated_backend.version");
        verify(creditsView).setBackendBuildDate("translated_backend.buildDate");
        verify(creditsView).setAuthorLabel("translated_credits.devteam");
    }
}