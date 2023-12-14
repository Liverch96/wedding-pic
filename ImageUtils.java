package com.zhangyu.mybatis.demo;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ImageUtils {


    public static void main(String[] args) {
        rename(new File("C:\\Users\\zhangyu\\Pictures\\张雨&刘岩\\底片\\外景\\古风\\origin"));

        thumb(new File("C:\\Users\\zhangyu\\Pictures\\张雨&刘岩\\底片\\外景\\古风\\origin"));
    }

    public static void thumb(File dir) {
        try {
            List<File> files = Arrays.stream(dir.listFiles()).sorted(Comparator.comparing(f -> Integer.parseInt(f.getName().split("-")[1].split("\\.")[0]))).collect(Collectors.toList());
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file.isDirectory()) {
                    return;
                }
                String fileName = dir + "\\..\\thumb\\thumb-" + (i+1) + "." + file.getName().split("\\.")[1];
                ImageUtils.saveImage(file,new File(fileName), 800);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void rename(File dir) {
        try {
            List<File> files = Arrays.stream(dir.listFiles()).sorted(Comparator.comparing(File::length).reversed()).collect(Collectors.toList());
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file.isDirectory()) {
                    continue;
                }
                file.renameTo(new File(dir + "\\origin-" + (i+1) + ".jpg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成文件
     * @param image 源文件流
     * @param newWidth 缩小宽度
     * @param newHeigh 缩小高度
     * @return
     */
    public static BufferedImage resize(BufferedImage image, int newWidth,int newHeigh) {
        int type = image.getType();
        BufferedImage newImage = null;
        double sx = (double) newWidth / image.getWidth();
        double sy = (double) newHeigh / image.getHeight();
        if (type == BufferedImage.TYPE_CUSTOM) {
            ColorModel cm = image.getColorModel();
            WritableRaster raster = cm.createCompatibleWritableRaster(newWidth,newHeigh);
            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
            newImage = new BufferedImage(cm, raster, alphaPremultiplied, null);
        } else {
            newImage = new BufferedImage(newWidth, newHeigh, type);
            Graphics2D g = newImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            g.drawRenderedImage(image, AffineTransform.getScaleInstance(sx, sy));
            g.dispose();
        }

        return newImage;
    }

    /**
     * 保存缩略图
     * @param fromFile 源文件
     * @param saveFile 生成新文件
     * @param size 限制尺寸
     * @return
     * @throws Exception
     */
    public static String[] saveImage(File fromFile, File saveFile,int size) throws Exception {
        String[] rets = new String[2];
        BufferedImage srcImage;
        String imgType = "JPEG";
        double maxLeng = 0d;
        double ratio = 0;
        String ext = fromFile.getName().split("\\.")[1].toLowerCase();
        switch (ext) {
            case "png":
                imgType = "PNG";
                break;
            case "jpg":
                imgType = "JPEG";
                break;
            case "bmp":
                imgType = "BMP";
                break;
            case "gif":
                imgType = "GIF";
                break;
            default:
                rets[0] = "0";
                rets[1] = "不支持此图片格式！";
                return rets;
        }
        srcImage = ImageIO.read(fromFile);
        double width = (double)srcImage.getWidth();
        double height = (double)srcImage.getHeight();
        if (width > 0 || height > 0) {
//计算比例
            if (width > height) {
                maxLeng = width;
                ratio = maxLeng/size;
            } else {
                maxLeng = height;
                ratio = maxLeng/size;
            }
            if (maxLeng > size) {
                int newWidth = (int) (width/ratio);
                int newHeight = (int) (height/ratio);
                srcImage = resize(srcImage, newWidth, newHeight);
            }
        }
        boolean isSuccess = ImageIO.write(srcImage, imgType, saveFile);
        if (isSuccess) {
            rets[0]="1";
            rets[1]="上传成功！";
            return rets;
        }else {
            rets[0]="0";
            rets[1]="上传发生错误！";
            return rets;
        }
    }

}