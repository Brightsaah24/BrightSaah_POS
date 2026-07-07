import ui.SmartPosFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // The custom UI styles still provide a consistent appearance.
            }
            new SmartPosFrame().setVisible(true);
        });
    }
}
