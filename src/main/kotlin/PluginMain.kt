package top.colter.mirai.plugin

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.registeredCommands
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.info
import java.lang.Exception

object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "top.colter.dynamic-plugin",
        name = "DynamicPlugin",
        version = "0.1.0"
    )
) {

    // 动态历史记录
    val historyDynamic : MutableList<String> = mutableListOf()

    // b站表情
    val emojiMap = mutableMapOf<String,java.awt.Image>()

    var ys = mutableMapOf<Long,Int>()

    var goodWorkCount = 0
    var tempTime : Long = 0

    override fun onEnable() {
        logger.info { "Plugin loaded" }

        //加载插件配置数据
        PluginConfig.reload()
        //加载插件数据
        PluginData.reload()

        //设置运行路径
        PluginData.runPath = System.getProperty("user.dir")

        PluginMain.launch {
            logger.info("forward......")
            //检测动态更新 并发送给群

            delay(6000)
            lateinit var bot : Bot
            Bot.instances.forEach { b: Bot ->
                bot = b
            }
            bot.eventChannel.registerListenerHost(NewFriendRequestListener)
            bot.eventChannel.registerListenerHost(MemberJoinListener)
            bot.eventChannel.registerListenerHost(GroupListener)
            bot.eventChannel.registerListenerHost(FriendListener)
            bot.eventChannel.registerListenerHost(MessageListener)
            check(bot)

        }


    }
}