package ms.ralph.diffpacker.util.ext

import javax.swing.JTextArea

fun JTextArea.appendln(text: String) {
    append(text + "\n")
}