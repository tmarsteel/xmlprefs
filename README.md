# xmlprefs
A XML implementation of the Java Preferences API

## Global use

```java
System.setProperty("java.util.prefs.PreferencesFactory", "com.tmarsteel.xmlprefs.XMLFilePreferencesFactory");

// then use the preferences as usual

Preferences prefs = Preferences.userRoot();

prefs.put("key", "value");
prefs.node("subNode").put("key2", "value2");
prefs.flush();
```

## Isolated use

To store preferences to a file of your choice, just contstruct a `XMLFilePreferences`:

```java
Preferences prefs = new XMLFilePreferences(new File("prefs.xml"));

prefs.put("key", "value");
prefs.node("subNode").put("key2", "value2");
prefs.flush();
```

## Imporant notes

*  `sync()` is not supported.
*  `flush()` always flushes the entire tree, not only the node it was called on

**Therefore:** use `userRoot()` and `systemRoot()` only if you give a shit about other applications preferences that use this implementation, too. Instead, use a XML file in your applications directory, the user.home or %APPDATA%.
