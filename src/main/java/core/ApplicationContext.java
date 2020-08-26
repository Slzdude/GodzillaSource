package core;

import core.annotation.CryptionAnnotation;
import core.annotation.PayloadAnnotation;
import core.annotation.PluginnAnnotation;
import core.imp.Cryption;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import util.Log;
import util.http.Http;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ApplicationContext {
    public static final String VERSION = "1.00";
    private static final HashMap<String, Class> payloadMap;
    private static final HashMap<String, Class> cryptionMap;
    private static final HashMap<String, Class> pluginMap;
    public static int windowWidth;
    public static int windowsHeight;
    public static boolean easterEgg;
    private static File[] pluginJarFiles;
    private static Font font;
    private static Map<String, String> headerMap;

    static {
        windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        windowsHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        easterEgg = true;
        payloadMap = new HashMap<>();
        cryptionMap = new HashMap<>();
        pluginMap = new HashMap<>();
    }

    protected ApplicationContext() {
    }

    public static void init() {
        initFont();
        initHttpHeader();
        scanPluginJar();
        scanPayload();
        scanCryption();
        scanPlugin();
    }

    private static void initFont() {
        String fontName = Db.getSetingValue("font-name");
        String fontType = Db.getSetingValue("font-type");
        String fontSize = Db.getSetingValue("font-size");
        if (fontName != null && fontType != null && fontSize != null) {
            font = new Font(fontName, Integer.parseInt(fontType), Integer.parseInt(fontSize));
            InitGlobalFont(font);
        }

    }

    private static void initHttpHeader() {
        String headerString = getGloballHttpHeader();
        if (headerString != null) {
            String[] reqLines = headerString.split("\n");
            headerMap = new Hashtable<>();

            for (String reqLine : reqLines) {
                if (!reqLine.trim().isEmpty()) {
                    int index = reqLine.indexOf(":");
                    if (index > 1) {
                        String keyName = reqLine.substring(0, index).trim();
                        String keyValue = reqLine.substring(index + 1).trim();
                        headerMap.put(keyName, keyValue);
                    }
                }
            }
        }

    }

    private static void scanPayload() {
        try {
            URL url = ApplicationContext.class.getResource("/shells/payloads/");
            int loadNum = scanClass(url.toURI(), "shells.payloads", Payload.class, PayloadAnnotation.class, payloadMap);
            Log.log(String.format("load payload success! payloadMaxNum:%s onceLoadPayloadNum:%s", payloadMap.size(), loadNum));
        } catch (Exception var2) {
            Log.error(var2);
        }

    }

    private static void scanCryption() {
        try {
            URL url = ApplicationContext.class.getResource("/shells/cryptions/");
            int loadNum = scanClass(url.toURI(), "shells.cryptions", Cryption.class, CryptionAnnotation.class, cryptionMap);
            Log.log(String.format("load cryption success! cryptionMaxNum:%s onceLoadCryptionNum:%s", cryptionMap.size(), loadNum));
        } catch (Exception var2) {
            Log.error(var2);
        }

    }

    private static void scanPlugin() {
        try {
            URL url = ApplicationContext.class.getResource("/shells/plugins/");
            int loadNum = scanClass(url.toURI(), "shells.plugins", Plugin.class, PluginnAnnotation.class, pluginMap);
            Log.log(String.format("load plugin success! pluginMaxNum:%s onceLoadPluginNum:%s", pluginMap.size(), loadNum));
        } catch (Exception var2) {
            Log.error(var2);
        }

    }

    private static void scanPluginJar() {
        String[] pluginJars = Db.getAllPlugin();
        ArrayList<File> list = new ArrayList<>();

        for (String pluginJar : pluginJars) {
            File jarFile = new File(pluginJar);
            if (jarFile.exists() && jarFile.isFile()) {
                addJar(jarFile);
                list.add(jarFile);
            } else {
                Log.error(String.format("PluginJarFile : %s no found", pluginJar));
            }
        }

        pluginJarFiles = list.toArray(new File[0]);
        Log.log(String.format("load pluginJar success! pluginJarNum:%s LoadPluginJarSuccessNum:%s", pluginJars.length, pluginJars.length));
    }

    private static int scanClass(URI uri, String packageName, Class parentClass, Class annotationClass, Map destMap) {
        int num = scanClassX(uri, packageName, parentClass, annotationClass, destMap);

        for (File jarFile : pluginJarFiles) {
            num += scanClassByJar(jarFile, packageName, parentClass, annotationClass, destMap);
        }

        return num;
    }

    private static int scanClassX(URI uri, String packageName, Class parentClass, Class annotationClass, Map destMap) {
        if (System.getProperty("java.class.path").endsWith(".jar") && System.getProperty("java.class.path").indexOf(59) == -1) {
            return scanClassByJar(new File(System.getProperty("java.class.path")), packageName, parentClass, annotationClass, destMap);
        } else {
            int addNum = 0;

            try {
                File file = new File(uri);
                File[] file2 = file.listFiles();

                for (File objectFile : file2) {
                    if (objectFile.isDirectory()) {
                        File[] objectFiles = objectFile.listFiles();

                        for (File objectClassFile : objectFiles) {
                            if (objectClassFile.getPath().endsWith(".class")) {
                                try {
                                    String objectClassName = String.format("%s.%s.%s", packageName, objectFile.getName(), objectClassFile.getName().substring(0, objectClassFile.getName().length() - ".class".length()));
                                    Class objectClass = Class.forName(objectClassName);
                                    if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent(annotationClass)) {
                                        Annotation annotation = objectClass.getAnnotation(annotationClass);
                                        String name = (String) annotation.annotationType().getMethod("Name").invoke(annotation, (Object[]) null);
                                        destMap.put(name, objectClass);
                                        ++addNum;
                                    }
                                } catch (Exception var18) {
                                    Log.error(var18);
                                }
                            }
                        }
                    }
                }
            } catch (Exception var19) {
                Log.error(var19);
            }

            return addNum;
        }
    }

    private static int scanClassByJar(File srcJarFile, String packageName, Class parentClass, Class annotationClass, Map destMap) {
        int addNum = 0;

        try {
            JarFile jarFile = new JarFile(srcJarFile);
            Enumeration<JarEntry> jarFiles = jarFile.entries();
            packageName = packageName.replace(".", "/");

            while (jarFiles.hasMoreElements()) {
                JarEntry jarEntry = jarFiles.nextElement();
                String name = jarEntry.getName();
                if (name.startsWith(packageName) && name.endsWith(".class")) {
                    name = name.replace("/", ".");
                    name = name.substring(0, name.length() - 6);

                    try {
                        Class objectClass = Class.forName(name);
                        if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent(annotationClass)) {
                            Annotation annotation = objectClass.getAnnotation(annotationClass);
                            name = (String) annotation.annotationType().getMethod("Name").invoke(annotation, (Object[]) null);
                            destMap.put(name, objectClass);
                            ++addNum;
                        }
                    } catch (Exception var14) {
                        Log.error(var14);
                    }
                }
            }

            jarFile.close();
        } catch (Exception var15) {
            Log.error(var15);
        }

        return addNum;
    }

    public static String[] getAllPayload() {
        return payloadMap.keySet().toArray(new String[0]);
    }

    public static Payload getPayload(String payloadName) {
        Class payloadClass = payloadMap.get(payloadName);
        Payload payload = null;
        if (payloadClass != null) {
            try {
                payload = (Payload) payloadClass.newInstance();
            } catch (Exception var4) {
                Log.error(var4);
            }
        }

        return payload;
    }

    public static Plugin[] getAllPlugin(String payloadName) {
        ArrayList<Plugin> list = new ArrayList<>();

        for (String cryptionName : pluginMap.keySet()) {
            Class pluginClass = pluginMap.get(cryptionName);
            if (pluginClass != null) {
                PluginnAnnotation pluginAnnotation = (PluginnAnnotation) pluginClass.getAnnotation(PluginnAnnotation.class);
                if (pluginAnnotation.payloadName().equals(payloadName)) {
                    try {
                        Plugin plugin = (Plugin) pluginClass.newInstance();
                        list.add(plugin);
                    } catch (Exception var8) {
                        Log.error(var8);
                    }
                }
            }
        }

        return list.toArray(new Plugin[0]);
    }

    public static String[] getAllCryption(String payloadName) {
        ArrayList<String> list = new ArrayList<>();

        for (String cryptionName : cryptionMap.keySet()) {
            Class cryptionClass = cryptionMap.get(cryptionName);
            if (cryptionClass != null) {
                CryptionAnnotation cryptionAnnotation = (CryptionAnnotation) cryptionClass.getAnnotation(CryptionAnnotation.class);
                if (cryptionAnnotation.payloadName().equals(payloadName)) {
                    list.add(cryptionName);
                }
            }
        }

        return list.toArray(new String[0]);
    }

    public static Cryption getCryption(String payloadName, String crytionName) {
        Class cryptionClass = cryptionMap.get(crytionName);
        CryptionAnnotation cryptionAnnotation = (CryptionAnnotation) cryptionClass.getAnnotation(CryptionAnnotation.class);
        if (cryptionAnnotation.payloadName().equals(payloadName)) {
            Cryption cryption;

            try {
                cryption = (Cryption) cryptionClass.newInstance();
                return cryption;
            } catch (Exception var6) {
                Log.error(var6);
                return null;
            }
        }

        return null;
    }

    private static void addJar(File jarPath) {
        try {
            URLClassLoader classLoader = (URLClassLoader) ApplicationContext.class.getClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            URL url = jarPath.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception var4) {
            Log.error(var4);
        }

    }

    private static void InitGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        Enumeration<Object> keys = UIManager.getDefaults().keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }

    }

    public static Proxy getProxy(ShellEntity shellContext) {
        return ProxyT.getProxy(shellContext);
    }

    public static String[] getAllProxy() {
        return ProxyT.getAllProxyType();
    }

    public static String[] getAllEncodingTypes() {
        return Encoding.getAllEncodingTypes();
    }

    public static Http getHttp(ShellEntity shellEntity) {
        return new Http(shellEntity);
    }

    public static Font getFont() {
        return font;
    }

    public static void setFont(Font font) {
        Db.updateSetingKV("font-name", font.getName());
        Db.updateSetingKV("font-type", Integer.toString(font.getStyle()));
        Db.updateSetingKV("font-size", Integer.toString(font.getSize()));
        ApplicationContext.font = font;
    }

    public static void resetFont() {
        Db.removeSetingK("font-name");
        Db.removeSetingK("font-type");
        Db.removeSetingK("font-size");
    }

    public static String getGloballHttpHeader() {
        return Db.getSetingValue("globallHttpHeader");
    }

    public static Map<String, String> getGloballHttpHeaderX() {
        return headerMap;
    }

    public static boolean updateGloballHttpHeader(String header) {
        boolean state = Db.updateSetingKV("globallHttpHeader", header);
        initHttpHeader();
        return state;
    }

    public static boolean isGodMode() {
        return Boolean.valueOf(Db.getSetingValue("godMode"));
    }

    public static boolean setGodMode(boolean state) {
        return Db.updateSetingKV("godMode", String.valueOf(state));
    }
}
