package Zipiing;

import Gui.MainFrame;
import Zipiing.CharCode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mehdi
 */
public class UnZipper {

    int metaDataSize;
    byte[] mainData;
    int realNumberOfBitsOfLastByte;
    String sourcePath;
    String destinationPath;
    String fileContent;
    String binaryOfOriginalData = "";
    String metaData;
    String lastData = "";
    ArrayList<CharCode> codes;
    int inputSize;
    int outputSize;
    MainFrame UI;

    public UnZipper(String sourcePath, String destinationPath, MainFrame UI) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        codes = new ArrayList<>();
        this.UI = UI;
    }

    public void unZip() {
        UI.setProgress(0);
        System.out.println("unzipping started ...");
        readFile();
        interpretMetaData();
        generateUnzippedData();
        translateBinary(0, 1);
        writeToFile();
        System.out.println("--------------------------------------");
        System.out.println("   File unzipped successfully!");
        System.out.println("   input size   : " + inputSize + " bytes");
        System.out.println("   output size  : " + outputSize + " bytes");
        System.out.println("--------------------------------------");
        UI.setProgress(100);
    }

    private void readFile() {
        System.out.print("reading data ...");
        File file = new File(sourcePath);
        UI.setProgress(1);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            inputSize = (int) file.length();
            UI.setProgress(2);
            mainData = new byte[inputSize];
            UI.setProgress(3);
            fileInputStream.read(mainData);
            UI.setProgress(5);
            this.fileContent = new String(mainData);
        } catch (Exception ex) {
            System.err.println("Mehdi: coud not make the scanner!");
            Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("  done!");
    }

    private void interpretMetaData() {
        metaData = fileContent.substring(0, fileContent.indexOf("##"));
        metaDataSize = metaData.getBytes().length + 2;
        //realising memory
        fileContent = null;
        realNumberOfBitsOfLastByte = Integer.parseInt("" + metaData.charAt(0));
        metaData = metaData.substring(1);
        String[] codes = metaData.split("_&");
        for (String item : codes) {
            if (!"".equals(codes[0].trim())) {
                this.codes.add(new CharCode("" + item.charAt(0), item.substring(2)));
            }
        }
        System.out.println("------------- codes ----------");
        for (CharCode item : this.codes) {
            System.out.println(item.getValue() + "  :  " + item.getCode());
        }
    }

    private void generateUnzippedData() {
        System.out.print("generating binary ...");
        char[] temp = new char[mainData.length * 8];
        String token;
        int nextEmptyCharIndex = 0;
        for (int i = 0; i < mainData.length - metaDataSize; i++) {
            UI.setProgress(5 + (float)(i+1) / (mainData.length - metaDataSize) * 45);
            int decimalValue = mainData[i + metaDataSize];
            if (decimalValue < 0) {
                decimalValue = 256 + decimalValue;
            }
            token = decimalToBinary(decimalValue);
            for (int j = 0; j < token.length(); j++) {
                temp[nextEmptyCharIndex + j] = token.charAt(j);
            }
            nextEmptyCharIndex += token.length();
        }
        binaryOfOriginalData = new String(temp, 0, nextEmptyCharIndex);
        binaryOfOriginalData = binaryOfOriginalData.substring(0, binaryOfOriginalData.length() - (realNumberOfBitsOfLastByte > 0 ? 8 : 0) + realNumberOfBitsOfLastByte);
        System.out.println("  done!");
    }

    private String decimalToBinary(int num) {
        String result = "";
        for (int i = 7; i >= 0; i--) {
            if (num >= Math.pow(2, i)) {
                result += "1";
                num -= Math.pow(2, i);
            } else {
                result += "0";
            }
        }
        return result;
    }

    private void translateBinary(int start, int end) {
        System.out.print("translating binary ...");
        char[] temp = new char[binaryOfOriginalData.length()];
        int nextEmptyCharIndex = 0;
        String realChar;
        while (end <= binaryOfOriginalData.length()) {
            realChar = isACharacter(binaryOfOriginalData.substring(start, end));
            UI.setProgress(50 + (float)(end +1) / binaryOfOriginalData.length() * 45);
            if (realChar != null) {
//                lastData += realChar;
                temp[nextEmptyCharIndex] = realChar.charAt(0);
                nextEmptyCharIndex++;
                start = end;
            }
            end++;
        }
        lastData = new String(temp, 0, nextEmptyCharIndex);
        System.out.println("  done!");
    }

    private String isACharacter(String binaryCode) {
        for (CharCode charCode : codes) {
            if (charCode.getCode().equals(binaryCode)) {
                return charCode.getValue();
            }
        }
        return null;
    }

    private void writeToFile() {
        System.out.print("writing final data to destination ...");
        try {
            File file = new File(destinationPath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            UI.setProgress(96);
            FileOutputStream oo = new FileOutputStream(file);
            UI.setProgress(97);
            oo.write(lastData.getBytes());
            oo.close();
            UI.setProgress(98);
            outputSize = (int) file.length();
        } catch (IOException ex) {
            Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("  done!");
    }
}
