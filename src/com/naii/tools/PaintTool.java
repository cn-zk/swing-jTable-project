package com.naii.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class PaintTool {
	/**
	 * 获得一个随机颜色
	 * @return Color
	 */
	public static Color randomColor(){
		return randomColor(0, 255, false);
	}
	/**
	 * 获得一个随机透明度的随机颜色
	 * @return Color
	 */
	public static Color randomAlphaColor(){
		return randomColor(0, 255, true);
	}
	/**
	 * 随机一个颜色(0 ~ 255)
	 * @param min 最小值
	 * @param max 最大值
	 * @param alpha 启用透明度
	 * @return Color
	 */
	public static Color randomColor(int min, int max, boolean alpha){
		if(min < 0) min = 0;
		if(max > 255) max = 255;
		return new Color(
				Assist.random(min, max),
				Assist.random(min, max),
				Assist.random(min, max),
				alpha ? Assist.random(min, max): 255
			);
	}
	/**
	 * 取得图像上指定位置像素的 RGB 颜色分量。
	 * 
	 * @param image	源图像。
	 * @param x		图像上指定像素位置的 x 坐标。
	 * @param y		图像上指定像素位置的 y 坐标。
	 * @return Color 返回包含 RGB 颜色分量值的数组。元素 index 由小到大分别对应 r，g，b。
	 * @throws IOException
	 */
	public static Color getPointRGB(InputStream imageIn, int x, int y)
			throws IOException {
		return getPointRGB(ImageIO.read(imageIn), x, y);
	}
	/**
	 * 取得图像上指定位置像素的 RGB 颜色分量。
	 * 
	 * @param image	源图像。
	 * @param x		图像上指定像素位置的 x 坐标。
	 * @param y		图像上指定像素位置的 y 坐标。
	 * @return Color 返回包含 RGB 颜色分量值的数组。元素 index 由小到大分别对应 r，g，b。
	 * @throws IOException
	 */
	public static Color getPointRGB(File image, int x, int y) throws IOException {
		return getPointRGB(ImageIO.read(image), x, y);
	}
	/**
	 * 取得图像上指定位置像素的 RGB 颜色分量。
	 * 
	 * @param image	源图像。
	 * @param x		图像上指定像素位置的 x 坐标。
	 * @param y		图像上指定像素位置的 y 坐标。
	 * 
	 * @return 返回包含 RGB 颜色分量值的数组。元素 index 由小到大分别对应 r，g，b。
	 */
	public static Color getPointRGB(BufferedImage image, int x, int y) {
		Color color = null;
		if (image != null && x < image.getWidth() && y < image.getHeight()) {
			int pixel = image.getRGB(x, y);
			int r = (pixel & 0xff0000) >> 16, g = (pixel & 0xff00) >> 8, b = (pixel & 0xff);
			color = new Color(r, g, b);
		}
		return color;
	}
	
	
	/** 束缚宽高  */
	public static final int ASTRICT_WIDHT_AND_HEIGHT = 0;
	/** 束缚宽  */
	public static final int ASTRICT_WIDHT = 1;
	/** 束缚高  */
	public static final int ASTRICT_HEIGHT = 2;
	/**
	 * 调整大小
	 * @param imgW
	 * @param imgH
	 * @param viewW
	 * @param viewH
	 * @param astrict
	 * @return
	 */
	public static Dimension adjustImageSize(int imgW, int imgH, int viewW,int viewH, int astrict){
		Dimension d = new Dimension(imgW, imgH);
		if(ASTRICT_WIDHT == astrict || ASTRICT_WIDHT_AND_HEIGHT == astrict){
			if(d.width > viewW){
				float f = (float)d.width / (float)viewW;
				d.width = (int)((float)d.width / f);
				d.height = (int) ((float)d.height / f);
			}
		}
		if(ASTRICT_HEIGHT == astrict || ASTRICT_WIDHT_AND_HEIGHT == astrict){
			if(d.height > viewH){
				float f = (float)d.height / (float)viewH;
				d.width = (int)((float)d.width / f);
				d.height = (int) ((float)d.height / f);
			}
		}
		return d;
	}
	/**
	 * 等分切割图像为图像数组
	 * @param imagebuf 		源图像
	 * @param isHorizontal	是否横切
	 * @param split			分割数量(split > 0)
	 * @return BufferedImage[split]
	 */
	public static BufferedImage[] splitImage(BufferedImage splitImage, boolean isHorizontal, int split){
		BufferedImage images[] = new BufferedImage[split]; 
		
		if(split > 0 && splitImage != null){
			if(split > 1){
				int x=0, y=0, w= splitImage.getWidth(), h= splitImage.getHeight();
				if(isHorizontal){
					w /= split;
				}else{
					h /= split;
				}
				for(int i = 0 ; i < split ; i ++){
					BufferedImage image = null;
					if(isHorizontal){
						x = i * w;
						image = splitImage.getSubimage(x, y, w, h);
					}else{
						y = i * h;
						image = splitImage.getSubimage(x, y, w, h);
					}
					images[i] = image;
				}
			}else{
				images[0] = splitImage; 
			}
		}
		return images;
	}
	/**
	 * 修改颜色透明度
	 * @param c
	 * @param alpha
	 * @return
	 */
	public static Color alphaColor(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}
	/**
	 * 反色
	 * @param c
	 * @return
	 */
	public static Color inverseColor(Color c){
		return new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue(), c.getAlpha());
	}

	/**
	 * 获得桌面除去任务栏的显示大小
	 * @return Insets
	 */
	public static Rectangle getScreen(Window window){
		Insets ins = java.awt.Toolkit.getDefaultToolkit().getScreenInsets(window.getGraphicsConfiguration());
		Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		return new Rectangle(ins.left, ins.top, d.width - ins.left - ins.right, d.height - ins.top - ins.bottom);
	}
	
	public static BufferedImage rotateImage(final BufferedImage bufferedimage,
            final int degree) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type))
                .createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }
}