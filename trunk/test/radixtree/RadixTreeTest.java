package radixtree;

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
        String key = "";
        Integer newVal;
        RadixTreeSearchResult<Integer> searchResult = null;
        RadixTree<Integer> deck = new RadixTree<Integer>(){
            @Override
            @SuppressWarnings (value="unchecked")
            public boolean continueInsert(String key, Integer newVal, RadixTreeSearchResult<Integer> searchResult ){
            //void modifyInsertPos(RadixTreeSearchResult<T> insPosRes){
                //System.out.println("test");
                if (searchResult.matchList.size() > 0 && searchResult.matchList.peekFirst().value.equals(newVal))
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

        deck.insert("55", 6);
        deck.insert("556", 6);
        deck.insert("55671", 8);
        deck.insert("55672", 8);
        deck.insert("55673", 8);
        deck.insert("55674", 8);
        deck.insert("55675", 8);
        deck.insert("55676", 8);
        deck.insert("55677", 8);
        deck.insert("55678", 8);
        deck.insert("55679", 8);
        assertEquals(true, deck.contains("55"));
        assertEquals(true, deck.contains("55677"));
        assertEquals(false, deck.contains("556") );
        deck.insert("55670", 8);
        assertEquals(false, deck.contains("55677"));
        assertEquals(true, deck.contains("5567"));
    }

}
