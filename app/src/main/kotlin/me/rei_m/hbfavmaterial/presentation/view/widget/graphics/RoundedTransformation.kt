package me.rei_m.hbfavmaterial.presentation.view.widget.graphics

import android.graphics.*
import android.graphics.Bitmap.Config
import com.squareup.picasso.Transformation

/**
 * Picassoの画像をCircleにトリミングするTransformation.
 */
class RoundedTransformation(private val radius: Int = 50,
                            private val margin: Int = 0) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val output = Bitmap.createBitmap(source.width, source.height, Config.ARGB_8888)
        val canvas = Canvas(output)
        canvas.drawRoundRect(RectF(margin.toFloat(),
                margin.toFloat(),
                (source.width - margin).toFloat(), (source.height - margin).toFloat()),
                radius.toFloat(),
                radius.toFloat(),
                paint)

        if (source != output) {
            source.recycle()
        }

        return output
    }

    override fun key(): String {
        return "rounded(radius=${radius.toString()}, margin=${margin.toString()})"
    }
}
