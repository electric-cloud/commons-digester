/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/test/org/apache/commons/digester/DigesterTestCase.java,v 1.1 2001/05/22 04:19:11 craigmcc Exp $
 * $Revision: 1.1 $
 * $Date: 2001/05/22 04:19:11 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */


package org.apache.commons.digester;


import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.ErrorHandler;



/**
 * <p>Test Case for the Digester class.  These tests exercise the individual
 * methods of a Digester, but do not attempt to process complete documents.
 * </p>
 *
 * @author Craig R. McClanahan
 * @version $Revision: 1.1 $ $Date: 2001/05/22 04:19:11 $
 */

public class DigesterTestCase extends TestCase {


    // ----------------------------------------------------- Instance Variables


    /**
     * The digester instance we will be processing.
     */
    protected Digester digester = null;


    /**
     * The set of public identifiers, and corresponding resource names,
     * for the versions of the DTDs that we know about.  There
     * <strong>MUST</strong> be an even number of Strings in this array.
     */
    protected static final String registrations[] = {
        "-//Netscape Communications//DTD RSS 0.9//EN",
        "/org/apache/commons/digester/rss/rss-0.9.dtd",
        "-//Netscape Communications//DTD RSS 0.91//EN",
        "/org/apache/commons/digester/rss/rss-0.91.dtd",
    };


    // ----------------------------------------------------------- Constructors


    /**
     * Construct a new instance of this test case.
     *
     * @param name Name of the test case
     */
    public DigesterTestCase(String name) {

        super(name);

    }


    // -------------------------------------------------- Overall Test Methods


    /**
     * Set up instance variables required by this test case.
     */
    public void setUp() {

        digester = new Digester();

    }


    /**
     * Return the tests included in this test suite.
     */
    public static Test suite() {

        return (new TestSuite(DigesterTestCase.class));

    }


    /**
     * Tear down instance variables required by this test case.
     */
    public void tearDown() {

        digester = null;

    }



    // ------------------------------------------------ Individual Test Methods


    /**
     * Test the basic property getters and setters.
     */
    public void testProperties() {

        assertEquals("Initial debug is zero", 0, digester.getDebug());
        digester.setDebug(1);
        assertEquals("Set debug is one", 1, digester.getDebug());
        digester.setDebug(0);
        assertEquals("Reset debug is zero", 0, digester.getDebug());

        assertNull("Initial error handler is null",
                   digester.getErrorHandler());
        digester.setErrorHandler((ErrorHandler) digester);
        assert("Set error handler is digester",
               digester.getErrorHandler() == digester);
        digester.setErrorHandler(null);
        assertNull("Reset error handler is null",
                   digester.getErrorHandler());

        assert("Initial namespace aware is false",
               !digester.getNamespaceAware());
        digester.setNamespaceAware(true);
        assert("Set namespace aware is true",
               digester.getNamespaceAware());
        digester.setNamespaceAware(false);
        assert("Reset namespace aware is false",
               !digester.getNamespaceAware());

        assert("Initial validating is false",
               !digester.getValidating());
        digester.setValidating(true);
        assert("Set validating is true",
               digester.getValidating());
        digester.setValidating(false);
        assert("Reset validating is false",
               !digester.getValidating());

    }


    /**
     * Test registration of URLs for specified public identifiers.
     */
    public void testRegistrations() {

        Map map = digester.getRegistrations();
        assertEquals("Initially zero registrations", 0, map.size());
        int n = 0;
        for (int i = 0; i < registrations.length; i += 2) {
            URL url = this.getClass().getResource(registrations[i + 1]);
            if (url != null) {
                digester.register(registrations[i], url.toString());
                n++;
            }
        }
        map = digester.getRegistrations();
        assertEquals("Registered two URLs", n, map.size());

        int count[] = new int[n];
        for (int i = 0; i < n; i++)
            count[i] = 0;
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            for (int i = 0; i < n; i++) {
                if (key.equals(registrations[i*2])) {
                    count[i]++;
                    break;
                }
            }
        }
        for (int i = 0; i < n; i++)
            assertEquals("Count for key " + registrations[i*2],
                         1, count[i]);

    }


    /**
     * Test the basic stack mechanisms.
     */
    public void testStackMethods() {

        Object value = null;

        // New stack must be empty
        assertEquals("New stack is empty", 0, digester.getCount());
        value = digester.peek();
        assertNull("New stack peek() returns null", value);
        value = digester.pop();
        assertNull("New stack pop() returns null", value);

        // Test pushing and popping activities
        digester.push("First Item");
        assertEquals("Pushed one item size", 1, digester.getCount());
        value = digester.peek();
        assertNotNull("Peeked first item is not null", value);
        assertEquals("Peeked first item value", "First Item", (String) value);

        digester.push("Second Item");
        assertEquals("Pushed two items size", 2, digester.getCount());
        value = digester.peek();
        assertNotNull("Peeked second item is not null", value);
        assertEquals("Peeked second item value", "Second Item", (String) value);

        value = digester.pop();
        assertEquals("Popped stack size", 1, digester.getCount());
        assertNotNull("Popped second item is not null", value);
        assertEquals("Popped second item value", "Second Item", (String) value);
        value = digester.peek();
        assertNotNull("Remaining item is not null", value);
        assertEquals("Remaining item value", "First Item", (String) value);
        assertEquals("Remaining stack size", 1, digester.getCount());

        // Cleared stack is empty
        digester.push("Dummy Item");
        digester.clear();
        assertEquals("Cleared stack is empty", 0, digester.getCount());
        value = digester.peek();
        assertNull("Cleared stack peek() returns null", value);
        value = digester.pop();
        assertNull("Cleared stack pop() returns null", value);

    }



}