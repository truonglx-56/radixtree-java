
package radixtree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author David Singer
 * This code is not licensed and may be used in any way.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */
 
public class RadixTreeTest {
    RadixTree<String> trie;
    public RadixTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        trie = new RadixTree<String>();
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of continueInsert method, of class RadixTree.
     */
    @Test
    public void testContinueInsert() {
        System.out.println("continueInsert");
        RadixTree<Integer> tr = new RadixTree<Integer>(){
            @Override
            @SuppressWarnings (value="unchecked")
            public boolean continueInsert(String key, Integer newVal, RadixTreeSearchResult<Integer> searchResult ){
            //void modifyInsertPos(RadixTreeSearchResult<T> insPosRes){
                //System.out.println("test");
                if (searchResult.matchList.size() > 0 && searchResult.matchList.peekFirst().value.equals(newVal))
                    // sample though not realistic case
                    return false;
                else if ( searchResult.parent != null && searchResult.parent.childrenSize == 9 ) {
                    RadixTreeNode parent = searchResult.parent, n = parent.firstChild;
                    LinkedList<RadixTreeNode> nL = new LinkedList();
                    do {
                        if ( ! n.real || n.value != newVal || n.key.length() > 1 || n.childrenSize != 0)
                            return true;
                        nL.add(n);
                        n = n.nextNode;
                    } while (n != null);
                    while (nL.size() > 0){
                        n = nL.removeLast();
                        remove(n, nL.peekLast(), parent);
                    }

                    parent.value = newVal;
                    parent.real = true;
                    return false;
                }

                return true;
            }
        };

        tr.insert("55", 6);
        tr.insert("556", 6);
        tr.insert("55671", 8);
        tr.insert("55672", 8);
        tr.insert("55673", 8);
        tr.insert("55674", 8);
        tr.insert("55675", 8);
        tr.insert("55676", 8);
        tr.insert("55677", 8);
        tr.insert("55678", 8);
        tr.insert("55679", 8);
        assertEquals(true, tr.contains("55"));
        assertEquals(true, tr.contains("55677"));
        assertEquals(false, tr.contains("556") );
        tr.insert("55670", 8);
        assertEquals(false, tr.contains("55677"));
        assertEquals(true, tr.contains("5567"));
    }
    /**
     * Test of dupKeyHandler method, of class RadixTree.
     */
    @Test
    public void testdupKeyHandler() {
        RadixTree<Object> tr = new RadixTree<Object>(){
            //throw new DuplicateKeyException("Duplicate key");  //default behavior of method we are overriding
            @Override
            public Object dupKeyHandler(Object curValue, Object newValue){
                // maintain unique values per key
                if (curValue instanceof Integer ) {
                    Integer tmCV = (Integer)curValue;
                    Integer tmNV = (Integer)newValue;
                    if ( tmCV.equals(tmNV) )
                        return curValue;
                    LinkedList<Integer> tl = new LinkedList();
                    tl.add(tmCV);
                    tl.add(tmNV);
                    return tl;
                } else if ( curValue instanceof LinkedList ) {
                    LinkedList<Integer> tmCV = (LinkedList<Integer>)curValue;
                    Integer tmNV = (Integer)newValue;
                    if ( ! tmCV.contains(tmNV) )
                        tmCV.add(tmNV);
                    return tmCV;
                }
                return newValue;
            }
        };
        tr.insert("556", 6);
        tr.insert("556", 6);
        assertEquals(true, ( tr.find("556").value instanceof Integer ) );
        tr.insert("556", 7);
        assertEquals(true, ( tr.find("556").value instanceof LinkedList ) );
        tr.insert("556", 7);
        assertEquals(2, ( ( (LinkedList)tr.find("556").value ).size() ) );
        tr.insert("556", 8);
        assertEquals(3, ( ( (LinkedList)tr.find("556").value ).size() ) );
        tr.insert("556", 7);
        assertEquals(3, ( ( (LinkedList)tr.find("556").value ).size() ) );
        tr.insert("556", 9);
        assertEquals(4, ( ( (LinkedList)tr.find("556").value ).size() ) );
        tr.insert("556", 8);
        assertEquals(4, ( ( (LinkedList)tr.find("556").value ).size() ) );

    }

    @Test
    public void testSearchForPartialParentAndLeafKeyWhenOverlapExists() {
        trie.insert("abcd", "abcd");
        trie.insert("abce", "abce");

        assertEquals(0, trie.findPostfixesGetValues("abe", 10).size());
        assertEquals(0, trie.findPostfixesGetValues("abd", 10).size());
    }

    @Test
    public void testSearchForLeafNodesWhenOverlapExists() {
        trie.insert("abcd", "abcd");
        trie.insert("abce", "abce");

        assertEquals(1, trie.findPostfixesGetValues("abcd", 10).size());
        assertEquals(1, trie.findPostfixesGetValues("abce", 10).size());
    }

