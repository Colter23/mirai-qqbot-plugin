package top.colter.mirai.plugin.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import top.colter.mirai.plugin.PluginConfig
import top.colter.mirai.plugin.PluginConfig.basePath
import top.colter.mirai.plugin.PluginData
import top.colter.mirai.plugin.PluginData.runPath
import top.colter.mirai.plugin.PluginMain
import top.colter.mirai.plugin.bean.Dynamic
import top.colter.mirai.plugin.bean.User
import top.colter.myplugin.translate.TransApi
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.*
import java.net.URL
import java.text.SimpleDateFormat
import javax.imageio.ImageIO
import kotlin.math.ceil

/**
 * 构建动态信息图片
 */
fun buildMessageImage(dynamic: Dynamic, user: User): String {
    var msg = dynamic.content
    //文本翻译
    if (PluginConfig.SECURITY_KEY!=""){
        try {
            val api = TransApi(PluginConfig.APP_ID, PluginConfig.SECURITY_KEY)
            val resMsg = JSON.parseObject(api.getTransResult(msg, "auto", "zh"))
            if (resMsg.getString("from")!="zh") {
                msg += "\n\n翻译: \n"
                for (item in resMsg.getJSONArray("trans_result")){
                    msg+=(item as JSONObject).getString("dst")
                    msg+="\n"
                }
            }
        }catch (e: Exception){
            PluginMain.logger.error("Baidu translation failure! 百度翻译失败!")
        }
    }else{
        PluginMain.logger.error("Baidu translation API not configured! 未配置百度翻译API")
    }


    //统计最终图片所需的高度
    var height = 0
    //获取一张基础背景图
    val bg = ImageIO.read(File("$runPath$basePath/img/template/${user.uid}.png"))

    //构建一张画布以供裁剪
    val bi = BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB)
    val g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)

    //最终图片片段列表
    val biList = mutableListOf<BufferedImage>()

    //裁剪图片头 并写入时间信息
    val topBi = bi.getSubimage(0, 0, 1920, 370);
    height += 370
    biList.add(topBi)
    val topG2 : Graphics2D = topBi.graphics as Graphics2D
    topG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)

    topG2.font = Font("微软雅黑", Font.BOLD, 70)
    topG2.color = Color(148, 147, 147)
    val timestamp :Long = dynamic.timestamp*1000
    topG2.drawString(SimpleDateFormat("yyyy.MM.dd  HH:mm:ss").format(timestamp), 510, 300)

    //加一条空白区域
    height += 70
    biList.add(bi.getSubimage(0, 400, 1920, 70))

    //处理动态内容
    val stringList = mutableListOf<String>()
    val msgText = msg

    val textBi =  BufferedImage(1920, 70, BufferedImage.TYPE_INT_RGB)
    val textG2 : Graphics2D = textBi.graphics as Graphics2D
    textG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
    textG2.font = Font("微软雅黑", Font.PLAIN, 60)

    // 文字换行处理
    var l = 0
    var tin = false
    var start = 0
    var fl = 0
    for((i, c) in msgText.withIndex()){
        if (c == '\n'){
            stringList.add(msgText.substring(start, i))
            start = i+1
            l = 0
        }
        if (c == '[') tin = true
        if (!tin) fl = textG2.font.getStringBounds(c.toString(), textG2.fontRenderContext).width.toInt()
        if (c == ']'){
            tin = false
            l += 65
        }
        if (l+fl>1680){
            stringList.add(msgText.substring(start, i + 1))
            start = i+1
            fl = 0
            l = 0
        }else{
            l += fl
            fl = 0
        }
    }
    stringList.add(msgText.substring(start))

    //构建动态内容图片片段
    for (index in 1..stringList.size){
        val centerBi =  BufferedImage(1920, 70, BufferedImage.TYPE_INT_RGB)
        val centerG2 : Graphics2D = centerBi.graphics as Graphics2D
        centerG2.drawImage(bi.getSubimage(0, 470, 1920, 70), 0, 0, null)
        centerG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)

        centerG2.font = Font("微软雅黑", Font.PLAIN, 60)
        centerG2.color = Color(87, 87, 87)

        height += 70
        biList.add(centerBi)

        var start = 0
        var end = 0
        var text = stringList[index - 1]
        var x = 100
        //解析b站表情 [tv_doge]
        while (text.indexOf('[')!=-1){
            start = text.indexOf('[')
            end = text.indexOf(']')+1
            val tempText = text.substring(0, start)
            val tempSimp = text.substring(start, end)
            text = text.substring(end)

            if (start != 0){
                centerG2.drawString(tempText, x, 50)
                //计算字符串像素长度 用于绘制表情
                x += centerG2.font.getStringBounds(tempText, centerG2.fontRenderContext).width.toInt()
            }
            try{
                val emoji = PluginMain.emojiMap[tempSimp]
                val reEmoji = emoji?.getScaledInstance(65, 65, java.awt.Image.SCALE_DEFAULT)
                centerG2.drawImage(reEmoji, x, 0, null)
            }catch (e: Exception){

            }
            x+=65
        }
        centerG2.drawString(text, x, 50)
    }

    try{
        //如动态有图片则添加图片
        if (dynamic.pictures != null && dynamic.pictures?.size!=0){
            for (imgSrc in dynamic.pictures!!){
                val img = ImageIO.read(URL(imgSrc))
                val reHeight = (img.height*1720.0)/img.width
                val reImg = img.getScaledInstance(1720, reHeight.toInt(), java.awt.Image.SCALE_DEFAULT)
                val row : Int = ceil(reHeight / 70.0).toInt()
                val imgBi = BufferedImage(1920, row * 70, BufferedImage.TYPE_INT_RGB)
                val imgG2 = imgBi.graphics as Graphics2D
                for (index in 1..row) {
                    imgG2.drawImage(bi.getSubimage(0, 470, 1920, 70), 0, (index - 1) * 70, null)
                }
                imgG2.drawImage(reImg, 100, 30, null)
                height += row*70
                biList.add(imgBi)
            }
        }
    }catch (e: Exception){
        PluginMain.logger.error("绘制图片失败")
    }

    //内容底部留空
    height += 70
    biList.add(bi.getSubimage(0, 400, 1920, 70))

    //构建底部图片片段 写入动态ID
    val bottomBi = bi.getSubimage(0, 995, 1920, 85)
    val bottomG2 : Graphics2D = bottomBi.graphics as Graphics2D
    bottomG2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
    height += 85
    biList.add(bottomBi)
    bottomG2.font = Font("微软雅黑", Font.BOLD, 45)
    bottomG2.color = Color(217, 217, 217)

    var path = ""
    if (dynamic.isDynamic){
        bottomG2.drawString("动态ID:${dynamic.did}", 80, 39)
        path = "$runPath$basePath/img/dynamic/${user.uid}/${dynamic.did}.jpg"
    }else{
        bottomG2.drawString("直播ID:${dynamic.did}", 80, 39)
        path = "$runPath$basePath/img/dynamic/live-${SimpleDateFormat("yyMMdd-HHmm").format(timestamp)}.jpg"
    }

    //构建最终图片
    val endBi = BufferedImage(1920, height, BufferedImage.TYPE_INT_RGB)
    val endG2 : Graphics2D = endBi.graphics as Graphics2D
    var preY = 0
    for (bi in biList){
        endG2.drawImage(bi, 0, preY, null)
        preY += bi.height
    }


    //把图片写入文件
    return try{
        ImageIO.write(endBi, "JPEG", FileOutputStream(path))
        path
    }catch (e: Exception){
        PluginMain.logger.error("储存图片失败")
        ""
    }
}


