package lab04;

import java.awt.datatransfer.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StringTransferable implements Transferable, ClipboardOwner {
    public static final DataFlavor plainTextFlavor = DataFlavor.plainTextFlavor;
    public static final DataFlavor localStringFlavor = DataFlavor.stringFlavor;
    private static final DataFlavor[] flavors = {
            StringTransferable.plainTextFlavor,
            StringTransferable.localStringFlavor
    };

    private final String string;

    public StringTransferable(String s) {
        this.string = s;
    }

    private static final List flavorList = Arrays.asList(flavors);

    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavorList.contains(flavor));
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(StringTransferable.plainTextFlavor)) {
            String charset = flavor.getParameter("charset").trim();
            if (charset.equalsIgnoreCase("unicode")) {
                System.out.println("returning unicode charset");
                // uppercase U in Unicode here!
                return new ByteArrayInputStream(this.string.getBytes("Unicode"));
            } else {
                System.out.println("returning latin-1 charset");
                return new ByteArrayInputStream(this.string.getBytes("iso8859-1"));
            }
        } else if (StringTransferable.localStringFlavor.equals(flavor)) {
            return this.string;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    static String extractString(final Transferable t) {
        try {
            return (String)t.getTransferData(StringTransferable.localStringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
