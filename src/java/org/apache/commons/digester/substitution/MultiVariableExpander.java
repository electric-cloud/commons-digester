/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//digester/src/java/org/apache/commons/digester/substitution/MultiVariableExpander.java,v 1.2 2004/01/10 17:43:46 rdonkin Exp $
 * $Revision: 1.2 $
 * $Date: 2004/01/10 17:43:46 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
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

package org.apache.commons.digester.substitution;

import java.util.Map;
import java.util.ArrayList;

/**
 * <p>Expands variable references from multiple sources.</p>
 *
 * @author Simon Kitching
 * @version $Revision: 1.2 $ $Date: 2004/01/10 17:43:46 $
 */

public class MultiVariableExpander implements VariableExpander {
    private int nEntries = 0;
    private ArrayList markers = new ArrayList(2);
    private ArrayList sources = new ArrayList(2);
    
    public MultiVariableExpander() {
    }
    
    public void addSource(String marker, Map source) {
        ++nEntries;
        markers.add(marker);
        sources.add(source);
    }

    /*    
     * Expands any variable declarations using any of the known
     * variable marker strings.
     * 
     * @throws IllegalArgumentException if the input param references
     * a variable which is not known to the specified source.
     */
    public String expand(String param) {
        for(int i=0; i<nEntries; ++i) {
            param = expand(
                param, 
                (String) markers.get(i), 
                (Map) sources.get(i));
        }
        return param;
    }
    
    /**
     * Replace any occurrences within the string of the form
     * "marker{key}" with the value from source[key].
     * <p>
     * Commonly, the variable marker is "$", in which case variables
     * are indicated by ${key} in the string.
     * <p>
     * Returns the string after performing all substitutions.
     * <p>
     * If no substitutions were made, the input string object is
     * returned (not a copy).
     *
     * @throws IllegalArgumentException if the input param references
     * a variable which is not known to the specified source.
     */
    public String expand(String str, String marker, Map source) {
        String startMark = marker + "{";
        int markLen = startMark.length();
        
        int index = 0;
        for(;;)
        {
            index = str.indexOf(startMark, index);
            if (index == -1)
            {
                return str;
            }
            
            int startIndex = index + markLen;
            if (startIndex > str.length())
            {
                throw new IllegalArgumentException(
                    "var expression starts at end of string");
            }
            
            int endIndex = str.indexOf("}", index + markLen);
            if (endIndex == -1)
            {
                throw new IllegalArgumentException(
                    "var expression starts but does not end");
            }
            
            String key = str.substring(index+markLen, endIndex);
            Object value =  source.get(key);
            if (value == null) {
                throw new IllegalArgumentException(
                    "parameter [" + key + "] is not defined.");
            }
            String varValue = value.toString();
            
            str = str.substring(0, index) + varValue + str.substring(endIndex+1);
            index += varValue.length();
        }
    }
        
}