package util.http;

import core.shell.ShellEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private final ShellEntity shellEntity;
    private byte[] result;
    private Map<String, List<String>> headerMap;
    private String message;

    public HttpResponse(HttpURLConnection http, ShellEntity shellEntity) throws IOException {
        this.shellEntity = shellEntity;
        this.handleHeader(http.getHeaderFields());
        this.ReadAllData(this.getInputStream(http));
    }

    public byte[] getResult() {
        return this.result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public Map<String, List<String>> getHeaderMap() {
        return this.headerMap;
    }

    public void setHeaderMap(Map<String, List<String>> headerMap) {
        this.headerMap = headerMap;
    }

    protected void handleHeader(Map<String, List<String>> map) {
        this.headerMap = map;
        this.message = (map.get(null)).get(0);
        List<String> cookieList = map.getOrDefault("Set-Cookie", new ArrayList<>());
        StringBuilder buffer = new StringBuilder();

        for (String o : cookieList) {
            String[] _cookie = o.split(";");
            buffer.append(_cookie[0]);
            buffer.append(";");
        }

        if (buffer.length() > 1) {
            String oldCookie = this.shellEntity.getHeaders().getOrDefault("Cookie", "");
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
            if (this.headerMap.get("Content-Length") != null && (this.headerMap.get("Content-Length")).size() > 0) {
                int maxLen = Integer.parseInt((this.headerMap.get("Content-Length")).get(0));
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
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int readOneNum;
            while ((readOneNum = inputStream.read(temp)) != -1) {
                bos.write(temp, 0, readOneNum);
            }

            return bos.toByteArray();
        }
    }

    protected byte[] ReadUnknownNumData(InputStream inputStream) throws IOException {
        byte[] temp = new byte[5120];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        int readOneNum;
        while ((readOneNum = inputStream.read(temp)) != -1) {
            bos.write(temp, 0, readOneNum);
        }

        return bos.toByteArray();
    }
}
