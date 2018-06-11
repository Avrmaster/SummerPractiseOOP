package lab05Plus;

import utils.JFrameUtils;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.function.Consumer;

class Editor extends JFrame {

    Editor() {
        this.setLayout(new GridBagLayout());
        final CanvasPanel canvasPanel = new CanvasPanel();

        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = c.gridy = 0;
        c.weightx = c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(canvasPanel, c);
        c.gridy = 1;
        c.weighty = 0.1;
        c.fill = GridBagConstraints.BOTH;
        this.add(createColorChooser(canvasPanel::setCurrentColor), c);

        this.setJMenuBar(createMenu(
                canvasPanel::saveToFile,
                canvasPanel::loadFromFile,
                canvasPanel::setCurrentShape
        ));
        JFrameUtils.centerAndNormalizeFrame(this, 0.8f);
    }

    private JComponent createColorChooser(final Consumer<Color> onColorPicked) {
        JColorChooser jColorChooser = new JColorChooser();
        AbstractColorChooserPanel[] panels = new AbstractColorChooserPanel[1];
        panels[0] = jColorChooser.getChooserPanels()[3];
        jColorChooser.setChooserPanels(panels);
        jColorChooser.setPreviewPanel(new JPanel());
        jColorChooser.getSelectionModel().addChangeListener(e -> onColorPicked.accept(jColorChooser.getColor()));
        return jColorChooser;
    }

    private JMenuBar createMenu(
            final Consumer<File> onSave,
            final Consumer<File> onLoad,
            final Consumer<Shape> onChooseShape
    ) {
        final JMenuBar menuBar = new JMenuBar();
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        final JMenuItem saveItem = new JMenuItem("Save", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveItem.addActionListener(e ->
                JFrameUtils.promptToSaveFile(fileString ->
                        onSave.accept(new File(
                                fileString.endsWith(".png") ?
                                        fileString : fileString + ".png"
                        )), "png"));
        final JMenuItem loadItem = new JMenuItem("Load", KeyEvent.VK_L);
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        loadItem.addActionListener(e ->
                JFrameUtils.promptToChooseFile(fileString ->
                        onLoad.accept(new File(fileString)), "png"));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        menuBar.add(fileMenu);

        final JMenu shapeMenu = new JMenu("Shape");
        shapeMenu.setMnemonic(KeyEvent.VK_S);


        final ButtonGroup group = new ButtonGroup();
        for (final Shape s : Shape.values()) {
            JRadioButtonMenuItem shapeChooserMenuItem = new JRadioButtonMenuItem(s.toString());
            shapeChooserMenuItem.setSelected(true);
            shapeChooserMenuItem.setMnemonic(s.toString().charAt(0));
            shapeChooserMenuItem.setAccelerator(KeyStroke.getKeyStroke(s.toString().charAt(0), KeyEvent.ALT_MASK));
            shapeChooserMenuItem.addActionListener(e -> onChooseShape.accept(s));
            group.add(shapeChooserMenuItem);
            shapeMenu.add(shapeChooserMenuItem);
        }

        menuBar.add(shapeMenu);

        return menuBar;
    }

}
