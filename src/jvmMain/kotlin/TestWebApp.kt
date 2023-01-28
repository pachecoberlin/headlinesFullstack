import java.io.IOException
import java.util.*
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class HelloServlet : HttpServlet() {
    @Throws(ServletException::class, IOException::class)
    public override fun doGet(request: HttpServletRequest, response: HttpServletResponse) {
        response.contentType = "text/html"
        val out = response.writer
        out.println("<html><head>")
        out.println("<title>Hello Servlet</title>")
        out.println("<link rel='stylesheet' type='text/css' href='styles.css' />")
        out.println("</head><body>")
        out.print("<p>Today is: " + Date())
        out.print("</p>")
        out.print("<h1>The Hello Servlet Demo</h1>")
        out.println("<p>This is the first hello servlet</p>")
        out.println("<img src='images/Tulips.PNG' width='100' height='100' />")
        out.println("</body></html>")
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}