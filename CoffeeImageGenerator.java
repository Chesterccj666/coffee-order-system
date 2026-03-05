import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CoffeeImageGenerator {
    public static void main(String[] args) {
        String[] coffeeNames = {"美式咖啡", "拿铁咖啡", "卡布奇诺咖啡", "摩卡咖啡", "焦糖玛奇朵咖啡", "馥芮白咖啡"};
        
        for (String name : coffeeNames) {
            generateCoffeePlaceholder(name + ".jpg", name);
        }
        
        System.out.println("咖啡占位图片生成完成！");
    }
    
    public static void generateCoffeePlaceholder(String filename, String text) {
        try {
            // 创建一个 BufferedImage 对象
            int width = 400;
            int height = 400;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            // 获取 Graphics2D 对象进行绘图
            Graphics2D g2d = image.createGraphics();
            
            // 设置背景色
            g2d.setColor(Color.decode("#8B4513")); // 咖啡棕色
            g2d.fillRect(0, 0, width, height);
            
            // 设置字体和颜色
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 30));
            
            // 计算文本居中位置
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int x = (width - textWidth) / 2;
            int y = height / 2;
            
            // 绘制文本
            g2d.drawString(text, x, y);
            
            // 绘制一个咖啡杯的简单图形
            g2d.setColor(Color.WHITE);
            g2d.fillOval(width/2 - 60, height/2 - 80, 120, 80); // 杯子主体
            g2d.setColor(Color.BLACK);
            g2d.drawOval(width/2 - 60, height/2 - 80, 120, 80);
            
            // 杯子把手
            g2d.setColor(Color.WHITE);
            g2d.fillArc(width/2 + 30, height/2 - 70, 40, 60, -90, 180);
            g2d.setColor(Color.BLACK);
            g2d.drawArc(width/2 + 30, height/2 - 70, 40, 60, -90, 180);
            
            // 释放资源
            g2d.dispose();
            
            // 输出图片到文件
            File outputFile = new File(filename);
            ImageIO.write(image, "jpg", outputFile);
            
            System.out.println("已生成图片: " + filename);
        } catch (IOException e) {
            System.err.println("生成图片失败: " + filename + ", 错误: " + e.getMessage());
        }
    }
}