    @Test
    public void testSearchForStringSmallerThanSharedParentWhenOverlapExists() {
        trie.insert("abcd", "abcd");
        trie.insert("abce", "abce");

        assertEquals(2, trie.findPostfixesGetValues("ab", 10).size());
        assertEquals(2, trie.findPostfixesGetValues("a", 10).size());
    }

    @Test
    public void testSearchForStringEqualToSharedParentWhenOverlapExists() {
        trie.insert("abcd", "abcd");
        trie.insert("abce", "abce");

        assertEquals(2, trie.findPostfixesGetValues("abc", 10).size());
    }

    @Test
    public void testInsert() {
        trie.insert("apple", "apple");
        trie.insert("bat", "bat");
        trie.insert("ape", "ape");
        trie.insert("bath", "bath");
        trie.insert("banana", "banana");
        trie.display();

        assertEquals("apple", trie.find("apple").value);
        assertEquals("bat", trie.find("bat").value);
        assertEquals("ape", trie.find("ape").value);
        assertEquals("bath", trie.find("bath").value);
        assertEquals("banana", trie.find("banana").value);
    }

    @Test
    public void testInsertExistingUnrealNodeConvertsItToReal() {
    	trie.insert("applepie", "applepie");
    	trie.insert("applecrisp", "applecrisp");

    	assertFalse(trie.contains("apple"));

    	trie.insert("apple", "apple");

    	assertTrue(trie.contains("apple"));
    }

    @Test
    public void testDuplicatesNotAllowed() {
        RadixTree<String> trie2 = new RadixTree<String>();

        trie2.insert("apple", "apple");

        try {
            trie2.insert("apple", "apple2");
            fail("Duplicate should not have been allowed");
        } catch (DuplicateKeyException e) {
            assertEquals("apple", e.getMessage());
        }
    }

    @Test
    public void testInsertWithRepeatingPatternsInKey() {
        trie.insert("xbox 360", "xbox 360");
        trie.insert("xbox", "xbox");
        trie.insert("xbox 360 games", "xbox 360 games");
        trie.insert("xbox games", "xbox games");
        trie.insert("xbox xbox 360", "xbox xbox 360");
        trie.insert("xbox xbox", "xbox xbox");
        trie.insert("xbox 360 xbox games", "xbox 360 xbox games");
        trie.insert("xbox games 360", "xbox games 360");
        trie.insert("xbox 360 360", "xbox 360 360");
        trie.insert("xbox 360 xbox 360", "xbox 360 xbox 360");
        trie.insert("360 xbox games 360", "360 xbox games 360");
        trie.insert("xbox xbox 361", "xbox xbox 361");
        try {
            trie.insert("xbox 360 xbox games", "duplicate rec xbox 360 xbox games");
        } catch  (DuplicateKeyException e) {
            assertEquals("xbox 360 xbox games", e.getMessage());
        }

        trie.display();

        assertEquals(12, trie.getSize());
    }

    @Test
    public void testDeleteNodeWithNoChildren() {
        RadixTree<String> trie2 = new RadixTree<String>();
        trie2.insert("apple", "apple");
        assertTrue(trie2.delete("apple"));
    }

    @Test
    public void testDeleteNodeWithOneChild() {
        RadixTree<String> trie2 = new RadixTree<String>();
        trie2.insert("apple", "apple");
        trie2.insert("applepie", "applepie");
        assertTrue(trie2.delete("apple"));
        assertTrue(trie2.contains("applepie"));
        assertFalse(trie2.contains("apple"));
    }

    @Test
    public void testDeleteNodeWithMultipleChildren() {
        RadixTree<String> trie2 = new RadixTree<String>();
        trie2.insert("apple", "apple");
        trie2.insert("applepie", "applepie");
        trie2.insert("applecrisp", "applecrisp");
        assertTrue(trie2.delete("apple"));
        assertTrue(trie2.contains("applepie"));
        assertTrue(trie2.contains("applecrisp"));
        assertFalse(trie2.contains("apple"));
    }

    @Test
    public void testCantDeleteSomethingThatDoesntExist() {
        RadixTree<String> trie2 = new RadixTree<String>();
        assertFalse(trie2.delete("apple"));
    }

    @Test
    public void testCantDeleteSomethingThatWasAlreadyDeleted() {
        RadixTree<String> trie2 = new RadixTree<String>();
        trie2.insert("apple", "apple");
        trie2.delete("apple");
        assertFalse(trie2.delete("apple"));
    }

    @Test
    public void testChildrenNotAffectedWhenOneIsDeleted() {
        RadixTree<String> trie2 = new RadixTree<String>();
        trie2.insert("apple", "apple");
        trie2.insert("appleshack", "appleshack");
        trie2.insert("applepie", "applepie");
        trie2.insert("ape", "ape");

        trie2.delete("apple");

        assertTrue(trie2.contains("appleshack"));
        assertTrue(trie2.contains("applepie"));
        assertTrue(trie2.contains("ape"));
        assertFalse(trie2.contains("apple"));
    }

