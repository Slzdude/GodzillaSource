package core;

import core.ui.component.dialog.ImageShowDialog;
import util.Log;
import util.functions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ApplicationConfig {
    public static final String GIT = "https://github.com/BeichenDream/Godzilla";
    private static final String GITEE_CONFIG_URL = "https://gitee.com/beichendram/Godzilla/raw/master/application.config";
    private static final String GIT_CONFIG_URL = "https://raw.githubusercontent.com/BeichenDream/Godzilla/master/application.config";
    private static final HashMap<String, String> headers = new HashMap<String, String>();

    static {
        headers.put("Accept", "*/*");
        headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)");
    }

    public ApplicationConfig() {
    }

    public static void invoke() {
        HashMap<String, String> configMap = null;

        try {
            configMap = getAppConfig("https://gitee.com/beichendram/Godzilla/raw/master/application.config");
        } catch (Exception var11) {
            try {
                configMap = getAppConfig("https://raw.githubusercontent.com/BeichenDream/Godzilla/master/application.config");
            } catch (Exception var10) {
                Log.error("Network connection failure");
            }
        }

        if (configMap != null && configMap.size() > 0) {
            String version = configMap.get("currentVersion");
            boolean isShowGroup = Boolean.valueOf(configMap.get("isShowGroup"));
            String wxGroupImageUrl = configMap.get("wxGroupImageUrl");
            String showGroupTitle = configMap.get("showGroupTitle");
            String gitUrl = configMap.get("gitUrl");
            boolean isShowAppTip = Boolean.valueOf(configMap.get("isShowAppTip"));
            String appTip = configMap.get("appTip");
            if (version != null && wxGroupImageUrl != null && appTip != null && gitUrl != null) {
                if (functions.stringToint(version.replace(".", "")) > functions.stringToint("1.00".replace(".", ""))) {
                    JOptionPane.showMessageDialog(null, String.format("新版本已经发布\n当前版本:%s\n最新版本:%s", "1.00", version), "message", 2);
                    functions.openBrowseUrl(gitUrl);
                }

                if (isShowAppTip) {
                    JOptionPane.showMessageDialog(null, appTip, "message", 1);
                }

                if (isShowGroup) {
                    try {
                        ImageIcon imageIcon = new ImageIcon(ImageIO.read(new ByteArrayInputStream(functions.httpReqest(wxGroupImageUrl, "GET", headers, null))));
                        ImageShowDialog.showImageDiaolog(imageIcon, showGroupTitle);
                    } catch (IOException var9) {
                        Log.error(var9);
                        Log.error("showGroup fail!");
                    }
                }
            }
        }

    }

    private static HashMap<String, String> getAppConfig(String configUrl) throws Exception {
        byte[] result = functions.httpReqest(configUrl, "GET", headers, null);
        if (result == null) {
            throw new Exception("readApplication Fail!");
        } else {
            String configString;
            configString = new String(result, StandardCharsets.UTF_8);

            HashMap<String, String> hashMap = new HashMap<>();
            String[] lines = configString.split("\n");
            String[] var8 = lines;
            int var7 = lines.length;

            for (int var6 = 0; var6 < var7; ++var6) {
                String line = var8[var6];
                int index = line.indexOf(58);
                if (index != -1) {
                    hashMap.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
                }
            }

            return hashMap;
        }
    }
}
