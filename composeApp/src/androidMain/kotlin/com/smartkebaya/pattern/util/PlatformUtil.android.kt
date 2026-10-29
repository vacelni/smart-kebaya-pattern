package com.smartkebaya.pattern.util

import android.content.Context
import android.widget.Toast
import com.smartkebaya.pattern.model.KebayaPatternSet
import com.smartkebaya.pattern.model.MeasurementInputs
import com.smartkebaya.pattern.model.UnitSystem
import java.lang.ref.WeakReference
import java.io.File
import java.io.FileOutputStream
import smartkebayapatterngenerator.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

actual object PlatformUtil {
    actual val isWeb: Boolean = false
    private var contextRef: WeakReference<Context>? = null

    fun setContext(context: Context) {
        contextRef = WeakReference(context)
    }

    actual fun printPattern(patternSet: KebayaPatternSet, inputs: MeasurementInputs, unit: UnitSystem) {
        contextRef?.get()?.let { context ->
            Toast.makeText(context, "Sedang menyiapkan PDF...", Toast.LENGTH_SHORT).show()
            val file = PdfUtil.generatePatternPdf(context, patternSet, inputs, unit)
            if (file != null) {
                PdfUtil.openPdf(context, file)
            } else {
                Toast.makeText(context, "Gagal membuat PDF", Toast.LENGTH_LONG).show()
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    actual suspend fun downloadGuide() {
        contextRef?.get()?.let { context ->
            try {
                val bytes = Res.readBytes("files/panduan.pdf")
                val guideFile = File(context.cacheDir, "panduan_aplikasi.pdf")
                FileOutputStream(guideFile).use { it.write(bytes) }
                PdfUtil.openPdf(context, guideFile)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Gagal membuka panduan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
