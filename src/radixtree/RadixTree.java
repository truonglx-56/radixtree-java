/*
The MIT License

Copyright (c) 2011 David J Singer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

package radixtree;

import java.util.LinkedList;
import java.util.Queue;
import java.util.ListIterator;

/**
 * This class implements the operation of a radix tree/trie. A radix tree,
 * Patricia trie/tree, or crit bit tree is a specialized set data structure
 * based on the trie that is used to store a set of strings. In contrast with a
 * regular trie, the key for nodes of a radix trie are labeled with one or more
 * characters rather than only a single characters.
 * Features of this implementation:
 *  Utilizes loops instead of recursion for hopefully faster searches.
 *  Almost all actions use a single common search function.
 *  Tree is sorted in lexilogical (alphabetical) order.
 *  Support for special handling for duplicate keys
 *      Good for keeping a count or list of values for a given key, etc.
 *  Support for not continuing an insert after the search phase.
 *      Good for tree reduction/simplification for example where all keys at a
 *      level have the same value.
 * @author David J Singer
 * email: dave.dorasinger {at.nospam} gmail.com
 */

/**
 *  *** Improvement Ideas ***
 *
 * * CPU cache friendly version using arrays to store nodes. nextNode and childNode are integers that point to a spot in an array rather than objects.
 *
 */


public class RadixTree<T> {
    RadixTreeNode<T> root;
    int size;

