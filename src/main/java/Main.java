import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class Main {
    //markdown文件的地址
    private static final String MD_PARENT_PATH = "/Users/xujiajia/Documents/TransformMarkDownImg/src/main/resources/md";
    //图片下载的地址
    private static final String IMG_PARENT_PATH = "/Users/xujiajia/Documents/TransformMarkDownImg/src/main/resources/img";
    //图片想要替换的地址，笔者此处写的是自己github的某个项目的地址
    private static final String IMG_URL_REPLACE = "https://raw.githubusercontent.com/Double2hao/xujiajia_blog/main/img";
    //constants
    private static final String IMG_URL_START_STRING = " src=\"http";
    private static final String IMG_URL_END_STRING = "\"";


    public static void main(String[] args) {
        File mdParent = new File(MD_PARENT_PATH);
        File[] listFiles = mdParent.listFiles();
        if (listFiles == null) {
            return;
        }
        int fileCount = 0;
        for (File file : listFiles) {
            System.out.println("fileName:" + file.getName());
            try {
                downloadImgAndReplacePath(file, fileCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileCount++;
        }
    }

    private static void downloadImgAndReplacePath(File file, int fileCount) {
        if (file == null || !file.exists()) {
            return;
        }
        String content = readStringFromFile(file);
        if (content == null) {
            return;
        }
        int indexToStart = 0;
        for (int imgCount = 0; ; imgCount++) {//一篇文章可能有多个图片，count是用来标识第几张
            //获取imgUrl
            int imgUrlStartIndex = content.indexOf(IMG_URL_START_STRING, indexToStart);
            if (imgUrlStartIndex < 0) {
                break;//找不到img的时候退出循环
            }
            imgUrlStartIndex += IMG_URL_START_STRING.length() - 4;//图片url的起始地址
            //自图片起始位置起开始找引号，那么中间的一段就是图片的url
            String lastContentString = content.substring(imgUrlStartIndex);
            indexToStart = lastContentString.indexOf(IMG_URL_END_STRING) + imgUrlStartIndex;
            String imgUrl = content.substring(imgUrlStartIndex, indexToStart);
            System.out.println("imgUrl:" + imgUrl);
            //下载图片
            String imgName = "" + fileCount + imgCount + ".png";//图片下载后的名字
            OkHttpManager.getInstance().downloadPic(imgUrl, IMG_PARENT_PATH + "/" + imgName);
            //替换图片地址
            String newImgPath = IMG_URL_REPLACE + "/" + imgName;
            content = content.replace(imgUrl, newImgPath);
            //由于url更改，因此index的位置也变了
            indexToStart += newImgPath.length() - imgUrl.length();
        }
        //删除文件后，将替换了图片地址的string重新写成文件
        String filePath = file.getAbsolutePath();
        file.delete();
        writeFile(content, filePath);
    }

    //将文件读成string
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
                stringBuilder.append("\n");//别丢了换行符号
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //将string写成文件
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
