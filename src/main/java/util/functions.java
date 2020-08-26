//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package util;

import util.http.Http;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.Desktop.Action;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class functions {
    private static final char[] toBase64 = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final char[] toBase64URL = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};

    public functions() {
    }

    public static void concatMap(Map<String, List<String>> receiveMap, Map<String, List<String>> map) {
        Iterator<String> iterator = map.keySet().iterator();
        String key;

        while (iterator.hasNext()) {
            key = iterator.next();
            receiveMap.put(key, map.get(key));
        }

    }

    public static void fireActionEventByJComboBox(JComboBox comboBox) {
        try {
            Method method = comboBox.getClass().getDeclaredMethod("fireActionEvent", (Class[]) null);
            method.setAccessible(true);
            method.invoke(comboBox, (Object[]) null);
        } catch (Exception var2) {
            Log.error(var2);
        }

    }

    public static boolean toBoolean(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch (Exception var2) {
            Log.error(var2);
            return false;
        }
    }

    public static byte[] ipToByteArray(String paramString) {
        String[] array2 = paramString.split("\\.");
        byte[] array = new byte[4];

        for (int i = 0; i < array2.length; ++i) {
            array[i] = (byte) Integer.parseInt(array2[i]);
        }

        return array;
    }

    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];

        for (int i = 0; i < 2; ++i) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) (s >>> offset & 255);
        }

        return targets;
    }

    public static byte[] intToBytes(int value) {
        return new byte[]{(byte) (value & 255), (byte) (value >> 8 & 255), (byte) (value >> 16 & 255), (byte) (value >> 24 & 255)};
    }

    public static String byteArrayToHex(byte[] bytes) {
        String strHex;
        StringBuilder sb = new StringBuilder();

        for (byte aByte : bytes) {
            strHex = Integer.toHexString(aByte & 255);
            sb.append(strHex.length() == 1 ? "0" + strHex : strHex);
        }

        return sb.toString().trim();
    }

    public static byte[] hexToByte(String hex) {
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];

        for (int i = 0; i < byteLen; ++i) {
            int m = i * 2 + 1;
            int n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = (byte) intVal;
        }

        return ret;
    }

    public static Object concatArrays(Object array1, int array1_Start, int array1_End, Object array2, int array2_Start, int array2_End) {
        if (array1.getClass().isArray() && array2.getClass().isArray()) {
            if (array1_Start >= 0 && array2_End >= 0 && array2_Start >= 0) {
                int array1len = array1_Start != array1_End ? array1_End - array1_Start + 1 : 0;
                int array2len = array2_Start != array2_End ? array2_End - array2_Start + 1 : 0;
                int maxLen = array1len + array2len;
                byte[] data = new byte[maxLen];
                System.arraycopy(array1, array1_Start, data, 0, array1len);
                System.arraycopy(array2, array2_Start, data, array1len, array2len);
                return data;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void addShutdownHook(final Class<?> cls, final Object object) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                cls.getMethod("Tclose", (Class[]) null).invoke(object, (Object[]) null);
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        }));
    }

    public static short bytesToShort(byte[] bytes) {
        return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }

    public static int stringToint(String intString) {
        try {
            return Integer.parseInt(intString);
        } catch (Exception var2) {
            return 0;
        }
    }

    public static byte[] aes(int opmode, byte[] key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(opmode, new SecretKeySpec(key, "AES"), new IvParameterSpec(key));
            return cipher.doFinal(data);
        } catch (Exception var4) {
            Log.error(var4);
            return null;
        }
    }

    public static void openBrowseUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                URI uri = URI.create(url);
                Desktop dp = Desktop.getDesktop();
                if (dp.isSupported(Action.BROWSE)) {
                    dp.browse(uri);
                }
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] temp = new byte[5120];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int readOneNum;
        while ((readOneNum = inputStream.read(temp)) != -1) {
            bos.write(temp, 0, readOneNum);
        }

        return bos.toByteArray();
    }

    public static HashMap<String, String> matcherTwoChild(String data, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(data);
        HashMap<String, String> hashMap = new HashMap<>();

        while (m.find()) {
            try {
                String v1 = m.group(1);
                String v2 = m.group(2);
                hashMap.put(v1, v2);
            } catch (Exception var8) {
                Log.error(var8);
            }
        }

        return hashMap;
    }

    public static short[] toShortArray(byte[] src) {
        int count = src.length >> 1;
        short[] dest = new short[count];

        for (int i = 0; i < count; ++i) {
            dest[i] = (short) (src[i * 2] << 8 | src[2 * i + 1] & 255);
        }

        return dest;
    }

    public static byte[] stringToByteArray(String data, String encodng) {
        try {
            return data.getBytes(encodng);
        } catch (Exception var3) {
            return data.getBytes();
        }
    }

    public static byte[] httpReqest(String urlString, String method, HashMap<String, String> headers, byte[] data) {
        byte[] result = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setDoInput(true);
            httpConn.setDoOutput(!"GET".equals(method.toUpperCase()));
            httpConn.setConnectTimeout(3000);
            httpConn.setReadTimeout(3000);
            httpConn.setRequestMethod(method.toUpperCase());
            Http.addHttpHeader(httpConn, headers);
            if (httpConn.getDoOutput() && data != null) {
                httpConn.getOutputStream().write(data);
            }

            InputStream inputStream = httpConn.getInputStream();
            result = readInputStream(inputStream);
        } catch (Exception var8) {
            Log.error(var8);
        }

        return result;
    }

    public static String formatDir(String dirString) {
        if (dirString != null && dirString.length() > 0) {
            dirString = dirString.trim();
            dirString = dirString.replaceAll("\\\\+", "/").replaceAll("/+", "/").trim();
            if (dirString.charAt(dirString.length() - 1) != '/') {
                dirString = dirString + "/";
            }

            return dirString;
        } else {
            return "";
        }
    }

    public static boolean filePutContent(String file, byte[] data) {
        return filePutContent(new File(file), data);
    }

    public static boolean filePutContent(File file, byte[] data) {
        boolean state = false;

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
            state = true;
        } catch (Exception e) {
            Log.error(e);
        }

        return state;
    }

    public static String concatCookie(String oldCookie, String newCookie) {
        oldCookie = oldCookie + ";";
        newCookie = newCookie + ";";
        StringBuilder cookieBuffer = new StringBuilder();
        Map<String, String> cookieMap = new HashMap<>();
        String[] tmpA = oldCookie.split(";");

        String[] temB;
        int i;
        for (i = 0; i < tmpA.length; ++i) {
            temB = tmpA[i].split("=");
            cookieMap.put(temB[0], temB[1]);
        }

        tmpA = newCookie.split(";");

        for (i = 0; i < tmpA.length; ++i) {
            temB = tmpA[i].split("=");
            cookieMap.put(temB[0], temB[1]);
        }

        for (String keyString : cookieMap.keySet()) {
            cookieBuffer.append(keyString);
            cookieBuffer.append("=");
            cookieBuffer.append(cookieMap.get(keyString));
            cookieBuffer.append(";");
        }

        return cookieBuffer.toString();
    }

    public static String md5(String s) {
        String ret = null;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = (new BigInteger(1, m.digest())).toString(16);
        } catch (NoSuchAlgorithmException var3) {
            Log.error(var3);
        }

        return ret;
    }

    public static String base64Encode(byte[] src) {
        int off = 0;
        int end = src.length;
        byte[] dst = new byte[4 * ((src.length + 2) / 3)];
        int linemax = -1;
        boolean doPadding = true;
        char[] base64 = toBase64;
        int sp = off;
        int slen = (end - off) / 3 * 3;
        int sl = off + slen;
        if (linemax > 0 && slen > linemax / 4 * 3) {
            slen = linemax / 4 * 3;
        }

        int dp;
        int b0;
        int b1;
        for (dp = 0; sp < sl; sp = b0) {
            b0 = Math.min(sp + slen, sl);
            b1 = sp;

            int bits;
            for (int var13 = dp; b1 < b0; dst[var13++] = (byte) base64[bits & 63]) {
                bits = (src[b1++] & 255) << 16 | (src[b1++] & 255) << 8 | src[b1++] & 255;
                dst[var13++] = (byte) base64[bits >>> 18 & 63];
                dst[var13++] = (byte) base64[bits >>> 12 & 63];
                dst[var13++] = (byte) base64[bits >>> 6 & 63];
            }

            b1 = (b0 - sp) / 3 * 4;
            dp += b1;
        }

        if (sp < end) {
            b0 = src[sp++] & 255;
            dst[dp++] = (byte) base64[b0 >> 2];
            if (sp == end) {
                dst[dp++] = (byte) base64[b0 << 4 & 63];
                if (doPadding) {
                    dst[dp++] = 61;
                    dst[dp++] = 61;
                }
            } else {
                b1 = src[sp++] & 255;
                dst[dp++] = (byte) base64[b0 << 4 & 63 | b1 >> 4];
                dst[dp++] = (byte) base64[b1 << 2 & 63];
                if (doPadding) {
                    dst[dp++] = 61;
                }
            }
        }

        return new String(dst);
    }

    public static byte[] base64Decode(String base64Str) {
        if (base64Str != null && !base64Str.isEmpty()) {
            byte[] src = base64Str.getBytes();
            if (src.length == 0) {
                return src;
            } else {
                int sp = 0;
                int sl = src.length;
                int paddings = 0;
                int len = sl - sp;
                if (src[sl - 1] == 61) {
                    ++paddings;
                    if (src[sl - 2] == 61) {
                        ++paddings;
                    }
                }

                if (paddings == 0 && (len & 3) != 0) {
                    paddings = 4 - (len & 3);
                }

                byte[] dst = new byte[3 * ((len + 3) / 4) - paddings];
                int[] base64 = new int[256];
                Arrays.fill(base64, -1);

                int dp;
                for (dp = 0; dp < toBase64.length; base64[toBase64[dp]] = dp++) {
                }

                base64[61] = -2;
                dp = 0;
                int bits = 0;
                int shiftto = 18;

                while (sp < sl) {
                    int b = src[sp++] & 255;
                    if ((b = base64[b]) < 0 && b == -2) {
                        if (shiftto == 6 && (sp == sl || src[sp++] != 61) || shiftto == 18) {
                            throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit");
                        }
                        break;
                    }

                    bits |= b << shiftto;
                    shiftto -= 6;
                    if (shiftto < 0) {
                        dst[dp++] = (byte) (bits >> 16);
                        dst[dp++] = (byte) (bits >> 8);
                        dst[dp++] = (byte) bits;
                        shiftto = 18;
                        bits = 0;
                    }
                }

                if (shiftto == 6) {
                    dst[dp++] = (byte) (bits >> 16);
                } else if (shiftto == 0) {
                    dst[dp++] = (byte) (bits >> 16);
                    dst[dp++] = (byte) (bits >> 8);
                } else if (shiftto == 12) {
                    throw new IllegalArgumentException("Last unit does not have enough valid bits");
                }

                if (dp != dst.length) {
                    byte[] arrayOfByte = new byte[dp];
                    System.arraycopy(dst, 0, arrayOfByte, 0, Math.min(dst.length, dp));
                    dst = arrayOfByte;
                }

                return dst;
            }
        } else {
            return new byte[0];
        }
    }

    public static String subMiddleStr(String data, String leftStr, String rightStr) {
        int leftIndex = data.indexOf(leftStr);
        leftIndex += leftStr.length();
        int rightIndex = data.indexOf(rightStr);
        return leftIndex != -1 && rightIndex != -1 ? data.substring(leftIndex, rightIndex) : null;
    }

    public static byte[] getResourceAsByteArray(Class cl, String name) {
        InputStream inputStream = cl.getResourceAsStream(name);
        byte[] data = null;

        try {
            data = readInputStream(inputStream);
        } catch (IOException var6) {
            Log.error(var6);
        }

        try {
            inputStream.close();
        } catch (Exception var5) {
            Log.error(var5);
        }

        return data;
    }

    public static byte[] getResourceAsByteArray(Object o, String name) {
        return getResourceAsByteArray(o.getClass(), name);
    }

    public static boolean saveDataViewToCsv(Vector columnVector, Vector dataRows, String saveFile) {
        boolean state = false;

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            int columnNum = columnVector.size();
            byte cob = 44;
            byte newLine = 10;
            int rowNum = dataRows.size();
            new StringBuilder();

            Object valueObject;
            int i;
            for (i = 0; i < columnNum - 1; ++i) {
                valueObject = columnVector.get(i);
                fileOutputStream.write(formatStringByCsv(valueObject.toString()).getBytes());
                fileOutputStream.write(cob);
            }

            valueObject = columnVector.get(columnNum - 1);
            fileOutputStream.write(formatStringByCsv(valueObject.toString()).getBytes());
            fileOutputStream.write(newLine);

            for (i = 0; i < rowNum; ++i) {
                Vector row = (Vector) dataRows.get(i);

                for (int j = 0; j < columnNum - 1; ++j) {
                    valueObject = row.get(j);
                    fileOutputStream.write(formatStringByCsv(String.valueOf(valueObject)).getBytes());
                    fileOutputStream.write(cob);
                }

                valueObject = row.get(columnNum - 1);
                fileOutputStream.write(formatStringByCsv(String.valueOf(valueObject)).getBytes());
                fileOutputStream.write(newLine);
            }

            fileOutputStream.close();
            state = true;
        } catch (Exception var14) {
            var14.printStackTrace();
        }

        return state;
    }

    public static String stringToUnicode(String unicode) {
        char[] chars = unicode.toCharArray();
        StringBuilder builder = new StringBuilder();

        for (char aChar : chars) {
            builder.append("\\u");
            String hx = Integer.toString(aChar, 16);
            if (hx.length() < 4) {
                builder.append("0000".substring(hx.length())).append(hx);
            } else {
                builder.append(hx);
            }
        }

        return builder.toString();
    }

    public static String unicodeToString(String s) {
        String[] split = s.split("\\\\");
        StringBuilder builder = new StringBuilder();

        for (String value : split) {
            if (value.startsWith("u")) {
                builder.append((char) Integer.parseInt(value.substring(1, 5), 16));
                if (value.length() > 5) {
                    builder.append(value.substring(5));
                }
            } else {
                builder.append(value);
            }
        }

        return builder.toString();
    }

    public static boolean sleep(int time) {
        boolean state = false;

        try {
            Thread.sleep(time);
            state = true;
        } catch (InterruptedException var3) {
            Log.error(var3);
        }

        return state;
    }

    public static String toString(java.awt.Font object) {
        return object == null ? "null" : object.toString();
    }

    public static String getLastFileName(String file) {
        String[] fs = formatDir(file).split("/");
        return fs[fs.length - 1];
    }

    private static String formatStringByCsv(String string) {
        string = string.replace("\"", "\"\"");
        return "\"" + string + "\"";
    }
}
