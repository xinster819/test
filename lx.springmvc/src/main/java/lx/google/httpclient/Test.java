package lx.google.httpclient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;

public class Test {

    static List<Character> num_word = new ArrayList<Character>();
    static {
        for (char i = '0'; i <= '9'; i++) {
            num_word.add(i);
        }
        for (char i = 'A'; i <= 'Z'; i++) {
            num_word.add(i);
        }
    }

    static ExecutorService executor = Executors.newFixedThreadPool(100);

    static NetHttpTransport nht = new NetHttpTransport();

    public static void main(String[] args) throws IOException {
        HttpRequestFactory hrf = nht.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        });
        String _url = "http://www.whxxn.com/uploads/allimg/150509/2-150509%s.jpg";
        for (Character a : num_word) {
            for (Character b : num_word) {
                for (Character c : num_word) {
                    String i = new StringBuilder().append(a).append(b).append(c).toString();
                    String format = String.format(_url, i);
                    GenericUrl url = new GenericUrl(format);
                    HttpRequest hr = hrf.buildGetRequest(url);
                    try {
                        HttpResponse resp = hr.execute();
                        if (resp.getStatusCode() != 404) {
                            System.out.println(format);
                            String name = i + ".jpg";
                            executor.execute(new Download(format, name));
                        }
                    } catch (HttpResponseException e) {
                        if (e.getStatusCode() != 404) {
                            System.out.println(e.getStatusCode());
                        }
                    }
                }
            }
        }
    }

    public static class Download implements Runnable {

        String img;
        String name;

        public Download(String img, String name) {
            this.img = img;
            this.name = name;
        }

        @Override
        public void run() {
            String src = "E:\\spider\\" + name;
            URL url;
            try {
                url = new URL(img);
                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(src);

                byte[] b = new byte[2048];
                int length;

                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }

                is.close();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
