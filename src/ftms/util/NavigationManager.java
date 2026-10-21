package ftms.util;

import javax.swing.*;
import java.awt.*;

public class NavigationManager {
    private static JPanel mainContainer;
    private static CardLayout cardLayout;

    public static void initialize(JPanel container, CardLayout layout) {
        mainContainer = container;
        cardLayout = layout;
    }

    public static void showPanel(String panelName) {
        if (cardLayout != null && mainContainer != null) {
            cardLayout.show(mainContainer, panelName);
        }
    }
}