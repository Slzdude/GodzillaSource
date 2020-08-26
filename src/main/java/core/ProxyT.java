package core;

import core.shell.ShellEntity;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

public class ProxyT {
    private static final String[] PTOXY_TYPES = new String[]{"NO_PROXY", "HTTP", "SOCKS"};

    private ProxyT() {
    }

    public static Proxy getProxy(ShellEntity context) {
        try {
            String type = context.getProxyType();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(context.getProxyHost(), context.getProxyPort());
            if ("SOCKS".equals(type.toUpperCase())) {
                return new Proxy(Type.SOCKS, inetSocketAddress);
            } else {
                return "HTTP".equals(context.getProxyType()) ? new Proxy(Type.HTTP, inetSocketAddress) : Proxy.NO_PROXY;
            }
        } catch (Exception var3) {
            return Proxy.NO_PROXY;
        }
    }

    public static String[] getAllProxyType() {
        return PTOXY_TYPES;
    }
}
