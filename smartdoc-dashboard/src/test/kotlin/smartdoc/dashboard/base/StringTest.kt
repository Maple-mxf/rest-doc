package smartdoc.dashboard.base

import org.junit.Test
import smartdoc.dashboard.util.MD5Util

class StringTest {

    @Test
    fun testReplace() {
//        val result = "a[1].b[2]".replace(Regex("\\[\\d+\\]"), "[]")
//        print(result)

        val url = "https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&rsv_idx=1&tn=baidu&wd=http%E8%AE%BE%E7%BD%AE%E6%96%87%E4%BB%B6%E5%93%8D%E5%BA%94%E5%A4%B4&fenlei=256&oq=http%25E8%25BF%2594%25E5%259B%259E%25E5%2593%258D%25E5%25BA%2594%25E6%25B5%2581&rsv_pq=81361b6c0006d6f5&rsv_t=1752BkN06%2Bnv604jfTZTVQtM9kuHOhRIaiePyEeGznLFBFFBma3mA7FKldQ&rqlang=cn&rsv_enter=1&rsv_dl=tb&rsv_btype=t&inputT=5840&rsv_sug3=58&rsv_sug1=1&rsv_sug7=000&rsv_sug2=0&prefixsug=http%25E8%25AE%25BE%25E7%25BD%25AE%25E6%2596%2587%25E4%25BB%25B6%25E5%2593%258D%25E5%25BA%2594%25E5%25A4%25B4&rsp=0&rsv_sug4=7734&rsv_sug=1"
        val si = url.lastIndexOf("?")

        if (si != -1) {
            val result = url.substring(startIndex = si).split("&")
            for (s in result) {
                println(s.split("="))
            }
        }

        // dd63da87e71a8309795a64746fe8dbef
        println(smartdoc.dashboard.util.MD5Util.encryptPassword("restdoc","746946688983240704",1024))
    }


}