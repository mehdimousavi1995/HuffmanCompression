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
public class CharacterItem {
    
    private String value;
    private int count;
    private TreeNode treeNode;

    public CharacterItem(String value) {
        this.value = value;
        this.count = 1;
        treeNode = new TreeNode(value);
    }
    
    public CharacterItem(CharacterItem item1, CharacterItem item2){
        this.value = item1.getValue() + item2.getValue();
        this.count = item1.getCount() + item2.getCount();
        this.treeNode = new TreeNode(this.value, item1.treeNode, item2.treeNode);
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the treeNode
     */
    public TreeNode getTreeNode() {
        return treeNode;
    }

    /**
     * @param treeNode the treeNode to set
     */
    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }
    
    public void increaseCount(){
    count++;
    }
}
