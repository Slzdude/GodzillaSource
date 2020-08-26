package core;

import core.shell.ShellEntity;
import java.io.UnsupportedEncodingException;
import util.Log;

public class Encoding {
   private String charsetString;
   private static final String[] ENCODING_TYPES = new String[]{"UTF-8", "GBK", "GB2312", "BIG5", "GB18030", "ISO-8859-1"};

   private Encoding(String charsetString) {
      this.charsetString = charsetString;
   }

   public static String[] getAllEncodingTypes() {
      return ENCODING_TYPES;
   }

   public byte[] Encoding(String string) {
      try {
         return string.getBytes(this.charsetString);
      } catch (UnsupportedEncodingException var3) {
         Log.error((Exception)var3);
         return string.getBytes();
      }
   }

   public String Decoding(byte[] srcData) {
      if (srcData == null) {
         return "";
      } else {
         try {
            return new String(srcData, this.charsetString);
         } catch (UnsupportedEncodingException var3) {
            Log.error((Exception)var3);
            return new String(srcData);
         }
      }
   }

   public static Encoding getEncoding(ShellEntity entity) {
      return new Encoding(entity.getEncoding());
   }

   public static Encoding getEncoding(String charsetString) {
      return new Encoding(charsetString);
   }
}
