package ftms.view.users.common;

import ftms.util.Theme;

import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    private final JLabel valueLabel;

    public StatCard(String value, String caption) {
        setLayout(new BorderLayout(6, 6));
        setBackground(Theme.WHITE);
        setOpaque(true);
        putClientProperty("customBackground", Boolean.TRUE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GREY_LINE, 1),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        valueLabel = new JLabel(value);
        valueLabel.setFont(Theme.FONT_STAT_VALUE);
        valueLabel.setForeground(Theme.INK);
        add(valueLabel, BorderLayout.CENTER);

        JLabel captionLabel = new JLabel(caption);
        captionLabel.setFont(Theme.FONT_CAPTION);
        captionLabel.setForeground(Theme.GREY_TEXT);
        add(captionLabel, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}