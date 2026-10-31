package ftms.view.users.common;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel valueLabel;

    public StatCard(String value, String caption) {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        valueLabel = new JLabel(value);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 26f));
        add(valueLabel, BorderLayout.CENTER);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setForeground(new Color(90, 90, 90));
        add(captionLabel, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}