import androidx.compose.desktop.Window
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.LinuxManItem
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.awt.Dimension
import java.awt.Point
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.JEditorPane
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.SwingUtilities

fun getResourceAsText(resource: String): String =
    (Thread.currentThread().contextClassLoader.getResourceAsStream(resource)
        ?: resource::class.java.getResourceAsStream(resource))
        .let {
            BufferedReader(InputStreamReader(it!!)).readText()
        }


@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    Window(title = "Linux Man") {
        var filter by remember { mutableStateOf("") }
        var filteredItems by remember { mutableStateOf<List<LinuxManItem>>(emptyList()) }
        var manMap by remember { mutableStateOf<Map<String, LinuxManItem>>(hashMapOf()) }

        LaunchedEffect(filter) {
            filteredItems = if (filter.trim().isEmpty()) {
                emptyList()
            } else {
                manMap
                    .filter { it.key.contains(filter.trim(), ignoreCase = true) }
                    .map { it.value }
            }
        }


        MaterialTheme {
            LaunchedEffect(Unit) {
                val indexData = getResourceAsText("linuxman/index.json")
                Json.decodeFromString<Map<String, LinuxManItem>>(indexData).also { manMap = it }
            }

            Box(Modifier.fillMaxSize()) {
                Column {
                    OutlinedTextField(
                        filter,
                        onValueChange = {
                            filter = it
                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        items(filteredItems) { item ->
                            Text(
                                text = "${item.name} - ${item.desc}",
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 16.dp)
                                    .clickable { detailWindow(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}


fun detailWindow(manData: LinuxManItem) = SwingUtilities.invokeLater {
    val manContext = getResourceAsText("linuxman${manData.path}.md")
    val window = JFrame()
    window.title = "${manData.name} - Linux Man"
    window.size = Dimension(600, 500)
    window.contentPane.add(JScrollPane(JEditorPane().apply {
        contentType = "text/html"
        text = HtmlRenderer.builder().build().render(Parser.builder().build().parse(manContext))
    }))
    window.location = Point(200,200)
    window.isVisible = true
}