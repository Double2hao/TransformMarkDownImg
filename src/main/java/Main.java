import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class Main {
    private static final String MD_PARENT_PATH = "/Users/xujiajia/Documents/TransformMarkDownImg/src/main/resources/md";
    private static final String IMG_PARENT_PATH = "/Users/xujiajia/Documents/TransformMarkDownImg/src/main/resources/img";
    private static final String IMG_URL_REPLACE = "https://github.com/Double2hao/xujiajia_blog/tree/main/img";
    private static final String IMG_URL_START_STRING = "<img src=\"";
    private static final String IMG_URL_END_STRING = "\"";


    public static void main(String[] args) {
        File mdParent = new File(MD_PARENT_PATH);
        File[] listFiles = mdParent.listFiles();
        if (listFiles == null) {
            return;
        }
        for (File file : listFiles) {
            System.out.println("fileName:" + file.getName());
            try {
                downloadImgAndReplacePath(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void downloadImgAndReplacePath(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        String content = readStringFromFile(file);
        if (content == null) {
            return;
        }
        int indexToStart = 0;
        for (int imgCount = 0; ; imgCount++) {
            //获取imgUrl
            int imgUrlStartIndex = content.indexOf(IMG_URL_START_STRING, indexToStart);
            if (imgUrlStartIndex < 0) {
                break;
            }
            imgUrlStartIndex += IMG_URL_START_STRING.length();
            String lastContentString = content.substring(imgUrlStartIndex);
            indexToStart = lastContentString.indexOf(IMG_URL_END_STRING) + imgUrlStartIndex;
            String imgUrl = content.substring(imgUrlStartIndex, indexToStart);
            System.out.println("imgUrl:" + imgUrl);
            //下载图片
            String imgName = file.getName() + imgCount;
            OkHttpManager.getInstance().downloadPic(imgUrl, IMG_PARENT_PATH + "/" + imgName + ".png");
            //替换图片地址
            content=content.replaceFirst(imgUrl, IMG_URL_REPLACE + "/" + imgName);
        }
        String filePath = file.getAbsolutePath();
        file.delete();
        writeFile(content, filePath);
    }

    private static String readStringFromFile(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeFile(String content, String filePath) {
        if (content == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