    public RadixTree() {
        root = new RadixTreeNode<T>();
        root.key = "";
        root.real = false;
        size = 0;
    }
    RadixTreeSearchResult<T> searchKey(String key, RadixTreeNode<T> node){
        RadixTreeNode<T> parent = null;
        RadixTreeSearchResult<T> result = new RadixTreeSearchResult<T>();
        if (node.key.isEmpty()){
            if (node.childrenSize == 0){
                result.lastMatchKey = "";
                return result;
            }
            parent = node;
            node = node.firstChild;
        }
        String workKey = key;
        String matchedKey = "";
        RadixTreeNode<T> previousNode = null;
        int keyLength = workKey.length();
        String nodeKey = "";
        int nodeLength = 0;
        int maxLoops = 0;
        int numberOfMatchingCharacters = 0;
        boolean belongsBefore = false;
        do {
            nodeKey = node.key;
            nodeLength = nodeKey.length();
            maxLoops = (nodeKey.length() > workKey.length()) ? workKey.length() : nodeKey.length();
            for (numberOfMatchingCharacters = 0; numberOfMatchingCharacters < maxLoops ; numberOfMatchingCharacters++) {
                /**
                The MIT License

                Copyright (c) 2011 Tahseen Ur Rehman, Javid Jamae, David Singer

                This loops logic is largely derived from the project at
                 http://code.google.com/p/radixtree/

                Permission is hereby granted, free of charge, to any person obtaining a copy
                of this software and associated documentation files (the "Software"), to deal
                in the Software without restriction, including without limitation the rights
                to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                copies of the Software, and to permit persons to whom the Software is
                furnished to do so, subject to the following conditions:

                The above copyright notice and this permission notice shall be included in
                all copies or substantial portions of the Software.

                THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
                THE SOFTWARE.
                */
                if (workKey.charAt(numberOfMatchingCharacters) != nodeKey.charAt(numberOfMatchingCharacters)){
                    if (workKey.charAt(numberOfMatchingCharacters) < nodeKey.charAt(numberOfMatchingCharacters))
                        belongsBefore = true;
                    break;
                }
            }
            if (numberOfMatchingCharacters == nodeLength ){
                matchedKey += nodeKey;
                if (node.real){
                    result.matchList.addFirst(node);
                    result.matchFullKeyList.addFirst(matchedKey);
                }
                if (keyLength == nodeLength){
                    result.exactMatch = true;
                    break;
                }
                if (node.childrenSize == 0)
                    break;
                parent = node;
                previousNode = null;
                node = node.firstChild;
                workKey = workKey.substring(nodeLength);
                keyLength = keyLength - nodeLength;
                continue;
            } else if (numberOfMatchingCharacters > 0){
                result.partialMatch = node;
                break;
            }
            if ( belongsBefore )
                break;
            if (node.nextNode == null){
                break;
            } else {
                previousNode = node;
                node = node.nextNode;
                continue;
            }
        } while ( ! belongsBefore );
        result.lastTry = node;
        result.lastTryNumMatchingChars = numberOfMatchingCharacters;
        result.parent = parent;
        result.previousNode = previousNode;
        result.lastMatchKey = matchedKey;
        result.belongsBefore = belongsBefore;

        return result;
    }
    /**
     * Find a value based on its corresponding key.
     *
     * @param key The key for which to search the tree.
     * @return The value corresponding to the key. null if it can not find the key
     */
    public RadixEntry<T> find(String key) {
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if ( result.exactMatch && result.lastTry.real ) {
            return new RadixEntry<T>(result.matchFullKeyList.getFirst(), result.matchList.getFirst().value);
        }
        return null;
    }
    /**
     * Insert a new string key and its value to the tree.
     *
     * @param key
     *            The string key of the object
     * @param value
     *            The value that need to be stored corresponding to the given
     *            key.
     * @throws DuplicateKeyException
     */
    public void insert(String key, T value) throws DuplicateKeyException {
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if ( ! continueInsert(key, value, result) )
            return;
        insert(key, value, result);
    }
    protected void insert(String key, T value, RadixTreeSearchResult<T> result ) throws DuplicateKeyException {
        /**
        The MIT License

        Copyright (c) 2011 Tahseen Ur Rehman, Javid Jamae, David Singer

        This insert function is largely derived from the project at
         http://code.google.com/p/radixtree/

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in
        all copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
        THE SOFTWARE.
        */
        RadixTreeNode<T> node;
        try {
            if (result.exactMatch){
                // Key matched exactly to a node
                if (result.lastTry.real) {
                    // node already has a value. default action is to throw exception.
                    // Over ride below function for cusomt handeling.
                    //result.lastTry.value = dupHandler.duplicateKeyHandler(result.lastTry.value, value);
                    result.lastTry.value = dupKeyHandler(result.lastTry.value, value);
                    return;
                } else {
                    // found an empty home. Yea.
                    result.lastTry.value = value;
                    result.lastTry.real = true;
                }
            } else if (result.partialMatch != null) {
                // Split node with partial match, set node to it (comments will refer to the var name node)
                node = result.partialMatch;
                // node will become the part of the key that matched.
                // the rest of node key will be put in a new node, n1,
                // n1 takes all the characteristics of node.
                // node of corse also gets the value for the insert.
                // n1 becomes a child of node.
                // before, new key/value ab/jerry :
                // |-node (key: abcd, value: frank)
                // | |-n_f_child (key: efg, value: null)
                // | | |-grandchilren_of_node etc...
                //after...
                // |-node (key: ab, value: jerry)
                // | |-n1 (key: cd, value: frank)
                // | | |-n_f_child (key: efg, value: null)
                // | | | |-grandchilren_of_node etc...
                int numMatchingChars = result.lastTryNumMatchingChars;
                RadixTreeNode<T> n1 = new RadixTreeNode<T>();
                n1.key = node.key.substring(numMatchingChars);
                n1.real = node.real;
                n1.value = node.value;
                n1.firstChild = node.firstChild;
                n1.childrenSize = node.childrenSize;
                int matchedKeyLen = result.lastMatchKey.length();
                node.key = key.substring(matchedKeyLen, matchedKeyLen + numMatchingChars);
                if(numMatchingChars < (key.length() - matchedKeyLen)) {
                    // The new key is longer than the matched portion.
                    // So add a node, n2 as a child of node.
                    // n2 will be the new key and value.
                    RadixTreeNode<T> n2 = new RadixTreeNode<T>();
                    n2.key = key.substring(matchedKeyLen + numMatchingChars);
    	            n2.real = true;
                    n2.value = value;
                    n2.childrenSize = 0;
                    node.real = false;
                    node.childrenSize = 2;
                    node.value = null;
                    if (result.belongsBefore){
                        // new key in n2 belongs before partial match found
                        node.firstChild = n2;
                        n2.nextNode = n1;
                    } else {
                        node.firstChild = n1;
                        n1.nextNode = n2;
                    }
                } else {
                    // the new key is the length of the matched characters.
                    // set the new value to this node.
                    node.value = value;
                    node.real = true;
                    node.firstChild = n1;
                    node.childrenSize = 1;
                }
            } else {
                // no match found. Add as new child.
                node = new RadixTreeNode<T>();
                node.key = key.substring(result.lastMatchKey.length());
                node.real = true;
                node.value = value;
                node.childrenSize = 0;
                if (result.parent == null){
                    root.firstChild = node;
                    root.childrenSize = 1;
                } else {
                    if (result.lastTryNumMatchingChars > 0){
                        result.lastTry.childrenSize = 1;
                        result.lastTry.firstChild = node;
                    } else {
                        RadixTreeNode<T> parent = result.parent;
                        parent.childrenSize++;
                        if (result.belongsBefore){
                            if (result.previousNode == null){
                                if (parent.childrenSize != 0 )
                                    node.nextNode = parent.firstChild;
                                parent.firstChild = node;
                            } else {
                                node.nextNode = result.previousNode.nextNode;
                                result.previousNode.nextNode = node;
                            }
                        } else
                                result.lastTry.nextNode = node;
                    }
                }

            }
        } catch (DuplicateKeyException e) {
            // re-throw the exception with 'key' in the message
            throw new DuplicateKeyException(key);
        }
        size++;
    }
    /**
     * Function is called if a duplicate key is encountered during an insert.
     * The builtin function throws DuplicateKeyException("Duplicate key"). To
     * Override to provide your own handling of duplicate keys.
     *
     * @param curValue  value for key already in the tree
     * @param newValue  value associated with new key trying to be inserted.
     * @return value to replace the current value associated with the key in the
     *         tree.
     */
    public T dupKeyHandler(T curValue, T newValue){
        throw new DuplicateKeyException("Duplicate key");
        //return curValue + "!!" + newValue;
    }
    /**
     * continueInsert when overridden can be used for things like trie reduction
     * by identifying they new value is "essentially" the same as the value for
     * a shorter prefix and we don't need to insert or redirects where to insert.
     * Here it is only a place holder that does nothing.
     * @param insPosRes: the search result for proposed key.
     * @param newVal: the new value for the proposed key/val.
     */
    public boolean continueInsert(String key, T newVal, RadixTreeSearchResult<T> searchResult ){
        return true;
    }
    /**
     * Delete a key and its associated value from the tree.
     * @param key The key of the node that need to be deleted
     * @return
     */
    public boolean delete(String key){
        // if remove succeds, (key found) remove returns the node and we return true.
        return (remove(key) != null);
    }
    public RadixTreeNode<T> remove(String key) {
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if (result.exactMatch && result.lastTry.real)
            return remove(result.lastTry, result.previousNode, result.parent);
        else
            return null;
    }
    protected RadixTreeNode<T> remove(RadixTreeNode<T> node, RadixTreeNode<T> previous, RadixTreeNode<T> parent) {
        /**
        The MIT License

        Copyright (c) 2011 Tahseen Ur Rehman, Javid Jamae, David Singer

        This remove function is largely derived from the project at
         http://code.google.com/p/radixtree/

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in
        all copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
        THE SOFTWARE.
        */
        RadixTreeNode<T> children = node.firstChild;
        int childrenSize = node.childrenSize;
        if (childrenSize < 2){
            if (childrenSize == 0){
                if (parent.childrenSize == 1)
                    parent.firstChild = null;
                else if ( previous == null)
                    parent.firstChild = node.nextNode;
                else
                    previous.nextNode = node.nextNode;
                parent.childrenSize--;
            } else {
                RadixTreeNode<T> child = node.firstChild;
                node.key = node.key + child.key;
                node.real = child.real;
                node.value = child.value;
                node.firstChild = child.firstChild;
                node.childrenSize = child.childrenSize;
            }
        } else {
            node.value = null;
            node.real = false;
        }
        size--;
        return node;
    }
    /**
     * Check if the tree contains any entry corresponding to the given key.
     *
     * @param key The key that need to be searched in the tree.
     * @return return true if the key is present in the tree otherwise false
     */
    public boolean contains(String key){
        RadixTreeSearchResult<T> result = searchKey(key, root);
        return result.exactMatch && result.lastTry.real;
    }
    public T findPrefixGetValue(String key){
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if (result.matchList.size() > 0)
            return result.matchList.getFirst().value;
        return null;
    }
    /**
     * Search for the longest prefix of key.
     *
     * @param prefix The prefix for which keys need to be search
     * @return The list of values those key start with the given prefix
     */
    public RadixEntry<T> findPrefix(String key){
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if (result.matchList.size() > 0)
            return new RadixEntry<T>(result.matchFullKeyList.getFirst(), result.matchList.getFirst().value);
        return null;
    }
    /**
     * Search for all prefixes of key.
     *
     * @param key The key for which prefixes need to be found.
     * @return The list of values those key start with the given prefix
     */
    public LinkedList<RadixEntry<T>> findPrefixes(String key){
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if (result.matchList.size() == 0)
            return null;
        RadixTreeNode<T> nn ;
        ListIterator keyIt = result.matchFullKeyList.listIterator();
        LinkedList<RadixEntry<T>> matchList = new LinkedList<RadixEntry<T>>();
        while (keyIt.hasNext()) {
            nn = result.matchList.removeFirst();
            if (nn != null)
            matchList.addFirst(new RadixEntry<T>((String)keyIt.next(), nn.value));
        }
        return matchList;
    }
    public LinkedList<RadixEntry<T>> findPostfixes(String key, long limit){
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if (result.partialMatch != null && result.lastTryNumMatchingChars + result.lastMatchKey.length() == key.length()) {
            return getChildren(result.partialMatch, key, limit);
        } else if (result.exactMatch) {
            return getChildren(result.lastTry, key, limit);
        }
        return new LinkedList<RadixEntry<T>>();
    }
    public LinkedList<String> findPostfixesGetkeys(String key, long limit){
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if (result.partialMatch != null && result.lastTryNumMatchingChars + result.lastMatchKey.length() == key.length()) {
            return getChildrenKeys(result.partialMatch, key, limit);
        } else if (result.exactMatch) {
            return getChildrenKeys(result.lastTry, key, limit);
        }
        return new LinkedList<String>();
    }
    /**
     * Search for all the keys that start with given prefix. limiting the results based on the supplied limit.
     *
     * @param prefix The prefix for which keys need to be search
     * @param recordLimit The limit for the results
     * @return The list of values those key start with the given prefix
     */
    public LinkedList<T> findPostfixesGetValues(String key, long limit){
        RadixTreeSearchResult<T> result = searchKey(key, root);
        if (result.partialMatch != null && result.lastTryNumMatchingChars + result.lastMatchKey.length() == key.length()) {
            return getChildrenValues(result.partialMatch, limit);
        } else if (result.exactMatch) {
            return getChildrenValues(result.lastTry, limit);
        }
        return new LinkedList<T>();
    }
    protected LinkedList<String> getChildrenKeys(RadixTreeNode<T> node, String levelKey, long limit) {
        if (limit < 0)
            limit = this.size;
        LinkedList<String> keys = new LinkedList<String>();
        Queue<String> levelKeys = new LinkedList<String>();
        Queue<RadixTreeNode<T>> iteratorQueue = new LinkedList<RadixTreeNode<T>>();
        if (node.real){
            keys.add(levelKey + node.key);
        }
        if (node.childrenSize > 0){
            levelKeys.add(levelKey);
            levelKey += node.key;
            node = node.firstChild;
        }
        while (keys.size() < limit) {
            if (node.real){
                keys.add(levelKey + node.key);
            }
            if (node.childrenSize > 0){
                levelKeys.add(levelKey);
                levelKey += node.key;
                if (node.nextNode != null)
                    iteratorQueue.add(node);
                node = node.firstChild;
                continue;
            }
            if (node.nextNode == null){
                while (!iteratorQueue.isEmpty() && node.nextNode == null){
                    node = iteratorQueue.remove();
                    levelKey = levelKeys.remove();
                }
                if (node.nextNode == null)
                    break;
            }
            node = node.nextNode;
        }
        return keys;
    }
    protected LinkedList<T> getChildrenValues(RadixTreeNode<T> node, long limit) {
        if (limit < 0)
            limit = this.size;
        LinkedList<T> values = new LinkedList<T>();
        if (node.real)
            values.add(node.value);
        Queue<RadixTreeNode<T>> queue = new LinkedList<RadixTreeNode<T>>();
        if (node.childrenSize > 0){
            queue.add(node);
            node = node.firstChild;
        } else
            return values;
        while (values.size() < limit) {
            if (node.real)
                values.add(node.value);
            if (node.childrenSize > 0){
                queue.add(node);
                node = node.firstChild;
                continue;
            }
            if (node.nextNode == null){
                while (!queue.isEmpty() && node.nextNode == null){
                    node = queue.remove();
                }
                if (node.nextNode == null)
                    break;
            }
            node = node.nextNode;
        }
        return values;
    }
    protected LinkedList<RadixEntry<T>> getChildren(RadixTreeNode<T> node, String levelKey, long limit) {
        if (limit < 0)
            limit = this.size;
        LinkedList<RadixEntry<T>> entries = new LinkedList<RadixEntry<T>>();
        Queue<String> levelKeys = new LinkedList<String>();
        Queue<RadixTreeNode<T>> iteratorQueue = new LinkedList<RadixTreeNode<T>>();
        if (node.real){
            entries.addFirst(new RadixEntry<T>(levelKey + node.key, node.value));
        }
        if (node.childrenSize > 0){
            levelKeys.add(levelKey);
            levelKey += node.key;
            node = node.firstChild;
        }
        while (entries.size() < limit) {
            if (node.real){
                entries.addFirst(new RadixEntry<T>(levelKey + node.key, node.value));
            }
            if (node.childrenSize > 0){
                levelKeys.add(levelKey);
                levelKey += node.key;
                if (node.nextNode != null)
                    iteratorQueue.add(node);
                node = node.firstChild;
                continue;
            }
            if (node.nextNode == null){
                while (!iteratorQueue.isEmpty() && node.nextNode == null){
                    node = iteratorQueue.remove();
                    levelKey = levelKeys.remove();
                }
                if (node.nextNode == null)
                    break;
            }
            node = node.nextNode;
        }
        return entries;
    }
    @Deprecated
    public void display(){
        display(root, "", 0);
    }
    @Deprecated
    protected void display(RadixTreeNode<T> node, String levelKey, long limit) {
        if (limit <= 0)
            limit = this.size;
        LinkedList<RadixTreeNode<T>> levelQueue = new LinkedList<RadixTreeNode<T>>();
        int level = 0;
        if (node.key.equals("")){
            if (node.childrenSize == 0){
                return;
            }
            node = node.firstChild;
        }
        long realCount = 0;
        String format = "";
        while (realCount < limit) {
            if (node.real){
                realCount++;
                System.out.printf("%s(%s)'%s' [%s]\n", format + "|-", levelKey, node.key, node.value.toString());
            } else
                System.out.printf("%s(%s)'%s'\n", format + "|-", levelKey, node.key);
            if (node.childrenSize > 0){
                levelKey += node.key;
                levelQueue.add(node);
                node = node.firstChild;
                format += "| ";
                continue;
            }
            if (node.nextNode == null){
                while (!levelQueue.isEmpty() && node.nextNode == null){
                    node = levelQueue.removeLast();
                    levelKey = levelKey.substring(0, levelKey.length() - node.key.length());
                    format = format.substring(0, format.length() - 2);
                }
                if (node.nextNode == null)
                    break;
            }
            node = node.nextNode;
        }
    }
    /**
     * Return the number of real nodes in the Radix tree
     * @return the size of the tree
     */
    public long getSize() {
        return size;
    }
}