/**
 * 生成每日总结图片
 */
fun buildSummaryImage(timestamp: Long, info: MutableMap<String,MutableMap<String, Int>>): String {

    val time = SimpleDateFormat("MM.dd").format(timestamp)

    var bg = ImageIO.read(File("$runPath$basePath/img/template/summary.png"))
    var bi = BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB)
    var g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
//    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.font = Font("微软雅黑", Font.BOLD, 68)
    g2.color = Color(52, 52, 52)
    g2.drawString(time, 690, 113)

    g2.font = Font("汉仪汉黑W", Font.BOLD, 70)
    g2.color = Color(87, 87, 87)

    var y = 280
    PluginData.summaryList.forEach { uid ->
        g2.drawString(info[uid]?.get("fan").toString(), 1050, y)
        g2.drawString(info[uid]?.get("riseFan").toString(), 1570, y)
        y += 105
        g2.drawString(info[uid]?.get("guard").toString(), 1050, y)
        g2.drawString(info[uid]?.get("riseGuard").toString(), 1570, y)
        y += 200
    }

    val path = "$runPath$basePath/img/summary/${SimpleDateFormat("yyyyMMdd").format(timestamp)}.jpg"
    //把图片写入文件
    return try{
        ImageIO.write(bi, "JPEG", FileOutputStream(path))
        path
    }catch (e: Exception){
        PluginMain.logger.error("储存图片失败")
        ""
    }
}

/**
 * 生成粉丝数图片
 */
fun buildFanImage(uid: Int): String {

    val followNum = httpGet(PluginConfig.followNumApi + uid).getJSONObject("data").getInteger("follower").toInt()

    val bg = ImageIO.read(File("$runPath$basePath/img/template/$uid" + "fan.png"))
    val bi = BufferedImage(1920, 426, BufferedImage.TYPE_INT_RGB)
    val g2 : Graphics2D = bi.graphics as Graphics2D
    g2.drawImage(bg, 0, 0, null) //画入背景
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)

    g2.font = Font("汉仪汉黑W", Font.BOLD, 85)
    g2.color = Color(87, 87, 87)
    g2.drawString(followNum.toString(), 870, 270)

    g2.font = Font("微软雅黑", Font.BOLD, 60)
    g2.color = Color(148, 147, 147)
    val timestamp :Long = System.currentTimeMillis()
    g2.drawString(SimpleDateFormat("yyyy.MM.dd  HH:mm:ss").format(timestamp), 570, 345)

    val path = "$runPath$basePath/img/fan/${uid}-${SimpleDateFormat("yyyyMMdd").format(timestamp)}.jpg"
    //把图片写入文件
    return try{
        ImageIO.write(bi, "JPEG", FileOutputStream(path))
        path
    }catch (e: Exception){
        PluginMain.logger.error("储存图片失败")
        ""
    }
}