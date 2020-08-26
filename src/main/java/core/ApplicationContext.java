package core;

import core.annotation.CryptionAnnotation;
import core.annotation.PayloadAnnotation;
import core.annotation.PluginnAnnotation;
import core.imp.Cryption;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import util.Log;
import util.http.Http;

public class ApplicationContext {
   public static final String VERSION = "1.00";
   private static HashMap payloadMap;
   private static HashMap cryptionMap;
   private static HashMap pluginMap;
   private static File[] pluginJarFiles;
   public static int windowWidth;
   public static int windowsHeight;
   public static boolean easterEgg;
   private static Font font;
   private static Map headerMap;

   static {
      windowWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
      windowsHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
      easterEgg = true;
      payloadMap = new HashMap();
      cryptionMap = new HashMap();
      pluginMap = new HashMap();
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
         headerMap = new Hashtable();

         for(int i = 0; i < reqLines.length; ++i) {
            if (!reqLines[i].trim().isEmpty()) {
               int index = reqLines[i].indexOf(":");
               if (index > 1) {
                  String keyName = reqLines[i].substring(0, index).trim();
                  String keyValue = reqLines[i].substring(index + 1, reqLines[i].length()).trim();
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
      ArrayList list = new ArrayList();

      for(int i = 0; i < pluginJars.length; ++i) {
         File jarFile = new File(pluginJars[i]);
         if (jarFile.exists() && jarFile.isFile()) {
            addJar(jarFile);
            list.add(jarFile);
         } else {
            Log.error(String.format("PluginJarFile : %s no found", pluginJars[i]));
         }
      }

      pluginJarFiles = (File[])list.toArray(new File[0]);
      Log.log(String.format("load pluginJar success! pluginJarNum:%s LoadPluginJarSuccessNum:%s", pluginJars.length, pluginJars.length));
   }

   private static int scanClass(URI uri, String packageName, Class parentClass, Class annotationClass, Map destMap) {
      int num = scanClassX(uri, packageName, parentClass, annotationClass, destMap);

      for(int i = 0; i < pluginJarFiles.length; ++i) {
         File jarFile = pluginJarFiles[i];
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

            for(int i = 0; i < file2.length; ++i) {
               File objectFile = file2[i];
               if (objectFile.isDirectory()) {
                  File[] objectFiles = objectFile.listFiles();

                  for(int j = 0; j < objectFiles.length; ++j) {
                     File objectClassFile = objectFiles[j];
                     if (objectClassFile.getPath().endsWith(".class")) {
                        try {
                           String objectClassName = String.format("%s.%s.%s", packageName, objectFile.getName(), objectClassFile.getName().substring(0, objectClassFile.getName().length() - ".class".length()));
                           Class objectClass = Class.forName(objectClassName);
                           if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent(annotationClass)) {
                              Annotation annotation = objectClass.getAnnotation(annotationClass);
                              String name = (String)annotation.annotationType().getMethod("Name").invoke(annotation, (Object[])null);
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
         Enumeration jarFiles = jarFile.entries();
         packageName = packageName.replace(".", "/").substring(0);

         while(jarFiles.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry)jarFiles.nextElement();
            String name = jarEntry.getName();
            if (name.startsWith(packageName) && name.endsWith(".class")) {
               name = name.replace("/", ".");
               name = name.substring(0, name.length() - 6);

               try {
                  Class objectClass = Class.forName(name);
                  if (parentClass.isAssignableFrom(objectClass) && objectClass.isAnnotationPresent(annotationClass)) {
                     Annotation annotation = objectClass.getAnnotation(annotationClass);
                     name = (String)annotation.annotationType().getMethod("Name").invoke(annotation, (Object[])null);
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
      Set keys = payloadMap.keySet();
      return (String[])keys.toArray(new String[0]);
   }

   public static Payload getPayload(String payloadName) {
      Class payloadClass = (Class)payloadMap.get(payloadName);
      Payload payload = null;
      if (payloadClass != null) {
         try {
            payload = (Payload)payloadClass.newInstance();
         } catch (Exception var4) {
            Log.error(var4);
         }
      }

      return payload;
   }

   public static Plugin[] getAllPlugin(String payloadName) {
      Iterator keys = pluginMap.keySet().iterator();
      ArrayList list = new ArrayList();

      while(keys.hasNext()) {
         String cryptionName = (String)keys.next();
         Class pluginClass = (Class)pluginMap.get(cryptionName);
         if (pluginClass != null) {
            PluginnAnnotation pluginAnnotation = (PluginnAnnotation)pluginClass.getAnnotation(PluginnAnnotation.class);
            if (pluginAnnotation.payloadName().equals(payloadName)) {
               try {
                  Plugin plugin = (Plugin)pluginClass.newInstance();
                  list.add(plugin);
               } catch (Exception var8) {
                  Log.error(var8);
               }
            }
         }
      }

      return (Plugin[])list.toArray(new Plugin[0]);
   }

   public static String[] getAllCryption(String payloadName) {
      Iterator keys = cryptionMap.keySet().iterator();
      ArrayList list = new ArrayList();

      while(keys.hasNext()) {
         String cryptionName = (String)keys.next();
         Class cryptionClass = (Class)cryptionMap.get(cryptionName);
         if (cryptionClass != null) {
            CryptionAnnotation cryptionAnnotation = (CryptionAnnotation)cryptionClass.getAnnotation(CryptionAnnotation.class);
            if (cryptionAnnotation.payloadName().equals(payloadName)) {
               list.add(cryptionName);
            }
         }
      }

      return (String[])list.toArray(new String[0]);
   }

   public static Cryption getCryption(String payloadName, String crytionName) {
      Class cryptionClass = (Class)cryptionMap.get(crytionName);
      if (cryptionMap != null) {
         CryptionAnnotation cryptionAnnotation = (CryptionAnnotation)cryptionClass.getAnnotation(CryptionAnnotation.class);
         if (cryptionAnnotation.payloadName().equals(payloadName)) {
            Cryption cryption = null;

            try {
               cryption = (Cryption)cryptionClass.newInstance();
               return cryption;
            } catch (Exception var6) {
               Log.error(var6);
               return null;
            }
         }
      }

      return null;
   }

   private static void addJar(File jarPath) {
      try {
         URLClassLoader classLoader = (URLClassLoader)ApplicationContext.class.getClassLoader();
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
      Enumeration keys = UIManager.getDefaults().keys();

      while(keys.hasMoreElements()) {
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
      Http httpx = new Http(shellEntity);
      return httpx;
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

   public static Map getGloballHttpHeaderX() {
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
