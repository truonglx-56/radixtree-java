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

public class RadixTreeSearchResult<T> {
    public LinkedList<RadixTreeNode<T>> matchList;
    public LinkedList<String> matchFullKeyList;
    public RadixTreeNode<T> partialMatch;
    public int lastTryNumMatchingChars;
    public boolean exactMatch;
    public RadixTreeNode<T> lastTry;
    public String lastMatchKey;
    public RadixTreeNode<T> previousNode;
    public boolean belongsBefore;
    public RadixTreeNode<T> parent;

    protected  RadixTreeSearchResult(){
        matchList = new LinkedList<RadixTreeNode<T>>();
        matchFullKeyList = new LinkedList<String>();
        exactMatch = false;
    }

}
