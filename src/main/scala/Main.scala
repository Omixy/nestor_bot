import java.sql.{Connection, DriverManager, ResultSet}

import dispatch._

import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  def postMenu(menu: (String, String, String), target: String): Unit = {
    def request = url(target)
    val body = "{\"text\": \":tomato: Today's menu :tomato:\n  - %s <3\n  - %s <3\n  - %s <3\n\n(do not forget to tell them about the gluten)\"}".format(menu._1, menu._2, menu._3)
    val post = request << body
    val ret = Http(post OK as.String)
    ret()
  }

  def main(args: Array[String]): Unit = {
    val menu = getMenu
    val targets = getAllTargets()
    targets.foreach(postMenu(menu, _))
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


  def getAllTargets(): List[String] = {
    def extractList(resultSet: ResultSet, list: List[String]): List[String] =
      if (!resultSet.next()) list
      else resultSet.getString("link") :: list

    // connect to the database named "mysql" on the localhost
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/nestor"
    val username = "nestor"
    val password = "nestorrocks"

    // there's probably a better way to do this
    var connection: Connection = null

    try {
      // make the connection
      Class.forName(driver).newInstance
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT link FROM targets")
      extractList(resultSet, List())
    }
    finally {
      connection.close()
    }
  }
}
