package util.http;

import util.functions;

import java.util.HashMap;

public class ReqParameter {
    HashMap<String, Object> hashMap = new HashMap<>();

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
        StringBuffer buffer;
        buffer = new StringBuffer();
        for (String key : this.hashMap.keySet()) {
            buffer.append(key);
            buffer.append("=");
            Object valueObject = this.hashMap.get(key);
            if (valueObject.getClass().isAssignableFrom(byte[].class)) {
                buffer.append(functions.base64Encode((byte[]) valueObject));
            } else {
                buffer.append(functions.base64Encode(((String) valueObject).getBytes()));
            }
            buffer.append("&");
        }

        return buffer.delete(buffer.length() - 1, buffer.length()).toString();
    }
}
