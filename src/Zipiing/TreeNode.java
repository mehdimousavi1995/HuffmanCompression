package Zipiing;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mehdi
 */
public class TreeNode {
    
    private String value;
    private TreeNode leftChild;
    private TreeNode rightChild;

    public TreeNode(String value) {
        this.value = value;
        leftChild = null;
        rightChild = null;
    }
    
    public TreeNode(String value, TreeNode leftChild, TreeNode rightChild){
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }
    
    public boolean hasNoChild(){
        if(getLeftChild() != null || getRightChild() != null){
        return false;
        }
        return true;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the leftChild
     */
    public TreeNode getLeftChild() {
        return leftChild;
    }

    /**
     * @return the rightChild
     */
    public TreeNode getRightChild() {
        return rightChild;
    }
}
