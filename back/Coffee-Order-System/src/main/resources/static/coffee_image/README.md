# 咖啡图片资源说明

本目录用于存放咖啡相关的图片资源。考虑到国际化和系统兼容性，推荐使用英文命名。

## 建议的图片文件列表（英文命名）

1. Americano.jpg (美式咖啡)
2. Latte.jpg (拿铁咖啡)
3. Cappuccino.jpg (卡布奇诺咖啡)
4. Mocha.jpg (摩卡咖啡)
5. Caramel_Macchiato.jpg (焦糖玛奇朵咖啡)
6. Flat_White.jpg (馥芮白咖啡)

## 为什么使用英文命名？

- **兼容性更好**：避免中文路径在不同操作系统和Web服务器上的编码问题
- **标准化**：符合Web开发的最佳实践
- **国际化**：便于后续国际化扩展
- **技术限制**：某些Web服务器或CDN可能对非ASCII字符路径处理不当

## 图片获取建议

可以从以下免费图片网站下载合适的咖啡图片：

1. Unsplash (https://unsplash.com/s/photos/coffee)
2. Pexels (https://www.pexels.com/search/coffee/)
3. Pixabay (https://pixabay.com/images/search/coffee/)

## 图片要求

- 格式：JPG
- 分辨率：建议 400x400 像素以上
- 文件大小：控制在 200KB 以内
- 质量：清晰，光线良好，突出咖啡特色

## 使用说明

在数据库中设置咖啡的 coffee_image 字段时，使用相对路径：
"/static/coffee_image/Americano.jpg"