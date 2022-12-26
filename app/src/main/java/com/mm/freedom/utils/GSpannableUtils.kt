package com.mm.freedom.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Created by cyc on 2018/11/5.
 *
 * 常用Span封装，不支持点击时文字颜色和背景色的设置（使用场景少，暂不添加）
 * 支持设置字体颜色、大小、下划线、删除线、点击事件 等，
 * 可以对一个字符串中的多个目标子串进行设置Span
 *
 * https://blog.csdn.net/unicorn97/article/details/83756802
 */
class GSpannableUtils(private val context: Context, text: CharSequence) : SpannableString(text) {

    companion object {

    }

    private val spanMode = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

    // 初始时，待处理的索引范围为全部字符串
    private val rangeList = mutableListOf(Pair(0, text.length))
    private var textColor: Int = 0


    /**
     *  匹配出现的第一个目标子串[target]，并记录开始和结束的index
     */
    fun first(target: String): GSpannableUtils {
        rangeList.clear()
        val index = toString().indexOf(target)
        val range = Pair(index, index + target.length)
        rangeList.add(range)
        return this
    }

    /**
     *  匹配出现的最后一个目标子串[target]，并记录开始和结束的index
     */
    fun last(target: String): GSpannableUtils {
        rangeList.clear()
        val index = toString().lastIndexOf(target)
        val range = Pair(index, index + target.length)
        rangeList.add(range)
        return this
    }

    /**
     *   匹配出现的所有目标子串[target]，并记录开始和结束的index
     */
    fun all(target: String): GSpannableUtils {
        rangeList.clear()
        val indexes = indexesOf(toString(), target)
        for (index in indexes) {
            val range = Pair(index, index + target.length)
            rangeList.add(range)
        }
        return this
    }

    /**
     *  记录源字符串[src]中目标子串 [target]出现的索引位置
     */
    fun indexesOf(src: String, target: String): MutableList<Int> {
        val positions = mutableListOf<Int>()
        var index = src.indexOf(target)
        while (index >= 0) {
            positions.add(index)
            index = src.indexOf(target, index + 1)
        }
        return positions
    }

    /**
     * 手动输入一个起点索引[from]和终点索引[to]
     */
    fun range(from: Int, to: Int): GSpannableUtils {
        rangeList.clear()
        val range = Pair(from, to + 1)
        rangeList.add(range)
        return this
    }

    /**
     * 手动输入所有起点和终点的索引范围[ranges]
     */
    fun ranges(ranges: MutableList<Pair<Int, Int>>): GSpannableUtils {
        rangeList.clear()
        rangeList.addAll(ranges)
        return this
    }

    /**
     * 计算两个字符串[startText] 和 [endText]之间的字符串的索引，加入到待处理的集合中，后续的Span设置都是对该索引范围内的字串进行的
     */
    fun between(startText: String, endText: String): GSpannableUtils {
        rangeList.clear()
        val startIndex = toString().indexOf(startText) + startText.length + 1
        val endIndex = toString().lastIndexOf(endText) - 1
        val range = Pair(startIndex, endIndex)
        rangeList.add(range)
        return this
    }

    /**
     * 给target字串设置文字绝对大小为[dp]
     */
    fun size(dp: Int): GSpannableUtils {
        for (range in rangeList) {
            setSpan(AbsoluteSizeSpan(dp, true), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给target字串设置文字相对大小，指相对于文本设定的大小的相对比例为[proportion]
     */
    fun scaleSize(proportion: Int): GSpannableUtils {
        for (range in rangeList) {
            setSpan(RelativeSizeSpan(proportion.toFloat()), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给target字串设置样式（粗体）
     */
    fun bold(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(StyleSpan(Typeface.BOLD), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给target字串设置样式（斜体）
     */
    fun italic(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(StyleSpan(Typeface.ITALIC), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给target字串设置样式（正常）
     */
    fun normal(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(StyleSpan(Typeface.NORMAL), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给target字串设置样式（粗斜体）
     */
    fun bold_italic(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(StyleSpan(Typeface.BOLD_ITALIC), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 字体样式，可以设置不同的字体，比如系统自带的SANS_SERIF、MONOSPACE和SERIF
     */
    fun font(font: String): GSpannableUtils {
        for (range in rangeList) {
            setSpan(TypefaceSpan(font), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给target字串添加删除线
     */
    fun strikethrough(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(StrikethroughSpan(), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给target字串添加下划线
     */
    fun underline(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(UnderlineSpan(), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 类似于HTML中的<li>标签的圆点效果,[dp]表示圆点和字体的间距，[colorRes]表示圆点的颜色
     */
    fun bullet(dp: Int, @ColorRes colorRes: Int?): GSpannableUtils {
        for (range in rangeList) {
            setSpan(BulletSpan(dp, colorRes ?: textColor), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 字体颜色 [colorRes]表示target字串的字体颜色
     */
    fun textColor(@ColorRes colorRes: Int): GSpannableUtils {
        textColor = ContextCompat.getColor(context, colorRes)
        for (range in rangeList) {
            setSpan(ForegroundColorSpan(textColor), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 将target字串作为下标
     */
    fun subscript(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(SubscriptSpan(), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 将target字串作为上标
     */
    fun superscript(): GSpannableUtils {
        for (range in rangeList) {
            setSpan(SuperscriptSpan(), range.first, range.second, spanMode)
        }
        return this
    }

    /**
     * 给[textView]设置一个点击事件[onTextClickListener]
     */
    fun onClick(textView: TextView, onTextClickListener: () -> Unit): GSpannableUtils {
        for (range in rangeList) {
            val span = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onTextClickListener.invoke()
                }
            }
            setSpan(span, range.first, range.second, spanMode)
        }

        textView.highlightColor = Color.TRANSPARENT
        textView.movementMethod = LinkMovementMethod.getInstance()
        return this
    }

}

fun CharSequence.buildSpannableText(context: Context, block: GSpannableUtils.() -> Unit): SpannableString {
    val spanned = GSpannableUtils(context, this)
    block.invoke(spanned)
    return spanned
}