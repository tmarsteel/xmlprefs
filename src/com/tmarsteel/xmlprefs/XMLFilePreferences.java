/*
 * The MIT License
 *
 * Copyright 2015 tobse-local.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tmarsteel.xmlprefs;

import java.io.File;
import java.io.IOException;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The XML file preferences implementation.
 * @author Tobias Marstaller (<a href="//github.com/tmarsteel">github.com/tmarsteel</a>)
 */
public class XMLFilePreferences extends AbstractPreferences
{
    private File backingFile;
    protected XMLNodePreferences rootPreferences;
    private Document rootDocument;
    
    public XMLFilePreferences(File backingFile)
        throws SAXException, IOException
    {
        super(null, "");
        this.backingFile = backingFile;
        this.rootDocument = readFile();
        
        if (rootDocument.getFirstChild() == null)
        {
            rootDocument.appendChild(rootDocument.createElement("prefs"));
        }

        rootPreferences = new XMLNodePreferences(this, rootDocument.getFirstChild());
    }
    
    /**
     * Returns the {@link Document} this preferences writes to.
     * @return The {@link Document} this preferences writes to.
     */
    public Document getRootDocument()
    {
        return rootDocument;
    }
    
    /**
     * Reads the underlying file and returns the document in it.
     * @return The document in <code>backingFile</code>
     */
    private Document readFile()
        throws SAXException, IOException
    {
        DocumentBuilderFactory dbf;
        DocumentBuilder builder;
        
        try
        {
            dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);

            builder = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex)
        {
            throw new RuntimeException(ex);
        }
        
        if (backingFile.exists())
        {
            return builder.parse(backingFile);
        }
        else
        {
            return builder.newDocument();
        }
    }
    
    @Override
    protected void putSpi(String key, String value)
    {
        rootPreferences.putSpi(key, value);
    }

    @Override
    protected String getSpi(String key)
    {
        return rootPreferences.getSpi(key);
    }

    @Override
    protected void removeSpi(String key)
    {
        rootPreferences.removeSpi(key);
    }

    @Override
    protected void removeNodeSpi()
        throws BackingStoreException
    {
        rootPreferences.removeNodeSpi();
    }

    @Override
    protected String[] keysSpi()
        throws BackingStoreException
    {
        return rootPreferences.keysSpi();
    }

    @Override
    protected String[] childrenNamesSpi()
        throws BackingStoreException
    {
       return rootPreferences.childrenNamesSpi();
    }

    @Override
    protected AbstractPreferences childSpi(String name)
    {
        return rootPreferences.childSpi(name);
    }

    @Override
    public void sync()
        throws BackingStoreException
    {
        throw new UnsupportedOperationException("Syncing not supported.");
    }
    
    @Override
    protected void syncSpi()
        throws BackingStoreException
    {
        throw new UnsupportedOperationException("Syncing not supported.");
    }

    @Override
    protected void flushSpi()
        throws BackingStoreException
    {
        throw new UnsupportedOperationException("call flush() directly.");
    }
    
    @Override
    public void flush()
        throws BackingStoreException
    {
        try
        {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            
            t.transform(new DOMSource(rootDocument), new StreamResult(backingFile));
        }
        catch (TransformerConfigurationException ex)
        {
            throw new RuntimeException(ex);
        }
        catch (TransformerException ex)
        {
            throw new BackingStoreException(ex);
        }
    }
}
