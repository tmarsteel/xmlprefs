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

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements preferences on a {@link org.w3c.dom.Node}.
 * @author Tobias Marstaller (<a href="//github.com/tmarsteel">github.com/tmarsteel</a>)
 */
class XMLNodePreferences extends AbstractPreferences
{
    public static final String PROPERTY_NODENAME = "property";
    public static final String NODE_NODENAME = "node";
    
    private Node baseNode;
    private Preferences parent = null;
    
    /**
     * @param parent The parent preferences of this sub-preferences. Must
     * be an instance of {@link XMLFilePreferences} or {@link XMLNodePreferences}
     * @param name The name of this sub-node.
     */
    protected XMLNodePreferences(AbstractPreferences parent, String name)
    {
        super(parent, name);
        
        XMLNodePreferences nodeParent;

        if (parent instanceof XMLNodePreferences)
        {
            nodeParent = (XMLNodePreferences) parent; 
            
            Node thisNode = nodeParent.getNode(name);
            
            if (thisNode != null)
            {
                if (thisNode.getNodeName().equals(NODE_NODENAME))
                {
                    baseNode = nodeParent.getNodeNode(name);
                }
                else
                {
                    throw new IllegalStateException(name + " is already set as a property.");
                }
            }
        }
        else if (parent instanceof XMLFilePreferences)
        {
            nodeParent = ((XMLFilePreferences) parent).rootPreferences;
        }
        else
        {
            throw new IllegalArgumentException("The given parent must be an instance of " +
                XMLNodePreferences.class.getName() + " or " + XMLFilePreferences.class.getName());
        }

        if (baseNode == null)
        {
            baseNode = nodeParent.baseNode.getOwnerDocument().createElement(NODE_NODENAME);
            ((Element) baseNode).setAttribute("name", name);
            nodeParent.baseNode.appendChild(baseNode);
        }
 
        this.parent = parent;
    }
    
    /**
     * Creates a new preferences based on the given node; this constructors
     * <b>ONLY</b> purpose is to create the XMLFilePreferences.rootPreferences
     * object.
     */
    protected XMLNodePreferences(AbstractPreferences parent, Node node)
    {
        super(parent, "prefs");
        
        this.parent = parent;
        this.baseNode = node;
    }
    
    /**
     * Returns the property node with the key <code>key</code>.
     * @return The property node with the key <code>key</code> or null if no such
     * node exists.
     * @see PROPERTY_NODE_NAME#PROPERTY_NODENAME
     */
    private Node getPropertyNode(String key)
    {
        Node node = getNode(key);
        
        if (node == null || !node.getNodeName().equals(PROPERTY_NODENAME))
        {
            return null;
        }
        
        return node;
    }
    
    private Node getNodeNode(String key)
    {
        Node node = getNode(key);
        
        if (node == null || !node.getNodeName().equals(NODE_NODENAME))
        {
            return null;
        }
        
        return node;
    }
    
    /**
     * Returns the node with nodename PROPERTY_NODENAME or NODE_NODENAME whichs
     * name attribute has the value <code>name</code>
     * @return The node with nodename PROPERTY_NODENAME or NODE_NODENAME whichs
     * name attribute has the value <code>name</code>. Returns null if no such
     * node exists.
     */
    private Node getNode(String name)
    {
        NodeList childNodes = baseNode.getChildNodes();
        for (int i = 0;i < childNodes.getLength();i++)
        {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals(PROPERTY_NODENAME) || node.getNodeName().equals(NODE_NODENAME))
            {
                if (name.equals(((Element) node).getAttribute("name")))
                {
                    return node;
                }
            }
        }
        
        return null;
    }
    
    @Override
    protected void putSpi(String key, String value)
    {
        Node pNode = getPropertyNode(key);
        
        if (pNode == null)
        {
            // node not set yet, create it
            pNode = baseNode.getOwnerDocument().createElement(PROPERTY_NODENAME);
            ((Element) pNode).setAttribute("name", key);
            
            baseNode.appendChild(pNode);
        }
        
        pNode.setTextContent(value);
    }

    @Override
    protected String getSpi(String key)
    {
        Node pNode = getPropertyNode(key);
        
        return pNode == null? null : pNode.getTextContent();
    }

    @Override
    protected void removeSpi(String key)
    {
        Node pNode = getNode(key);
        
        if (pNode != null)
        {
            baseNode.removeChild(pNode);
        }  
    }

    @Override
    protected void removeNodeSpi()
        throws BackingStoreException
    {
        if (parent != null)
        {
            baseNode.getParentNode().removeChild(baseNode);
        }
    }

    @Override
    protected String[] keysSpi()
        throws BackingStoreException
    {
        NodeList childNodes = baseNode.getChildNodes();
        List<String> keys = new ArrayList<>();
        for (int i = 0;i < childNodes.getLength();i++)
        {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals(PROPERTY_NODENAME))
            {
                keys.add(((Element) node).getAttribute("name"));
            }
        }
        
        return keys.toArray(new String[keys.size()]);
    }

    @Override
    protected String[] childrenNamesSpi()
        throws BackingStoreException
    {
        NodeList childNodes = baseNode.getChildNodes();
        List<String> names = new ArrayList<>();
        for (int i = 0;i < childNodes.getLength();i++)
        {
            Node node = childNodes.item(i);
            if (node.getNodeName().equals(NODE_NODENAME))
            {
                names.add(((Element) node).getAttribute("name"));
            }
        }
        
        return names.toArray(new String[names.size()]);
    }

    @Override
    protected AbstractPreferences childSpi(String name)
    {
        return new XMLNodePreferences(this, name);
    }

    @Override
    public void sync()
        throws BackingStoreException
    {
        throw new UnsupportedOperationException("Syncing not supported.");
    }

    @Override
    public void flush()
        throws BackingStoreException
    {
        if (parent == null)
        {
            throw new BackingStoreException("This preferences has no parent and thus cannot be flush()ed.");
        }
        
        parent.flush();
    }

    @Override
    protected void syncSpi()
        throws BackingStoreException
    {
        throw new UnsupportedOperationException("Call sync() directly.");
    }

    @Override
    protected void flushSpi()
        throws BackingStoreException
    {
        throw new UnsupportedOperationException("Call flush() directly.");
    }
}
