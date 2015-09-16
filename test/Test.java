import com.tmarsteel.xmlprefs.XMLFilePreferences;
import java.io.File;
import java.util.prefs.Preferences;

/**
 *
 * @author Tobias Marstaller (<a href="//github.com/tmarsteel">github.com/tmarsteel</a>)
 */
public class Test
{
    public static void main(String... args)
        throws Exception
    {
        File prefFile = new File("test.xml");
        
        Preferences prefs = new XMLFilePreferences(prefFile);
        
        prefs.put("key", "value");
        prefs.node("someNode").put("key2", "value2");
        
        prefs.flush();
        
        prefs = new XMLFilePreferences(prefFile);
        
        if (!"value".equals(prefs.get("key", null)))
        {
            System.err.println("Top-Level key not stored correctly");
        }
        
        if (!"value2".equals(prefs.node("someNode").get("key2", null)))
        {
            System.err.println("Key in Sub-Node not stored correctly");
        }
        
        prefs.node("someNode").removeNode();
        prefs.remove("key");
        
        prefs.flush();
        
        prefs = new XMLFilePreferences(prefFile);
        
        if (prefs.get("key", null) != null)
        {
            System.err.println("Deleting toplevel property failed");
        }
        
        if (prefs.nodeExists("someNode"))
        {
            System.err.println("Deleting toplevel node failed");
            
            if (prefs.node("someNode").get("key2", null) != null)
            {
                System.err.println("Deleting sublevel property failed");
            }
        }
    }
}
