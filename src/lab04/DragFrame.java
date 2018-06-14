package lab04;

import utils.JFrameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.util.ArrayList;

public class DragFrame extends JFrame {
    public DragFrame() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLayout(new BorderLayout());

        java.util.List<String> list1 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list1.add("Lefts " + i);
        }
        java.util.List<String> list2 = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list2.add("Rights " + i);
        }

        DragColumn dragColumn1 = new DragColumn(screenSize.width / 4, list1);
        DragColumn dragColumn2 = new DragColumn(screenSize.width / 4, list2);

//        dragColumn1.setPreferredSize(new Dimension(1200, list1.size()*100));
//        dragColumn2.setPreferredSize(new Dimension(1200, list1.size()*100));

        this.add(new JScrollPane(dragColumn1), BorderLayout.WEST);
        this.add(new JScrollPane(dragColumn2), BorderLayout.EAST);
        JFrameUtils.centerAndNormalizeFrame(this);
    }

    private class DragColumn extends JPanel {
        final java.util.List<String> list;

        private DragColumn(int width, java.util.List<String> list) {
            this.list = list;
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            this.setPreferredSize(new Dimension(width, 20));
            updateItems();
        }

        void updateItems() {
            int index = 0;
            this.removeAll();
            for (String s : list) {
                this.add(new DraggableTextField(s, index++));
            }
            this.setPreferredSize(new Dimension(1200, list.size()*100));
            this.updateUI();
        }

        private class DraggableTextField extends JTextField implements
                DragGestureListener, DragSourceListener, DropTargetListener {
            final DropTarget dropTarget;
            final DragSource dragSource;
            final String text;
            final int indexInList;

            final Color dragOverColor = Color.GREEN;

            private DraggableTextField(String text, int indexInList) {
                super(text);
                this.text = text;
                this.indexInList = indexInList;
                Dimension goalSize = new Dimension(1300, 100);

                this.setPreferredSize(goalSize);
                this.setMinimumSize(goalSize);
                this.setMaximumSize(goalSize);

                setHighlighter(null);
                setFont(getFont().deriveFont(75f));
                setEditable(false);

                dragSource = DragSource.getDefaultDragSource();
                dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
                dragSource.addDragSourceListener(this);

                dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY, this, true);
                this.setBackground(Color.WHITE);
            }

            @Override
            public void dragGestureRecognized(DragGestureEvent dge) {
                System.out.println("dragGestureRecognized " + dge);
                try {
                    list.remove(indexInList);
                    dge.startDrag(DragSource.DefaultCopyNoDrop, new StringTransferable(text));
                    updateItems();
                } catch (InvalidDnDOperationException idoe) {
                    System.err.println(idoe);
                }
            }

            @Override
            public void dragEnter(DragSourceDragEvent dsde) {
//                System.out.println("dragEnter " + dsde);
            }

            @Override
            public void dragOver(DragSourceDragEvent dsde) {
//                System.out.println("dragOver " + dsde);
            }

            @Override
            public void dropActionChanged(DragSourceDragEvent dsde) {
//                System.out.println("dropActionChanged " + dsde);
            }

            @Override
            public void dragExit(DragSourceEvent dse) {
//                System.out.println("dragExit " + text);
            }

            @Override
            public void dragDropEnd(DragSourceDropEvent dsde) {
//                System.out.println("dragDropEnd " + text);
            }

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
//                System.out.println("dragEnter " + dtde);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
//                System.out.println("dragOver " + dtde);
                this.setText(text + "\n" + StringTransferable.extractString(dtde.getTransferable()));
                this.setBackground(dragOverColor);
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
//                System.out.println("dropActionChanged " + dtde);
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
//                System.out.println("dragExit " + dte);
                this.setText(text);
                this.setBackground(Color.WHITE);
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                String item = StringTransferable.extractString(dtde.getTransferable());
                if (item != null) {
                    list.add(indexInList + 1, item);
                    updateItems();
                }
                this.setBackground(Color.WHITE);
            }
        }
    }
}
