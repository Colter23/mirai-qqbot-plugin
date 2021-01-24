package top.colter.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value
import top.colter.mirai.plugin.bean.User

object PluginData : AutoSavePluginData("pluginData"){
    // 运行路径 在初始化时赋值
    var runPath by value("./")

    var summaryDate by value("")

    // 动态计数
    var dynamicCount : Int by value(0)

    var followList : MutableList<String> by value()

    var groupList : MutableList<Long> by value()
    var friendList : MutableList<Long> by value()

    var followMemberGroup : MutableMap<String,MutableList<Long>> by value()

    var summaryList : MutableList<String> by value()

    var userData : MutableList<User> by value()
}