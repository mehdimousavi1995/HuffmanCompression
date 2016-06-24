
import Zipiing.UnZipper;
import Zipiing.Zipper;
import Gui.MainFrame;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mehdi
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainFrame UI = new MainFrame();
        UI.setVisible(true);
        redirectConsuledOnUITextArea(UI);
    }

    private static void zip() {
        String source = "C:\\Users\\Mehdi\\Desktop\\__New Text Document.txt";
        String destination = "C:\\Users\\Mehdi\\Desktop\\zippedFile.hzp";
        Zipper zipper = new Zipper(source, destination, null);
        zipper.zip();
    }

    private static void unzip() {
        String source = "C:\\Users\\Mehdi\\Desktop\\zippedFile.hzp";
        String destination = "C:\\Users\\Mehdi\\Desktop\\unZippedFile.docx";
        UnZipper unZipper = new UnZipper(source, destination, null);
        unZipper.unZip();
    }

    private static void redirectConsuledOnUITextArea(final MainFrame UI) {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                UI.printInStatusArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                UI.printInStatusArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

}
