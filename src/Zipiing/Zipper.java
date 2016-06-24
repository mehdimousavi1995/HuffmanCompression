package Zipiing;

import Gui.MainFrame;
import Zipiing.CharCode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.ByteBuffer;
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
public class Zipper {
    
    String sourcePath;
    String destinationPath;
    String newBinaryData = "";
    String originalData = "";
    ArrayList<CharacterItem> characters;
    ArrayList<CharCode> codes;
    MainFrame UI;
    
    int numberOfCharsUsedInFile = 0;
    int inputSize;
    int outputSize;
    
    
//    private int tempAllCharactersSize;
    public Zipper(String sourcePath, String destinationPath, MainFrame UI) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        codes = new ArrayList<>();
        this.UI = UI;
    }
    
    public void zip() {
        UI.setProgress(0);
        System.out.println("zipping started ...");
        readFile();
        makeCharactersArray();
        sortCharacterArray();
        printCharacterItems();
//        tempAllCharactersSize = characters.size();
        makeHufmanTree();
        declareCodes();
        printCodes();
        generateNewBinaryData();
        writeFinalDataToDestinationFile();
        System.out.println("");
        System.out.println("");
        System.out.println("--------------------------------------");
        System.out.println("   File zipped successfully!");
        System.out.println("   input size   : " + inputSize + " bytes");
        System.out.println("   output size  : " + outputSize + " bytes");
        System.out.println("--------------------------------------");
        UI.setProgress(100);
    }
    
    private void readFile() {
        System.out.print("reading data ...");
        File file = new File(sourcePath);
//        Scanner scanner = null;
//        try {
//            scanner = new Scanner(file);
//        } catch (FileNotFoundException ex) {
//            System.err.println("Mehdi: coud not make the scanner!");
//            Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        while (scanner.hasNextLine()) {
//            originalData += scanner.nextLine() + System.lineSeparator();
//        }
        try {
            UI.setProgress(2);
            FileInputStream fileInputStream = new FileInputStream(file);
            inputSize = (int) file.length();
            byte[] fileContent = new byte[inputSize];
            UI.setProgress(3);
            fileInputStream.read(fileContent);
            fileInputStream.close();
            UI.setProgress(5);
            originalData = new String(fileContent);
        } catch (Exception ex) {
            System.err.println("Mehdi: coud not make the scanner!");
            Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("  done!");
        
    }
    
    private void makeCharactersArray() {
        characters = new ArrayList<>();
        for (int i = 0; i < originalData.length(); i++) {
            UI.setProgress(5 + ((float) (i + 1) / originalData.length()) * 40);
            if (!increaseCountOfRegisteredCharacter(originalData.charAt(i))) {
                characters.add(new CharacterItem("" + originalData.charAt(i)));
                numberOfCharsUsedInFile++;
            }
        }
    }
    
    private boolean increaseCountOfRegisteredCharacter(char c) {
        for (CharacterItem characterItem : characters) {
            if (characterItem.getValue().contains("" + c)) {
                characterItem.increaseCount();
                return true;
            }
        }
        return false;
    }
    
    private void sortCharacterArray() {
//        for (CharacterItem characterItem : characters) {
//            doInsertionSort(characterItem);
//        }
        for (int i = 0; i < characters.size(); i++) {
            UI.setProgress(45 + ((float)(i+1) / characters.size()) * 5);
            doInsertionSort(characters.get(i));
        }
    }
    
    private void doInsertionSort(CharacterItem item) {
        if (characters.indexOf(item) == 0) {
            return;
        }
        
        CharacterItem previousItem = characters.get(characters.indexOf(item) - 1);
        if (previousItem.getCount() < item.getCount()) {
            replace(previousItem, item);
            doInsertionSort(item);
        }
    }
    
    private void replace(CharacterItem previousItem, CharacterItem item) {
        CharacterItem temp = previousItem;
        int itemIndex = characters.indexOf(item);
        characters.set(characters.indexOf(previousItem), item);
        characters.set(itemIndex, previousItem);
    }
    
    
    
    private void makeHufmanTree() {
//        UI.setProgress(50 + (tempAllCharactersSize - characters.size() + 2) / (float)tempAllCharactersSize * 5);
        if (characters.size() < 2) {
            return;
        }
        CharacterItem item1 = characters.get(characters.size() - 1);
        characters.remove(characters.size() - 1);
        CharacterItem item2 = characters.get(characters.size() - 1);
        characters.remove(characters.size() - 1);
        CharacterItem newItem = new CharacterItem(item1, item2);
        characters.add(newItem);
        doInsertionSort(newItem);
        makeHufmanTree();
    }
    
    private void printCharacterItems() {
        System.out.println("----- file chars -----");
        System.out.println("char : count");
        for (CharacterItem characterItem : characters) {
            System.out.println(" " + (characterItem.getValue().equals(" ") ? "SP" : (characterItem.getValue().equals("\n") ? "\\n" : characterItem.getValue().equals("\r") ? "\\r" : characterItem.getValue() + " ")) + "  :  " + characterItem.getCount());
        }
    }
    
    private void printCodes() {
        System.out.println("-------- generated codes ------------");
        System.out.println("char : code");
//        for (CharCode charCode : codes) {
//            System.out.println(" " + (charCode.getValue().equals(" ") ? "SP" : (charCode.getValue().equals("\n") ? "\\n" : charCode.getValue().equals("\r") ? "\\r" : charCode.getValue() + " ")) + "  : " + charCode.getCode());
//        }
        for (int i = 0; i < codes.size(); i++) {
            UI.setProgress(50 + (float)(i+1) / codes.size() * 5);
            System.out.println(" " + (codes.get(i).getValue().equals(" ") ? "SP" : (codes.get(i).getValue().equals("\n") ? "\\n" : codes.get(i).getValue().equals("\r") ? "\\r" : codes.get(i).getValue() + " ")) + "  : " + codes.get(i).getCode());
        }
        
    }
    
    private void declareCodes(TreeNode node, String code) {
        if (node.hasNoChild()) {
            codes.add(new CharCode(node.getValue(), code));
        } else {
            declareCodes(node.getLeftChild(), code + "1");
            declareCodes(node.getRightChild(), code + "0");
        }
    }
    
    private void generateNewBinaryData() {
        System.out.print("generating new binary data ...");
        char[] temp = new char[originalData.length() * 8];
        int nextEmptyCharIndex = 0;
        String token;
        for (int i = 0; i < originalData.length(); i++) {
            UI.setProgress(55 + (float)(i+1)/originalData.length() * 40);
            token = getCharCode(originalData.charAt(i));
            for (int j = 0; j < token.length(); j++) {
                temp[nextEmptyCharIndex + j] = token.charAt(j);
            }
            nextEmptyCharIndex += token.length();
        }
        newBinaryData = new String(temp, 0, nextEmptyCharIndex);
        System.out.println("   done!");
    }
    
    private String getCharCode(char c) {
        for (CharCode charCode : codes) {
            if (charCode.getValue().contains("" + c)) {
                return charCode.getCode();
            }
        }
        return null;
    }
    
    private void writeFinalDataToDestinationFile() {
        try {
            System.out.print("writing final data to destination file ...");
            File file = new File(destinationPath);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            UI.setProgress(92);
            FileOutputStream outputStream = new FileOutputStream(file);
            UI.setProgress(93);
            outputStream.write(getMetaData().getBytes());
            UI.setProgress(95);
            outputStream.write(generateCodedData());
            UI.setProgress(97);
            outputStream.close();
            outputSize = (int) file.length();
            System.out.println("   done!");
        } catch (IOException ex) {
            Logger.getLogger(Zipper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private byte[] generateCodedData() {
        int size = newBinaryData.length() / 8;
        if (newBinaryData.length() % 8 != 0) {
            size++;
        }
        byte[] data = new byte[size];
        for (int i = 0; i < size; i++) {
            if (i == size - 1 && newBinaryData.length() % 8 != 0) {
                break;
            }
            data[i] = new Integer(binaryToDecimal(newBinaryData.substring(i * 8, (i + 1) * 8))).byteValue();
        }
        if (newBinaryData.length() % 8 != 0) {
            data[size - 1] = new Integer(binaryToDecimal(newBinaryData.substring((size - 1) * 8))).byteValue();
        }
        return data;
    }
    
    private int binaryToDecimal(String binary) {
        int decimal = 0;
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                decimal += (int) Math.pow(2, (7 - i));
            }
        }
        return decimal;
    }
    
    private String getMetaData() {
        String metaData = "";
        //writing real number of bits of the last byte of data at the beggining of the file
        metaData += newBinaryData.length() % 8;
        for (CharCode charCode : codes) {
            metaData += charCode.getValue() + ":" + charCode.getCode() + "_&";
        }
        metaData += "##";
        return metaData;
    }
    
    private void declareCodes() {
        if (numberOfCharsUsedInFile == 0) {
            return;
        }
        if (numberOfCharsUsedInFile == 1) {
            codes.add(new CharCode(characters.get(0).getValue(), "1"));
        } else {
            declareCodes(characters.get(0).getTreeNode(), "");
        }
    }
}
