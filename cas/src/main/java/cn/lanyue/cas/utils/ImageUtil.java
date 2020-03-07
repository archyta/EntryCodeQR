package cn.lanyue.cas.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import sun.awt.SunHints;
import sun.net.www.content.image.png;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * @author 
 * @Description 海报合成工具类
 * @Date 2020/2/19 1:03
 */
public class ImageUtil {

    /**
     * 颜色转换
     * */
    public static Color fromStrToARGB(String str) {
        String str1 = str.substring(1, 3);
        String str2 = str.substring(3, 5);
        String str3 = str.substring(5, 7);
        int red = Integer.parseInt(str1, 16);
        int green = Integer.parseInt(str2, 16);
        int blue = Integer.parseInt(str3, 16);
        if (str.length()==9){
            String str4 = str.substring(7, 9);
            int alpha = Integer.parseInt(str4, 16);
            return new Color(red, green, blue, alpha);
        }else {
            return new Color(red, green, blue);
        }
    }

    /**
     * 创建Graphics2D
     *
     * 抗锯齿
     * @param bgBufImage
     *            底图
     * @return Graphics2D
     */
    public static Graphics2D createG2D(BufferedImage bgBufImage){
        Graphics2D bgBufImageGraphics = bgBufImage.createGraphics();
        //bgBufImageGraphics.setRenderingHint(SunHints.KEY_ANTIALIASING, SunHints.VALUE_ANTIALIAS_OFF);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIASING, SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_STROKE_CONTROL, SunHints.VALUE_STROKE_DEFAULT);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_TEXT_ANTIALIAS_LCD_CONTRAST, 140);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_FRACTIONALMETRICS, SunHints.VALUE_FRACTIONALMETRICS_OFF);
        bgBufImageGraphics.setRenderingHint(SunHints.KEY_RENDERING, SunHints.VALUE_RENDER_DEFAULT);
        bgBufImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return bgBufImageGraphics;
    }

    /**
     * 绘制海报底色（默认微软雅黑/PLAIN/32）
     *
     * @param bgBufImage
     *            底图
     * @param color
     *            颜色
     */
    public static void setBackGroup(BufferedImage bgBufImage,String color){
        Graphics2D bgBufImageGraphics = createG2D(bgBufImage);
        bgBufImageGraphics.setBackground(fromStrToARGB(color));//填充整个屏幕
        bgBufImageGraphics.clearRect(0,0,bgBufImage.getWidth(),bgBufImage.getHeight());
        bgBufImageGraphics.dispose();
    }


    /**
     * 绘制海报文字（默认微软雅黑/PLAIN/32）
     *
     * @param basebBI
     *            底图
     * @param text
     *            文本
     * @param x
     *            坐标 x
     * @param y
     *            坐标 y
     * @param color
     *            颜色
     * @param font
     *            字体
     */
    public static void drawText(BufferedImage basebBI, String text, int x, int y, String color, Font font, boolean xcenter) {
        // 抗锯齿
        if (font == null) {
            font = new Font("微软雅黑", Font.PLAIN, 32);
        }
        Graphics2D g2D = createG2D(basebBI);
        g2D.setFont(font);
        g2D.setPaint(new Color(0, 0, 0, 64));
        //文字居中
        if (xcenter) {
            x = textXCenter(g2D,font,text,basebBI.getWidth());
        }
        // 先绘制一遍底色
        g2D.drawString(text, x, y);
        g2D.setPaint(fromStrToARGB(color));

        // 再绘制一遍文字
        // 由于部分情况会出现文字模糊的情况，保险起见才出此对策。
        g2D.drawString(text, x, y);
        g2D.dispose();
    }
    /**

     * 绘制海报文字(换行)
     *
     * @param basebBI
     *            底图
     * @param text
     *            文本
     * @param x
     *            位置：x
     * @param y
     *            位置：y
     * @param lineHeight
     *            单行行高
     * @param lineWidth
     *            单行行宽
     * @param color
     *            文本颜色
     * @param font
     *            文本字体
     * @param limitLineNum
     *            限制行数
     * @param backgroundWidth
     *            底背位置（多行文字绘制时，出现为单行时居中的区域宽度。）
     */
    public static void drawTextNewLine(BufferedImage basebBI, String text, int x, int y, int lineHeight,
                                        int lineWidth, String color, Font font, int limitLineNum, int backgroundWidth) {
        Graphics2D graphics = createG2D(basebBI);
        if (font == null){
            font = new Font("微软雅黑", Font.PLAIN, 32);
        }
        graphics.setFont(font);
        graphics.setPaint(fromStrToARGB(color));

        //计算字符串所占屏幕长度
        FontRenderContext frc = graphics.getFontRenderContext();
        graphics.getFontRenderContext();
        //记录行数
        List<String> lineList = Lists.newArrayList();
        if (backgroundWidth > 0) {
            x = (backgroundWidth - lineWidth) / 2;
        }

        int stringIndex =0;
        StringBuilder tmpLineString  = new StringBuilder();
        while (stringIndex < text.length()) {
            String tmp_char =text.substring(stringIndex,stringIndex+1);
            Rectangle2D tempStringBounds = font.getStringBounds(tmpLineString+tmp_char, frc);
            double width = tempStringBounds.getWidth();
            if (width>lineWidth ){
                lineList.add(tmpLineString.toString());
                tmpLineString = new StringBuilder();
            }
            tmpLineString = tmpLineString.append(tmp_char);
            stringIndex++;
            if (stringIndex == text.length()) {
                lineList.add(tmpLineString.toString());
            }
        }
        // Color.BLACK 。字体颜色
        graphics.setPaint(fromStrToARGB(color));
        if (lineHeight == 0) {
            lineHeight = 35;
        }

        for (int i = 0; i < lineList.size(); i++) {
            String lineStr = lineList.get(i);
            double width = font.getStringBounds(lineStr, frc).getWidth();
            double diffWidth = font.getStringBounds("...", frc).getWidth();
            if (i > limitLineNum -1) {
                break;
            }else if (i  == limitLineNum -1 && lineWidth -width<diffWidth){
                lineStr = lineStr.substring(0, lineStr.length() - 2) + "...";
            }
            graphics.drawString(lineStr, x, y + (i + 1) * lineHeight);
            graphics.drawString(lineStr, x, y + (i + 1) * lineHeight);
        }
        graphics.dispose();
    }

