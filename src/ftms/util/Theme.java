package ftms.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Global Theme and Color Palette for FTAMS.
 * Implements a classic monochrome palette:
 * - INK (#1E1E1E): Sidebar, table headers, primary buttons
 * - INK_SOFT (#333333): Hover states, secondary dark accents
 * - CLOUD (#F4F4F5): Content background, page background
 * - WHITE (#FFFFFF): Cards, table rows, text on dark
 * - GREY_LINE (#C8C8C8): Borders and subtle dividers
 * - GREY_TEXT (#5A5A5A): Captions, secondary labels, subtitles
 */
public final class Theme {

    private Theme() {}

    // Color Tokens
    public static final Color INK = new Color(0x1E, 0x1E, 0x1E);        // #1E1E1E: sidebar, table headers, primary buttons
    public static final Color INK_SOFT = new Color(0x33, 0x33, 0x33);   // #333333: hover state
    public static final Color CLOUD = new Color(0xF4, 0xF4, 0xF5);      // #F4F4F5: content background
    public static final Color WHITE = new Color(0xFF, 0xFF, 0xFF);      // #FFFFFF: cards, table rows, text on dark
    public static final Color GREY_LINE = new Color(0xC8, 0xC8, 0xC8);  // #C8C8C8: borders (matches StatCard)
    public static final Color GREY_TEXT = new Color(0x5A, 0x5A, 0x5A);  // #5A5A5A: captions (matches StatCard)

    // Typography
    public static final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font FONT_CAPTION = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font FONT_STAT_VALUE = new Font(Font.SANS_SERIF, Font.BOLD, 26);

    // Borders
    public static Border createLineBorder() {
        return BorderFactory.createLineBorder(GREY_LINE, 1);
    }

    public static Border createCardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREY_LINE, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        );
    }

    public static Border createFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREY_LINE, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );
    }

    public static Border createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(GREY_LINE, 1),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONT_BOLD,
                INK
        );
    }

    /**
     * Styles a primary button (dark charcoal background, white text, subtle hover).
     */
    public static void stylePrimaryButton(JButton button) {
        if (button == null) return;
        button.setUI(new BasicButtonUI());
        button.setFont(FONT_BOLD);
        button.setBackground(INK);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(INK_SOFT, 1),
                BorderFactory.createEmptyBorder(7, 16, 7, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        // Avoid adding duplicate hover listeners
        if (!Boolean.TRUE.equals(button.getClientProperty("themeHoverAttached"))) {
            button.putClientProperty("themeHoverAttached", Boolean.TRUE);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(INK_SOFT);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(INK);
                    }
                }
            });
        }
    }

    /**
     * Styles a secondary button (white background, dark text, grey border).
     */
    public static void styleSecondaryButton(JButton button) {
        if (button == null) return;
        button.setUI(new BasicButtonUI());
        button.setFont(FONT_BODY);
        button.setBackground(WHITE);
        button.setForeground(INK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GREY_LINE, 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        if (!Boolean.TRUE.equals(button.getClientProperty("themeHoverAttached"))) {
            button.putClientProperty("themeHoverAttached", Boolean.TRUE);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(CLOUD);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(WHITE);
                    }
                }
            });
        }
    }

    /**
     * Styles navigation buttons for the dark sidebar.
     */
    public static void styleSidebarButton(JButton button) {
        if (button == null) return;
        button.setUI(new BasicButtonUI());
        button.setFont(FONT_BOLD);
        button.setBackground(INK);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(45, 45, 45)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        if (!Boolean.TRUE.equals(button.getClientProperty("themeHoverAttached"))) {
            button.putClientProperty("themeHoverAttached", Boolean.TRUE);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(INK_SOFT);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(INK);
                }
            });
        }
    }

    /**
     * Styles a JTable with dark header and clean white rows.
     */
    public static void styleTable(JTable table) {
        if (table == null) return;
        table.setBackground(WHITE);
        table.setForeground(INK);
        table.setGridColor(GREY_LINE);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setRowHeight(30);
        table.setFont(FONT_BODY);
        table.setSelectionBackground(INK_SOFT);
        table.setSelectionForeground(WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(INK);
            header.setForeground(WHITE);
            header.setFont(FONT_BOLD);
            header.setOpaque(true);
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));
            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                               boolean isSelected, boolean hasFocus, int row, int col) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                    label.setBackground(INK);
                    label.setForeground(WHITE);
                    label.setFont(FONT_BOLD);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 1, GREY_LINE),
                            BorderFactory.createEmptyBorder(4, 8, 4, 8)
                    ));
                    label.setOpaque(true);
                    return label;
                }
            });
        }

        // Clean cell padding
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        table.setDefaultRenderer(Object.class, cellRenderer);
    }

    /**
     * Attaches right-click context menu (Cut, Copy, Paste, Select All) to text components.
     */
    public static void attachContextMenu(javax.swing.text.JTextComponent comp) {
        if (comp == null || Boolean.TRUE.equals(comp.getClientProperty("themeContextMenu"))) return;
        comp.putClientProperty("themeContextMenu", Boolean.TRUE);

        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(WHITE);
        menu.setBorder(createLineBorder());

        JMenuItem cutItem = new JMenuItem(new javax.swing.text.DefaultEditorKit.CutAction());
        cutItem.setText("Cut");

        JMenuItem copyItem = new JMenuItem(new javax.swing.text.DefaultEditorKit.CopyAction());
        copyItem.setText("Copy");

        JMenuItem pasteItem = new JMenuItem(new javax.swing.text.DefaultEditorKit.PasteAction());
        pasteItem.setText("Paste");

        JMenuItem selectAllItem = new JMenuItem("Select All");
        selectAllItem.addActionListener(e -> comp.selectAll());

        menu.add(cutItem);
        menu.add(copyItem);
        menu.add(pasteItem);
        menu.addSeparator();
        menu.add(selectAllItem);

        comp.setComponentPopupMenu(menu);
    }

    /**
     * Styles a text component and enables macOS (⌘) and Windows/Linux (Ctrl) copy/paste/cut/selectAll shortcuts.
     */
    public static void styleTextComponent(javax.swing.text.JTextComponent comp) {
        if (comp == null) return;
        comp.setBackground(WHITE);
        comp.setForeground(INK);
        comp.setCaretColor(INK);
        comp.setFont(FONT_BODY);
        if (comp instanceof JTextField) {
            comp.setBorder(createFieldBorder());
        }

        // Enable Mac Command (⌘) and Ctrl shortcuts
        InputMap im = comp.getInputMap(JComponent.WHEN_FOCUSED);
        int meta = java.awt.event.KeyEvent.META_DOWN_MASK;
        int ctrl = java.awt.event.KeyEvent.CTRL_DOWN_MASK;

        // Command shortcuts for Mac
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, meta), javax.swing.text.DefaultEditorKit.copyAction);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, meta), javax.swing.text.DefaultEditorKit.pasteAction);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, meta), javax.swing.text.DefaultEditorKit.cutAction);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, meta), javax.swing.text.DefaultEditorKit.selectAllAction);

        // Control shortcuts for PC / Linux
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ctrl), javax.swing.text.DefaultEditorKit.copyAction);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ctrl), javax.swing.text.DefaultEditorKit.pasteAction);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ctrl), javax.swing.text.DefaultEditorKit.cutAction);
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ctrl), javax.swing.text.DefaultEditorKit.selectAllAction);

        attachContextMenu(comp);
    }

    /**
     * Styles a text input field.
     */
    public static void styleTextField(JTextField field) {
        styleTextComponent(field);
    }

    /**
     * Styles a combo box.
     */
    public static void styleComboBox(JComboBox<?> box) {
        if (box == null) return;
        box.setBackground(WHITE);
        box.setForeground(INK);
        box.setFont(FONT_BODY);
    }

    /**
     * Recursively walks a component tree and applies the monochrome theme.
     */
    public static void applyTheme(Component comp) {
        if (comp == null) return;

        if (comp instanceof JTable) {
            styleTable((JTable) comp);
        } else if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            // Only style if not already styled as sidebar button
            if (!Boolean.TRUE.equals(btn.getClientProperty("isSidebarBtn"))) {
                stylePrimaryButton(btn);
            }
        } else if (comp instanceof javax.swing.text.JTextComponent) {
            styleTextComponent((javax.swing.text.JTextComponent) comp);
        } else if (comp instanceof JComboBox) {
            styleComboBox((JComboBox<?>) comp);
        } else if (comp instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) comp;
            sp.setBackground(CLOUD);
            sp.setBorder(BorderFactory.createLineBorder(GREY_LINE, 1));
            sp.getViewport().setBackground(WHITE);
        } else if (comp instanceof JCheckBox) {
            JCheckBox cb = (JCheckBox) comp;
            cb.setForeground(INK);
            if (cb.getParent() != null) {
                cb.setBackground(cb.getParent().getBackground());
            }
        } else if (comp instanceof JPanel) {
            JPanel p = (JPanel) comp;
            Border border = p.getBorder();
            if (border instanceof TitledBorder) {
                TitledBorder tb = (TitledBorder) border;
                p.setBorder(createTitledBorder(tb.getTitle()));
                p.setBackground(WHITE);
                p.setOpaque(true);
            } else if (!Boolean.TRUE.equals(p.getClientProperty("customBackground"))) {
                // If nested inside a white container, inherit white
                if (p.getParent() != null && WHITE.equals(p.getParent().getBackground())) {
                    p.setBackground(WHITE);
                } else {
                    p.setBackground(CLOUD);
                }
            }
        } else if (comp instanceof JLabel) {
            JLabel lbl = (JLabel) comp;
            if (lbl.getForeground().equals(Color.BLACK) || lbl.getForeground().equals(new Color(0, 0, 0))) {
                lbl.setForeground(INK);
            }
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                applyTheme(child);
            }
        }
    }

    /**
     * Enables standard clipboard keybindings for both macOS (Command ⌘) and Windows/Linux (Control).
     */
    public static void enableStandardKeybindings() {
        int meta = java.awt.event.KeyEvent.META_DOWN_MASK;
        int ctrl = java.awt.event.KeyEvent.CTRL_DOWN_MASK;

        String[] components = {
                "TextField.focusInputMap",
                "PasswordField.focusInputMap",
                "TextArea.focusInputMap",
                "TextPane.focusInputMap",
                "EditorPane.focusInputMap",
                "FormattedTextField.focusInputMap"
        };

        for (String comp : components) {
            InputMap im = (InputMap) UIManager.get(comp);
            if (im != null) {
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, meta), javax.swing.text.DefaultEditorKit.copyAction);
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, meta), javax.swing.text.DefaultEditorKit.pasteAction);
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, meta), javax.swing.text.DefaultEditorKit.cutAction);
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, meta), javax.swing.text.DefaultEditorKit.selectAllAction);

                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, ctrl), javax.swing.text.DefaultEditorKit.copyAction);
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, ctrl), javax.swing.text.DefaultEditorKit.pasteAction);
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, ctrl), javax.swing.text.DefaultEditorKit.cutAction);
                im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, ctrl), javax.swing.text.DefaultEditorKit.selectAllAction);
            }
        }
    }

    /**
     * Configures UIManager global defaults for the application.
     */
    public static void applyGlobalTheme() {
        try {
            // Set cross-platform look and feel for uniform behavior across macOS, Windows, Linux
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Enable Command+V / Command+C / Ctrl+V / Ctrl+C keybindings
        enableStandardKeybindings();

        // Panel and general surfaces
        UIManager.put("Panel.background", CLOUD);
        UIManager.put("OptionPane.background", CLOUD);
        UIManager.put("OptionPane.messageForeground", INK);

        // Labels
        UIManager.put("Label.foreground", INK);
        UIManager.put("Label.font", FONT_BODY);

        // Buttons
        UIManager.put("Button.background", INK);
        UIManager.put("Button.foreground", WHITE);
        UIManager.put("Button.font", FONT_BOLD);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));

        // Tables
        UIManager.put("Table.background", WHITE);
        UIManager.put("Table.foreground", INK);
        UIManager.put("Table.gridColor", GREY_LINE);
        UIManager.put("Table.selectionBackground", INK_SOFT);
        UIManager.put("Table.selectionForeground", WHITE);
        UIManager.put("Table.font", FONT_BODY);
        UIManager.put("TableHeader.background", INK);
        UIManager.put("TableHeader.foreground", WHITE);
        UIManager.put("TableHeader.font", FONT_BOLD);

        // Inputs
        UIManager.put("TextField.background", WHITE);
        UIManager.put("TextField.foreground", INK);
        UIManager.put("TextField.caretForeground", INK);
        UIManager.put("TextField.font", FONT_BODY);
        UIManager.put("PasswordField.background", WHITE);
        UIManager.put("PasswordField.foreground", INK);
        UIManager.put("PasswordField.caretForeground", INK);
        UIManager.put("TextArea.background", WHITE);
        UIManager.put("TextArea.foreground", INK);
        UIManager.put("TextArea.font", FONT_BODY);

        // ScrollPane & Viewport
        UIManager.put("ScrollPane.background", CLOUD);
        UIManager.put("Viewport.background", WHITE);

        // Titled Border
        UIManager.put("TitledBorder.titleColor", INK);
        UIManager.put("TitledBorder.font", FONT_BOLD);
    }
}
