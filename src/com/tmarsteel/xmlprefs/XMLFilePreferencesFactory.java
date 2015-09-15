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
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * PreferencesFactory implementation that stores the preferences in a user-defined file. To use it,
 * set the system property <tt>java.util.prefs.PreferencesFactory</tt> to
 * <tt>com.tmarsteel.xmlprefs.XMLFilePreferencesFactory</tt>
 * <br />
 * These are the file defaults and the system properties used to change them:
 * <table>
 *  <thead>
 *   <tr>
 *    <td>OS (File.separatorChar)</td>
 *    <td>Preferences</td>
 *    <td>System property</td>
 *    <td>default</td>
 *   </tr>
 *  </thead>
 *  <tbody>
 *   <tr>
 *    <td>\ DOS</td>
 *    <td>userRoot()</td>
 *    <td>com.tmarsteel.xmlprefs.XMLFilePreferencesFactory.userRoot</td>
 *    <td>%APPDATA%/Java/prefs.xml</td>
 *   </tr>
 *   <tr>
 *    <td>\ DOS</td>
 *    <td>systemRoot()</td>
 *    <td>com.tmarsteel.xmlprefs.XMLFilePreferencesFactory.systemRoot</td>
 *    <td>%SYSTEMROOT%/javaprefs.xml</td>
 *   </tr>
 *   <tr>
 *    <td>/ UNIX</td>
 *    <td>userRoot()</td>
 *    <td>com.tmarsteel.xmlprefs.XMLFilePreferencesFactory.userRoot</td>
 *    <td>$/.java/prefs.xml</td>
 *   </tr>
 *   <tr>
 *    <td>/ UNIX</td>
 *    <td>systemRoot()</td>
 *    <td>com.tmarsteel.xmlprefs.XMLFilePreferencesFactory.systemRoot</td>
 *    <td>/etc/java/systemprefs.xml</td>
 *   </tr>
 *  </tbody>
 * </table>
 *
 * @author Tobias Marstaller (<a href="//github.com/tmarsteel">github.com/tmarsteel</a>)
 */
public class XMLFilePreferencesFactory implements PreferencesFactory
{
    public static final String SYSTEMROOT_FILE_PROPERTY = "com.tmarsteel.xmlprefs.XMLFilePreferencesFactory.systemRoot";
    public static final String USERROOT_FILE_PROPERTY = "com.tmarsteel.xmlprefs.XMLFilePreferencesFactory.userRoot";
    
    private XMLFilePreferences systemRoot;
    private XMLFilePreferences userRoot;
    
    @Override
    public Preferences systemRoot()
    {
        if (systemRoot == null)
        {
            String path = System.getProperty(SYSTEMROOT_FILE_PROPERTY, null);
            
            if (path == null)
            {
                if (File.separatorChar == '\\')
                {
                    path = System.getenv("SYSTEMROOT") + "/javaprefs.xml";
                }
                else
                {
                    path = "/etc/java/systemprefs.xml";
                }
            }
            
            systemRoot = getFilePreferences(path);
        }
        
        return systemRoot;
    }

    @Override
    public Preferences userRoot()
    {
        if (userRoot == null)
        {
            String path = System.getProperty(USERROOT_FILE_PROPERTY, null);
            
            if (path == null)
            {
                if (File.separatorChar == '\\')
                {
                    path = System.getenv("APPDATA") + "/Java/prefs.xml";
                }
                else
                {
                    path = System.getProperty("user.home") + "/.java/prefs.xml";
                }
            }
            
            systemRoot = getFilePreferences(path);
        }
        
        return systemRoot;
    }
    
    private XMLFilePreferences getFilePreferences(String path)
    {
        try
        {
            return new XMLFilePreferences(new File(path));
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
