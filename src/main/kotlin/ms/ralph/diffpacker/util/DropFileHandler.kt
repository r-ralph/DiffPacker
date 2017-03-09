package ms.ralph.diffpacker.util

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.File
import java.io.IOException
import javax.swing.TransferHandler

class DropFileHandler : TransferHandler() {

    private var listener: ((List<File>) -> Unit)? = null

    override fun canImport(support: TransferHandler.TransferSupport): Boolean {
        if (!support.isDrop) {
            return false
        }
        if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            return false
        }
        return true
    }

    override fun importData(support: TransferHandler.TransferSupport): Boolean {
        if (!canImport(support)) {
            return false
        }
        val t = support.transferable
        try {
            @Suppress("UNCHECKED_CAST")
            val files = t.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
            listener?.invoke(files)
        } catch (e: UnsupportedFlavorException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    fun setDropFilesListener(listener: ((List<File>) -> Unit)?) {
        this.listener = listener
    }
}
