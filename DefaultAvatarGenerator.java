import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DefaultAvatarGenerator {
    public static void main(String[] args) {
        generateDefaultAvatar();
    }
    
    public static void generateDefaultAvatar() {
        try {
            // 创建一个 BufferedImage 对象
            int width = 200;
            int height = 200;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            // 获取 Graphics2D 对象进行绘图
            Graphics2D g2d = image.createGraphics();
            
            // 设置背景色
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, width, height);
            
            // 设置头像边框
            g2d.setColor(Color.GRAY);
            g2d.fillOval(10, 10, width-20, height-20);
            
            // 设置头像内部颜色
            g2d.setColor(Color.WHITE);
            g2d.fillOval(15, 15, width-30, height-30);
            
            // 设置字体和颜色
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("微软雅黑", Font.BOLD, 40));
            
            // 绘制一个简单的头像图标（一个人形轮廓）
            g2d.fillOval(width/2 - 25, height/2 - 40, 50, 50); // 头部
            g2d.fillRect(width/2 - 15, height/2 + 10, 30, 40); // 身体
            
            // 释放资源
            g2d.dispose();
            
            // 确保目录存在
            String uploadDir = "g:\\CoffeeOrderSystem\\back\\Coffee-Order-System\\src\\main\\resources\\static\\head_image\\";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 输出图片到文件
            File outputFile = new File(uploadDir + "default-avatar.jpg");
            ImageIO.write(image, "jpg", outputFile);
            
            System.out.println("默认头像已生成: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("生成默认头像失败: " + e.getMessage());
        }
    }
}