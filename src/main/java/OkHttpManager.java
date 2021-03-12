import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OkHttpManager {

    private OkHttpClient okHttpClient;

    private OkHttpManager() {
        okHttpClient = new OkHttpClient();
    }

    private static final class Host {
        private static final OkHttpManager instance = new OkHttpManager();
    }

    public static OkHttpManager getInstance(){
        return Host.instance;
    }

    public boolean downloadPic(String picUrl, String localFileName) {
        final File file = new File(localFileName);
        if (file.exists()) {
            file.delete();
        } else {
            try {

                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            Request request = new Request.Builder().url(picUrl).build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            byte[] buf = new byte[2048];
            int len = 0;
            is = response.body().byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
