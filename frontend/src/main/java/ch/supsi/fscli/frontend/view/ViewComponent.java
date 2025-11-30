package ch.supsi.fscli.frontend.view;

import javafx.scene.Node;

public interface ViewComponent {
    /**
     * Restituisce il nodo grafico root di questo componente per il layout.
     */
    Node getNode();

}
