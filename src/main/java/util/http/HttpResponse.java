package util.http;

import core.shell.ShellEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HttpResponse {
   private byte[] result;
   private ShellEntity shellEntity;
   private Map headerMap;
   private String message;

   public byte[] getResult() {
      return this.result;
   }

   public Map getHeaderMap() {
      return this.headerMap;
   }

   public void setResult(byte[] result) {
      this.result = result;
   }

   public void setHeaderMap(Map headerMap) {
      this.headerMap = headerMap;
   }

   public HttpResponse(HttpURLConnection http, ShellEntity shellEntity) throws IOException {
      this.shellEntity = shellEntity;
      this.handleHeader(http.getHeaderFields());
      this.ReadAllData(this.getInputStream(http));
   }

   protected void handleHeader(Map map) {
      this.headerMap = map;
      this.message = (String)((List)map.get((Object)null)).get(0);
      List cookieList = (List)map.getOrDefault("Set-Cookie", new ArrayList());
      StringBuffer buffer = new StringBuffer();
      Iterator iterator = cookieList.iterator();

      while(iterator.hasNext()) {
         String cookieValue = (String)iterator.next();
         String[] _cookie = cookieValue.split(";");
         buffer.append(_cookie[0]);
         buffer.append(";");
      }

      if (buffer.length() > 1) {
         String oldCookie = (String)this.shellEntity.getHeaders().getOrDefault("Cookie", "");
         this.shellEntity.getHeaders().put("Cookie", (oldCookie.trim().length() > 0 ? oldCookie : "") + buffer.toString());
      }

   }

   protected InputStream getInputStream(HttpURLConnection httpURLConnection) throws IOException {
      InputStream inputStream = httpURLConnection.getErrorStream();
      return inputStream != null ? inputStream : httpURLConnection.getInputStream();
   }

   protected void ReadAllData(InputStream inputStream) throws IOException {
      boolean var2 = false;

      try {
         if (this.headerMap.get("Content-Length") != null && ((List)this.headerMap.get("Content-Length")).size() > 0) {
            int maxLen = Integer.parseInt((String)((List)this.headerMap.get("Content-Length")).get(0));
            this.result = this.ReadKnownNumData(inputStream, maxLen);
         } else {
            this.result = this.ReadUnknownNumData(inputStream);
         }
      } catch (NumberFormatException var4) {
         this.result = this.ReadUnknownNumData(inputStream);
      }

      this.result = this.shellEntity.getCryptionModel().decode(this.result);
   }

   protected byte[] ReadKnownNumData(InputStream inputStream, int num) throws IOException {
      if (num <= 0) {
         return num == 0 ? this.ReadUnknownNumData(inputStream) : null;
      } else {
         byte[] temp = new byte[5120];
         int readOneNum = false;
         ByteArrayOutputStream bos = new ByteArrayOutputStream();

         int readOneNum;
         while((readOneNum = inputStream.read(temp)) != -1) {
            bos.write(temp, 0, readOneNum);
         }

         return bos.toByteArray();
      }
   }

   protected byte[] ReadUnknownNumData(InputStream inputStream) throws IOException {
      byte[] temp = new byte[5120];
      int readOneNum = false;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      int readOneNum;
      while((readOneNum = inputStream.read(temp)) != -1) {
         bos.write(temp, 0, readOneNum);
      }

      return bos.toByteArray();
   }
}
