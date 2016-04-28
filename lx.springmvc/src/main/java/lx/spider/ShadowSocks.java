package lx.spider;

import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.HttpClientUtils;

import com.google.api.client.http.HttpResponse;

public class ShadowSocks {

    static Logger LOGGER = LoggerFactory.getLogger(ShadowSocks.class);

    public static List<String> SITES = new ArrayList<String>();

    static {
        // SITES.add("http://www.pilivpn.com/");
        SITES.add("https://ss.kalone.net");
    }

    public static void singIn() {
        // System.setProperty("http.proxyHost", "localhost");
        // System.setProperty("http.proxyPort", "8888");
        for (String site : SITES) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("email", "540818255@qq.com");
            params.put("passwd", "123456789");
            params.put("remember_me", "week");
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Origin", site);
            headers.put("Host", getDomainName(site));
            headers.put("Referer", site + "/user/login.php");
            StringBuilder sb = new StringBuilder();
            try {
                HttpResponse post = HttpClientUtils.post(site + "/auth/login", params, headers, "");

                @SuppressWarnings("unchecked")
                List<String> cookies = (List<String>) post.getHeaders().get("set-cookie");
                List<HttpCookie> list = new ArrayList<HttpCookie>();
                for (String cookie : cookies) {
                    List<HttpCookie> parse = HttpCookie.parse(cookie.toString());
                    list.addAll(parse);
                }
                for (HttpCookie one : list) {
                    sb.append(one.getName()).append("=").append(one.getValue()).append(";");
                }
                String html = HttpClientUtils.getHtml(site + "/user/_checkin.php", new HashMap<String, String>(), sb);
                LOGGER.info("sign . result: {}", unicodeToChinese(html));
            } catch (Exception e) {
                LOGGER.error("sth wrong. site: {}", site, e);
            }
        }
    }

    public static void main(String[] args) {
        singIn();
    }

    public static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (Exception e) {
            LOGGER.error("get domain error. url: {}", url);
        }
        return "";
    }

    public static String unicodeToChinese(String str) {
        if (str.indexOf("\\u") == -1 || str == null
                || "".equals(str.trim())) {/* 若不是unicode，则直接返回 */
            return str.replaceAll("\\\\ ", " ");// 删掉英文中的\,such as "default\
                                                // value1"
            /* 主要是针对 zk 中的国际化问题 */
        }
        StringBuffer sb = new StringBuffer();
        if (!str.startsWith("\\u")) {/* 若开头不是unicode，如“abc\u4e2d\u56fd” */
            int index = str.indexOf("\\u");
            sb.append(str.substring(0, index));
            str = str.substring(index);
        }
        if (str.endsWith(":")) /* 如“\u4e2d\u56fd：” */ {
            str = str.substring(0, str.length() - 1);
        }
        String[] chs = str.trim().split("\\\\u");

        for (int i = 0; i < chs.length; i++) {
            String ch = chs[i].trim();
            if (ch != null && !"".equals(ch)) {

                sb.append((char) Integer.parseInt(ch.substring(0, 4), 16));
                if (ch.length() > 4) {
                    sb.append(ch.substring(4));
                }
            }
        }
        return sb.toString();
    }
}