    /**

     * 绘制海报图片
     *
     * @param basebBI
     *            底图
     * @param path
     *            图片地址
     * @param x
     *            位置：x
     * @param y
     *            位置：y
     * @param width
     *            图片宽度
     * @param height
     *            图片高度
     */
    public static void drawImage(BufferedImage basebBI,String path,int x, int y,int width,int height, boolean xCenter) throws Exception {
        BufferedImage qrCodeImage = ImageIO.read(new File(path));
        drawImage(basebBI,qrCodeImage,x,y,width,height,xCenter);
    }

    /**

     * 绘制海报图片
     *
     * @param basebBI
     *            底图
     * @param imageBI
     *            图片 BufferedImage
     * @param x
     *            位置：x
     * @param y
     *            位置：y
     * @param width
     *            图片宽度
     * @param height
     *            图片高度
     */
    public static void drawImage(BufferedImage basebBI,BufferedImage imageBI,int x, int y,int width,int height , boolean xCenter)  {
        Graphics2D g2D = createG2D(basebBI);
        if (width == -1){
            width = imageBI.getWidth();
        }

        if (height == -1){
            height = imageBI.getHeight();
        }

        if (xCenter) {
            x = (basebBI.getWidth() - width) / 2;
        }

        g2D.drawImage(imageBI, x, y, width, height, null);
        g2D.dispose();
    }

    /**
     * 创建带圆角的图片
     * @param path
     *            图片地址
     * @param ratioWith
     *            水平直径 -1 表示圆型
     * @param ratioHeith
     *            垂直直径 -1 表示圆型
     */
    public static BufferedImage getRoundImage(String path,int ratioWith,int ratioHeith, int size) throws Exception{
        BufferedImage image = ImageIO.read(new File(path));
        BufferedImage bufferedImage = new BufferedImage(image.getWidth() + size, image.getHeight() + size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D image2D = createG2D(bufferedImage);
        if (ratioWith <= -1){
            ratioWith = image.getWidth();
        }
        if (ratioHeith<=-1){
            ratioHeith = image.getHeight();
        }

        image2D.fillRoundRect(0, 0, image.getWidth() + size, image.getHeight() + size, ratioWith, ratioHeith);
        image2D.setComposite(AlphaComposite.SrcIn);
        image2D.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        image2D.dispose();
        return bufferedImage;
    }

    /**
     * 输出图片
     * @param bgBufImage
     *            底图
     * @param path
     *            图片输出地址
     */
    public static void saveImage(BufferedImage bgBufImage,String savePath, String fileName) throws Exception {
        Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter imageWriter = iterator.next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(1);
        Path path = Paths.get(savePath);
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }
        imageWriter.setOutput( ImageIO.createImageOutputStream(new FileOutputStream(new File(StringUtils.join(savePath,fileName)))));
        IIOImage iio_image = new IIOImage(bgBufImage, null, null);
        imageWriter.write(null, iio_image, null);
        imageWriter.dispose();
    }

    public static void drawImage(String qrcodePath, String savePosterPath) throws Exception {
        //头像
        String headUrl = "D:/lanyue/communityaccessserver/cas/qrcode/1estate.png";

        BufferedImage bgBufImage = new BufferedImage(2479, 3508, BufferedImage.TYPE_INT_RGB);
        setBackGroup(bgBufImage, "#3A6EFF");
        drawText(bgBufImage,"成都市",100,150,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,90), false);
        drawText(bgBufImage,"疫情防控",0,600,"#FFFFFF",new Font("微软雅黑",Font.PLAIN ,300), true);
        drawImage(bgBufImage,getRoundImage(headUrl,-1,-1,18),160,800,1280,1280, true);
        drawText(bgBufImage,"龙都花园一期",0,2300,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,130), true);

        ImageUtil.drawTextNewLine(bgBufImage,
                "为做好疫情防控，避免交叉感染。请所有小区出入人员，主动登记相关信息，领取“临时通行证”。老人和儿童，请监护人员给予辅助登记。若有不明之处，请联系物业。",
                0,2500,150,2000,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,90),10,bgBufImage.getWidth());
//        drawTextNewLine(bgBufImage,
//                "1.保安扫此码注册保安端账号。                          " +
//                        "2.成功注册后，用此工具扫住户通行证，并查验身份，确定是放行还是劝返。",
//                0,2600,150,2000,"#FFFFFF",new Font("微软雅黑",Font.BOLD ,90),10,bgBufImage.getWidth());

        saveImage(bgBufImage,"","E:\\demo.jpeg");
    }

    public static int textXCenter(Graphics2D graphics2D, Font font, String text, int width) {
        // 计算文字长度，计算居中的x点坐标
        FontMetrics fm = graphics2D.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);
        return (width - textWidth) / 2;
    }

    public static void main(String[] args)  {
        try {
            drawImage("","");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
