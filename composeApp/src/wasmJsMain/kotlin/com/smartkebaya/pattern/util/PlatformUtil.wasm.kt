package com.smartkebaya.pattern.util

import com.smartkebaya.pattern.model.KebayaPatternSet
import com.smartkebaya.pattern.model.MeasurementInputs
import com.smartkebaya.pattern.model.UnitSystem
import kotlinx.browser.window
import kotlinx.browser.document
import org.w3c.dom.HTMLStyleElement

actual object PlatformUtil {
    actual val isWeb: Boolean = true

    actual fun printPattern(patternSet: KebayaPatternSet, inputs: MeasurementInputs, unit: UnitSystem) {
        val oldStyle = document.getElementById("print-style")
        if (oldStyle != null) {
            document.head?.removeChild(oldStyle)
        }

        val style = document.createElement("style") as HTMLStyleElement
        style.id = "print-style"
        style.textContent = """
            @media print {
                @page {
                    size: A4 portrait;
                    margin: 0;
                }
                html, body {
                    width: 210mm;
                    height: auto;
                    margin: 0 !important;
                    padding: 0 !important;
                    background: white !important;
                }
                #compose-canvas {
                    width: 210mm !important;
                    height: auto !important;
                }
                .page-container {
                    width: 210mm;
                    min-height: 297mm;
                    padding: 15mm;
                    box-sizing: border-box;
                    page-break-after: always;
                    display: flex;
                    flex-direction: column;
                }
                button, .no-print {
                    display: none !important;
                }
            }
        """.trimIndent()
        document.head?.appendChild(style)
        
        window.print()
    }

    actual suspend fun downloadGuide() {
        window.open("composeResources/smartkebayapatterngenerator.composeapp.generated.resources/files/panduan.pdf", "_blank")
    }
}
