package lab02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static utils.JFrameUtils.centerAndNormalizeFrame;

public class ResizableFrame extends JFrame {

    private JTextField jTextField;
    private float highlightOpacity;
    private float goalHighlightOpacity;

    public ResizableFrame() {
        super("RESIZE ME");

        jTextField = new JTextField();
        jTextField.setEditable(false);
        jTextField.setBackground(Color.WHITE);
        jTextField.setHorizontalAlignment(SwingConstants.CENTER);
        jTextField.setFont(jTextField.getFont().deriveFont(Toolkit.getDefaultToolkit().getScreenSize().height / 20f));

        this.add(jTextField);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Component component = e.getComponent();
                jTextField.setText(component.getWidth() + "x" + component.getHeight());
                goalHighlightOpacity = 1;
            }
        });

        new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    highlightOpacity = highlightOpacity*0.99f + goalHighlightOpacity*0.01f;
                    goalHighlightOpacity *= 0.9;
                    jTextField.setBackground(new Color(255, (int)(255*(1-highlightOpacity)), (int)(255*(1-highlightOpacity))));
                    jTextField.setForeground(new Color((int) (highlightOpacity * 255), (int) (highlightOpacity * 255), (int) (highlightOpacity * 255)));
                    Thread.sleep(16);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        centerAndNormalizeFrame(this);
    }

}
