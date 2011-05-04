/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
