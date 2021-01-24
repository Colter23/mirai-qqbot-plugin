package top.colter.mirai.plugin.bean

import com.alibaba.fastjson.JSONObject

class Dynamic {
    var did = ""
    var uid = ""
    var type = 0
    var timestamp : Long = 0
    var content = ""
    var contentJson : JSONObject = JSONObject()
    var isDynamic = true
    var pictures : MutableList<String>? = null
    var display : JSONObject = JSONObject()
}