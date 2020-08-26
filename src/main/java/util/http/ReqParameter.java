package util.http;

import java.util.HashMap;
import java.util.Iterator;
import util.functions;

public class ReqParameter {
   HashMap hashMap = new HashMap();

   public ReqParameter() {
   }

   public void add(String name, byte[] value) {
      this.hashMap.put(name, value);
   }

   public void add(String name, String value) {
      this.hashMap.put(name, value);
   }

   public Object get(String name) {
      return this.hashMap.get(name);
   }

   public String format() {
      Iterator keys = this.hashMap.keySet().iterator();

      StringBuffer buffer;
      for(buffer = new StringBuffer(); keys.hasNext(); buffer.append("&")) {
         String key = (String)keys.next();
         buffer.append(key);
         buffer.append("=");
         Object valueObject = this.hashMap.get(key);
         if (valueObject.getClass().isAssignableFrom(byte[].class)) {
            buffer.append(functions.base64Encode((byte[])valueObject));
         } else {
            buffer.append(functions.base64Encode(((String)valueObject).getBytes()));
         }
      }

      String temString = buffer.delete(buffer.length() - 1, buffer.length()).toString();
      return temString;
   }
}
