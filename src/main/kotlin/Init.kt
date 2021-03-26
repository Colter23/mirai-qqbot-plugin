package top.colter.mirai.plugin

import com.alibaba.fastjson.JSON
import kotlinx.coroutines.delay
import top.colter.mirai.plugin.utils.httpGet
import top.colter.mirai.plugin.PluginConfig.BPI

suspend fun init(){
    PluginMain.logger.info("初始化数据中...")
    PluginData.userData.forEach { user ->
        delay(2000)
        val rawDynamic = httpGet(BPI["dynamic"]+user.uid ,BPI["COOKIE"]!!).getJSONObject("data").getJSONArray("cards")

        val raw0 = rawDynamic.getJSONObject(0)
        val desc = raw0.getJSONObject("desc")
        user.dynamicId = desc.getBigInteger("dynamic_id").toString()
        user.liveStatus =
            try {
                raw0.getJSONObject("display").getJSONObject("live_info").getInteger("live_status")
            }catch (e:Exception){
                0
            }

        rawDynamic.forEach { item ->
            val dy = JSON.parseObject(item.toString())
            val desc = dy.getJSONObject("desc")
            PluginMain.historyDynamic.add(desc.getBigInteger("dynamic_id").toString())
        }
    }
    PluginMain.logger.info("初始化结束")
}