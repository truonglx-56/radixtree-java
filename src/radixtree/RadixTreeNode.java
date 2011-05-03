package radixtree;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author daves
 */
public class RadixTreeNode<T> {
    public String key;
    public T value;
    public boolean real;
    public RadixTreeNode<T> nextNode;
    public RadixTreeNode<T> firstChild;
    public int childrenSize;

    public RadixTreeNode() {
    }
}
