import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  def postMenu(menu: (String, String, String)): Unit = {
    def request = url("https://hooks.slack.com/services/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")
    val body = "{\"text\": \":tomato: Today's menu :tomato:\n  - %s <3\n  - %s <3\n  - %s <3\n\n(do not forget to tell them about the gluten)\"}".format(menu._1, menu._2, menu._3)
    val post = request << body
    val ret = Http(post OK as.String)
    ret()
  }

  def main(args: Array[String]): Unit = {
    val menu = getMenu
    postMenu(menu)
  }

  def getMenu: (String, String, String) = {
    val request = url("https://nestorparis.com/home/index.php?controller=authentication")
    val info = Map("email" -> "thuzhen@gmail.com", "passwd" -> "sKK6bBWy", "back" -> "my-account", "SubmitLogin" -> "")
    val post = request << info

    val r = """<ul>\s*<li>(.+?)</li>\s*<li>(.+?)</li>\s*<li>(.+?)</li>\s*</ul>""".r
    val res = Http.configure(_ setFollowRedirects true)(post OK as.String)
    val content = res()
    val lis = (r findAllIn content).toList
    val menu = lis(1)
    menu match {
      case r(starter, main, dessert) => (starter, main, dessert)
    }
  }
}
