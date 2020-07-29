import org.asciidoctor.Asciidoctor
import org.asciidoctor.Asciidoctor.Factory.create
import org.junit.Test
import java.io.File
import java.util.*


class AdocTest {

    @Test
    fun testGenerateAdoc() {

        val asciidoctor: Asciidoctor = create()

        val html: String = asciidoctor.convert(
                "Writing AsciiDoc is _easy_!",
                HashMap<String, Any>())
        println(html)
        val result = asciidoctor.convertFiles(
                Arrays.asList(File("sample.adoc")),
                HashMap())

        for (html1 in result) {
            println(html1)
        }
    }
}