    @Test
    public void testSiblingsNotAffectedWhenOneIsDeleted() {
        RadixTree<String> trie2 = new RadixTree<String>();
        trie2.insert("apple", "apple");
        trie2.insert("ball", "ball");

        trie2.delete("apple");

        assertTrue(trie2.contains("ball"));
    }

    @Test
    public void testCantDeleteUnrealNode() {
        RadixTree<String> trie2 = new RadixTree<String>();
        trie2.insert("apple", "apple");
        trie2.insert("ape", "ape");

        assertFalse(trie2.delete("ap"));
    }

    @Test
    public void testCantFindRootNode() {
        assertNull(trie.find(""));
    }

    @Test
    public void testFindSeInsert() {
        trie.insert("apple", "apple");
        assertNotNull(trie.find("apple"));
    }

    @Test
    public void testContainsSeInsert() {
        trie.insert("apple", "apple");
        assertTrue(trie.contains("apple"));
    }

    @Test
    public void testFindChildInsert() {
        trie.insert("apple", "apple");
        trie.display();
        trie.insert("ape", "ape");
        trie.insert("appletree", "appletree");
        trie.insert("appleshackcream", "appleshackcream");
        assertNotNull(trie.find("appletree"));
        assertNotNull(trie.find("appleshackcream"));
        assertTrue(trie.contains("ape"));
        trie.display();
    }

    @Test
    public void testContainsChildInsert() {
        trie.insert("apple", "apple");
        trie.insert("ape", "ape");
        trie.insert("appletree", "appletree");
        trie.insert("appleshackcream", "appleshackcream");
        assertTrue(trie.contains("appletree"));
        assertTrue(trie.contains("appleshackcream"));
        assertTrue(trie.contains("ape"));
        trie.display();
    }

    @Test
    public void testCantFindNonexistantNode() {
        assertNull(trie.find("apple"));
    }

    @Test
    public void testDoesntContainNonexistantNode() {
        assertFalse(trie.contains("apple"));
    }

    @Test
    public void testCantFindUnrealNode() {
        trie.insert("apple", "apple");
        trie.insert("ape", "ape");
        assertNull(trie.find("ap"));
    }

    @Test
    public void testDoesntContainUnrealNode() {
        trie.insert("apple", "apple");
        trie.insert("ape", "ape");
        assertFalse(trie.contains("ap"));
    }


    @Test
    public void testfindPostfixesGetValues_LimitGreaterThanPossibleResults() {
        trie.insert("apple", "apple");
        trie.insert("appleshack", "appleshack");
        trie.insert("appleshackcream", "appleshackcream");
        trie.insert("applepie", "applepie");
        trie.insert("ape", "ape");

        LinkedList<String> result = trie.findPostfixesGetValues("app", 10);
        assertEquals(4, result.size());

        assertTrue(result.contains("appleshack"));
        assertTrue(result.contains("appleshackcream"));
        assertTrue(result.contains("applepie"));
        assertTrue(result.contains("apple"));
    }

    @Test
    public void testfindPostfixesGetValues_LimitLessThanPossibleResults() {
        trie.insert("apple", "apple");
        trie.insert("appleshack", "appleshack");
        trie.insert("appleshackcream", "appleshackcream");
        trie.insert("applepie", "applepie");
        trie.insert("ape", "ape");

        LinkedList<String> result = trie.findPostfixesGetValues("appl", 3);
        assertEquals(3, result.size());

        assertTrue(result.contains("appleshack"));
        assertTrue(result.contains("applepie"));
        assertTrue(result.contains("apple"));
    }

    @Test
    public void testGetSize() {
        trie.insert("apple", "apple");
        trie.insert("appleshack", "appleshack");
        trie.insert("appleshackcream", "appleshackcream");
        trie.insert("applepie", "applepie");
        trie.insert("ape", "ape");

        assertTrue(trie.getSize() == 5);
    }

    @Test
    public void testDeleteReducesSize() {
        trie.insert("apple", "apple");
        trie.insert("appleshack", "appleshack");

        trie.delete("appleshack");

        assertTrue(trie.getSize() == 1);
    }
    @Test
    public void testCandelInsertFromSearchResults() {
        RadixTree<String> trie2 = new RadixTree<String>() {
            @Override
            public boolean continueInsert(String key, String newVal, RadixTreeSearchResult<String> searchResult ){
                System.out.println("cancel insert test");
                //RadixTreeNode<String> node;
                if ( ! searchResult.exactMatch && ! searchResult.matchList.isEmpty() && searchResult.matchList.peekFirst().value.equals(newVal)) {
                        return false;
                }
                System.out.println("orig key :" + key + ": new key :" + key.substring(0, key.length() - 1) + ":" );
                return true;
            }
        };
        trie2.insert("ape", "ape");
        trie2.insert("apes", "ape");
        assertFalse(trie2.contains("apes"));
        trie2.insert("12", "ape");
        trie2.insert("122", "apple");
        assertTrue(trie2.contains("122"));
        trie2.insert("122345", "ape");
        assertTrue(trie2.contains("122345"));
        trie2.insert("1223", "apple");
        assertFalse(trie2.contains("1223"));

    }

}
