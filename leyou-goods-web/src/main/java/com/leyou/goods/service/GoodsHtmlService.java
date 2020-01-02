package com.leyou.goods.service;

import com.leyou.common.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>
 * <code>GoodsHtmlService</code>
 * </p>
 * 
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/12/31 15:16
 */
@Service
public class GoodsHtmlService {

    @Autowired
    private GoodsService goodsServicel;

    @Autowired
    private TemplateEngine templateEngine;

   /**
    *
    * @description
    * @author huiwang45@iflytek.com
    * @date 2019/12/31 15:18
    * @param 
    * @return 
    */
    public void createHtml(Long spuId) {

        //初始化运行上下文
        Context context = new Context();
        //设置数据模型
        context.setVariables(this.goodsServicel.loadData(spuId));
        //文件存储路径
        String filePath = "C:\\Users\\Administrator\\Desktop\\nginx-1.16.0\\nginx-1.16.0\\html\\user";

        PrintWriter printWriter = null;

        try {
            //把静态文件生成到服务器本地
            File file = new File(filePath +"\\"+ spuId + ".html");
            printWriter = new PrintWriter(file);
            this.templateEngine.process("user",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(printWriter != null){
                printWriter.close();
            }
        }
    }

    /**
     * 删除文件
     * @description
     * @author huiwang45@iflytek.com
     * @date 2020/01/01 15:35
     * @param
     * @return 
     */
    public void deleteHtml(Long id) {
        String filePath = "C:\\Users\\Administrator\\Desktop\\nginx-1.16.0\\nginx-1.16.0\\html\\user";
        File file = new File(filePath +"\\"+ id + ".html");
        file.deleteOnExit();
    }
}