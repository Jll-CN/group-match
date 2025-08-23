package com.jiale.teamtogether.once.importuser;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 星球表格用户信息 - 打上注解匹配 excel表中的列
 * 将Java对象的属性和 Excel中的列关联上
 */
@Data
public class XingQiuTableUserInfo {

    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;
}