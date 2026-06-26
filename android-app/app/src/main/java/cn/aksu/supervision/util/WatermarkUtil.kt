package cn.aksu.supervision.util

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 图片水印工具
 * 在照片上添加时间+地点+企业+检查员水印信息
 */
object WatermarkUtil {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    /**
     * 为图片添加水印
     * @param context Context
     * @param srcPath 原图路径
     * @param location 地点描述
     * @param enterprise 企业名称
     * @param inspector 检查员姓名
     * @return 添加水印后的图片路径
     */
    fun addWatermark(
        context: Context,
        srcPath: String,
        location: String?,
        enterprise: String?,
        inspector: String?
    ): String? {
        return try {
            val srcFile = File(srcPath)
            if (!srcFile.exists()) return null

            val bitmap = BitmapFactory.decodeFile(srcPath) ?: return null
            val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            bitmap.recycle()

            val canvas = Canvas(mutableBitmap)
            val paint = Paint().apply {
                color = Color.WHITE
                alpha = 200
                textSize = mutableBitmap.width * 0.035f
                isAntiAlias = true
                setShadowLayer(2f, 1f, 1f, Color.BLACK)
            }

            val padding = mutableBitmap.width * 0.03f
            val lineHeight = paint.textSize * 1.4f
            var y = mutableBitmap.height - padding - lineHeight * 4

            // 绘制半透明背景
            val bgPaint = Paint().apply {
                color = Color.BLACK
                alpha = 100
            }
            canvas.drawRect(
                0f, y - padding,
                mutableBitmap.width.toFloat(),
                mutableBitmap.height.toFloat(),
                bgPaint
            )

            // 时间水印
            val timeStr = dateFormat.format(Date())
            canvas.drawText(timeStr, padding, y, paint)
            y += lineHeight

            // 地点水印
            location?.let {
                canvas.drawText("地点：$it", padding, y, paint)
                y += lineHeight
            }

            // 企业水印
            enterprise?.let {
                val text = "企业：$it"
                val displayText = if (paint.measureText(text) > mutableBitmap.width - padding * 2) {
                    // 截断过长文字
                    val maxChars = ((mutableBitmap.width - padding * 2) / paint.textSize).toInt()
                    text.take(maxChars) + "…"
                } else text
                canvas.drawText(displayText, padding, y, paint)
                y += lineHeight
            }

            // 检查员水印
            inspector?.let {
                canvas.drawText("检查员：$it", padding, y, paint)
            }

            // 保存水印图片
            val watermarkDir = File(context.filesDir, "watermark_photos")
            if (!watermarkDir.exists()) watermarkDir.mkdirs()
            val destFile = File(watermarkDir, "wm_${System.currentTimeMillis()}.jpg")
            FileOutputStream(destFile).use { fos ->
                mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            }
            mutableBitmap.recycle()

            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 读取图片的 EXIF 方向信息并旋转
     */
    private fun rotateIfNecessary(bitmap: Bitmap, path: String): Bitmap {
        return try {
            val exif = ExifInterface(path)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            val rotation = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            if (rotation == 0f) return bitmap

            val matrix = Matrix()
            matrix.postRotate(rotation)
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (_: Exception) {
            bitmap
        }
    }
}
