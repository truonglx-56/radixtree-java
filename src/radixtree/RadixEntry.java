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

import java.util.Map;

/**
 *
 * @author daves
 */
public class RadixEntry<T> implements Map.Entry<String, T> {
    public String key;
    public T value;
    public RadixEntry(){}
    public RadixEntry(String k)         { key = k; }
    public RadixEntry(String k, T v)    { key = k; value = v; }
    public T setValue(T v)          { value = v; return v; }
    public T getValue()             { return value; }
    public String getKey()          { return key; }
    public String setKey(String k)  { key = k; return key; }

}
