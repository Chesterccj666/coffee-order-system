package com.lihao.CoffeeOrderSystem.utils;

/**
 * 中文转拼音工具类（简化版）
 * 在实际项目中，建议使用专业的拼音转换库如pinyin4j
 */
public class PinYinUtils {
    /**
     * 将中文转换为拼音（简化处理）
     * @param chinese 中文字符串
     * @return 转换后的字符串（仅包含字母、数字、下划线和横线）
     */
    public static String convertChineseToPinyin(String chinese) {
        if (chinese == null || chinese.isEmpty()) {
            return "";
        }
        
        // 使用简单的规则处理常见的中文字符
        StringBuilder result = new StringBuilder();
        
        for (char c : chinese.toCharArray()) {
            // 如果是中文字符，替换为user
            if (isChineseCharacter(c)) {
                result.append("user");
                break; // 只要包含中文就用user
            } else {
                // 如果是非字母数字下划线横线字符，替换为下划线
                if (Character.isLetterOrDigit(c) || c == '_' || c == '-') {
                    result.append(c);
                } else {
                    result.append('_');
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 判断字符是否为中文
     * @param c 字符
     * @return 是否为中文
     */
    public static boolean isChineseCharacter(char c) {
        return c >= 0x4e00 && c <= 0x9fff;
    }
}