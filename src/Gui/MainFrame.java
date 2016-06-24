/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gui;

import Zipiing.UnZipper;
import Zipiing.Zipper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 *
 * @author Mehdi
 */
public class MainFrame extends JFrame implements ActionListener {

    JTextField source;
    JTextField destination;
    JButton chooseSource;
    JButton chooseDestination;
    JTextArea status;
    JProgressBar progress;
    JLabel progressValue;
    JButton zip;
    JButton unZip;

    public MainFrame() throws HeadlessException {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(350, 485));
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setTitle("HuffmanCompression :)");

        source = new JTextField("Source File Path ...");
        source.setPreferredSize(new Dimension(260, 25));
        source.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (source.getText().trim().equals("Source File Path ...")) {
                    source.setText("");
                }
            }

        });
        source.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (source.getText().trim().equals("")) {
                    source.setText("Source File Path ...");
                }
            }
        });
        add(source);

        chooseSource = new JButton("Select");
        chooseSource.setMargin(new Insets(0, 0, 0, 0));
        chooseSource.setPreferredSize(new Dimension(60, 25));
        chooseSource.addActionListener(this);
        add(chooseSource);

        destination = new JTextField("Destination Path ...");
        destination.setPreferredSize(new Dimension(260, 25));
        destination.addActionListener(this);
        destination.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (destination.getText().trim().equals("Destination Path ...")) {
                    destination.setText("");
                }
            }

        });
        destination.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (destination.getText().trim().equals("")) {
                    destination.setText("Destination Path ...");
                }
            }
        });
        add(destination);
        chooseDestination = new JButton("Select");
        chooseDestination.setMargin(new Insets(0, 0, 0, 0));
        chooseDestination.setPreferredSize(new Dimension(60, 25));
        chooseDestination.addActionListener(this);
        add(chooseDestination);

        status = new JTextArea("");
        status.setEditable(false);
        JScrollPane displayStatus = new JScrollPane(status);
        displayStatus.setPreferredSize(new Dimension(330, 300));
        displayStatus.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(displayStatus);

        progress = new JProgressBar(0, 100);
        progress.setPreferredSize(new Dimension(290, 20));
        add(progress);
        progress.setValue(0);

        progressValue = new JLabel("0%");
        progressValue.setPreferredSize(new Dimension(30, 20));
        add(progressValue);

        zip = new JButton("Zip");
        zip.setPreferredSize(new Dimension(160, 25));
        zip.addActionListener(this);
        add(zip);

        unZip = new JButton("UnZip");
        unZip.setPreferredSize(new Dimension(160, 25));
        unZip.addActionListener(this);
        add(unZip);
    }

    public void printInStatusArea(final String s) {
        status.append(s);
        status.setCaretPosition(status.getDocument().getLength());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object clickedObj = e.getSource();
        if (clickedObj == chooseDestination) {
            showPathChooser();
        } else if (clickedObj == chooseSource) {
            showFileChooser();
        } else if (clickedObj == zip) {
            if (validatePathsForZipp()) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        new Zipper(source.getText(), destination.getText(), MainFrame.this).zip();
                    }
                }).start();

            }
        } else if (clickedObj == unZip) {
            if (validatePathsForUnZipp()) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        new UnZipper(source.getText(), destination.getText(), MainFrame.this).unZip();
                    }
                }).start();
            }
        }
    }

    private void showPathChooser() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int pressedButton = fileChooser.showSaveDialog(this);
        if (pressedButton == JFileChooser.APPROVE_OPTION) {
            destination.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void showFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter1 = new FileNameExtensionFilter("Text File(.txt)", new String[]{"txt"});
        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("Mehdi Zipped File(.hzp)", new String[]{"hzp"});
        fileChooser.addChoosableFileFilter(filter1);
        fileChooser.addChoosableFileFilter(filter2);
        fileChooser.setFileFilter(filter2);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int pressedButton = fileChooser.showSaveDialog(this);
        if (pressedButton == JFileChooser.APPROVE_OPTION) {
            source.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private boolean validatePathsForZipp() {
        File sourceFile = new File(source.getText());
        if (!sourceFile.isFile()) {
            System.err.println("invalid source file");
            return false;
        }
        if (!source.getText().endsWith(".txt")) {
            System.err.println("only text files can be zipped!");
            return false;
        }
        if (!sourceFile.exists()) {
            System.err.println("source file not exists!");
            return false;
        }
        File destinationFile = new File(destination.getText());
        if (!(destinationFile.isFile() || destinationFile.isDirectory())) {
            System.err.println("invalid destination");
            return false;
        }
        if (destinationFile.isDirectory()) {

            String fileName = source.getText().substring(source.getText().lastIndexOf("\\") + 1, source.getText().lastIndexOf(".txt"));
            System.out.println("file name : " + fileName);
            destination.setText(destination.getText() + "\\_" + fileName + ".hzp");
            return true;
        } else if (destinationFile.isFile()) {
            if (!destination.getText().endsWith(".hzp")) {
                System.err.println("invalid destination file format!");
                return false;
            }
        }
        return true;
    }

    private boolean validatePathsForUnZipp() {
        File sourceFile = new File(source.getText());
        if (!sourceFile.isFile()) {
            System.err.println("invalid source file");
            return false;
        }
        if (!source.getText().endsWith(".hzp")) {
            System.err.println("only hzp files can be unZipped!");
            return false;
        }

        if (!sourceFile.exists()) {
            System.err.println("source file not exists!");
            return false;
        }
        File destinationFile = new File(destination.getText());
        if (!destinationFile.isFile() && !destinationFile.isDirectory()) {
            System.err.println("invalid destination");
            return false;
        }
        if (destinationFile.isDirectory()) {

            String fileName = source.getText().substring(source.getText().lastIndexOf("\\") + 1, source.getText().lastIndexOf(".hzp"));
            System.out.println("file name : " + fileName);
            destination.setText(destination.getText() + "\\_" + fileName + ".txt");
            return true;
        } else if (destinationFile.isFile()) {
            if (!destination.getText().endsWith(".txt")) {
                System.err.println("invalid destination file format!");
                return false;
            }
        }
        return true;
    }

    public void setProgress(float value) {
        progress.setValue((int) value);
        progressValue.setText((int) value + "%");
    }

    public int getProgress() {
        return progress.getValue();
    }
}
