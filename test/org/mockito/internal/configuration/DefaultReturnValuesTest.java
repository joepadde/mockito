/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;

import static org.hamcrest.CoreMatchers.*;

public class DefaultReturnValuesTest extends TestBase {
    
    @SuppressWarnings("unchecked")
    @Test public void shouldReturnEmptyCollectionsOrNullForNonCollections() {
        DefaultReturnValues values = new DefaultReturnValues();
        
        assertTrue(((Collection) values.returnValueFor(Collection.class)).isEmpty());

        assertTrue(((Set) values.returnValueFor(Set.class)).isEmpty());
        assertTrue(((SortedSet) values.returnValueFor(SortedSet.class)).isEmpty());
        assertTrue(((HashSet) values.returnValueFor(HashSet.class)).isEmpty());
        assertTrue(((TreeSet) values.returnValueFor(TreeSet.class)).isEmpty());
        assertTrue(((LinkedHashSet) values.returnValueFor(LinkedHashSet.class)).isEmpty());

        assertTrue(((List) values.returnValueFor(List.class)).isEmpty());
        assertTrue(((ArrayList) values.returnValueFor(ArrayList.class)).isEmpty());
        assertTrue(((LinkedList) values.returnValueFor(LinkedList.class)).isEmpty());

        assertTrue(((Map) values.returnValueFor(Map.class)).isEmpty());
        assertTrue(((SortedMap) values.returnValueFor(SortedMap.class)).isEmpty());
        assertTrue(((HashMap) values.returnValueFor(HashMap.class)).isEmpty());
        assertTrue(((TreeMap) values.returnValueFor(TreeMap.class)).isEmpty());
        assertTrue(((LinkedHashMap) values.returnValueFor(LinkedHashMap.class)).isEmpty());
        
        assertNull(values.returnValueFor(String.class));
    }
    
    interface HasPrimitives {
        boolean booleanMethod();
        char charMethod();
        int intMethod();
        long longMethod();
        float floatMethod();
        double doubleMethod();
    }
    
    @Test public void shouldReturnPrimitive() {
        DefaultReturnValues values = new DefaultReturnValues();
        
        assertEquals(false, values.returnValueFor(Boolean.TYPE));
        assertEquals((char) 0, values.returnValueFor(Character.TYPE));
        assertEquals(0, values.returnValueFor(Byte.TYPE));
        assertEquals(0, values.returnValueFor(Short.TYPE));
        assertEquals(0, values.returnValueFor(Integer.TYPE));
        assertEquals(0, values.returnValueFor(Long.TYPE));
        assertEquals(0, values.returnValueFor(Float.TYPE));
        assertEquals(0, values.returnValueFor(Double.TYPE));
        
        HasPrimitives mock = Mockito.mock(HasPrimitives.class);
        assertThat(mock.booleanMethod(), is(false));
        assertThat(mock.charMethod(), is((char)0));
        assertThat(mock.intMethod(), is((int)0));
        assertThat(mock.longMethod(), is((long)0));
        assertThat(mock.floatMethod(), is((float)0));
        assertThat(mock.doubleMethod(), is((double)0));
    }
}
