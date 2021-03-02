package com.hwichance.android.mindblooming.custom_views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.enums.ItemPosEnum
import com.hwichance.android.mindblooming.listeners.MindMapItemClick
import com.hwichance.android.mindblooming.rooms.data.MindMapItemData
import kotlin.math.max

class MindMapItem(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var itemData: MindMapItemData
    var itemPosition = ItemPosEnum.PRIMARY
    var leftTotalHeight = 0
    var rightTotalHeight = 0
    var leftChildHeight = 0
    var rightChildHeight = 0

    private lateinit var shape: GradientDrawable

    private lateinit var itemTextView: TextView

    private var itemParent: MindMapItem? = null
    private var rightChildItems = ArrayList<MindMapItem>()
    private var leftChildItems = ArrayList<MindMapItem>()

    private var itemClickListener: MindMapItemClick? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, data: MindMapItemData) : this(context, null, 0) {
        itemData = data
        itemPosition = itemData.itemPos

        isClickable = true
        orientation = VERTICAL
        gravity = Gravity.CENTER

        addItemTextView(itemData.itemText)
        setBaseStyle()
        setColorStyle(itemData.backgroundColor, itemData.textColor)
    }

    private fun addItemTextView(text: String) {
        itemTextView = TextView(context)
        itemTextView.text = text
        itemTextView.maxWidth = resources.getDimensionPixelSize(R.dimen.item_max_width)

        addView(itemTextView)
    }

    fun getItemData(): MindMapItemData {
        return itemData
    }

    fun addLeftChild(item: MindMapItem) {
        leftChildItems.add(item)
    }

    fun getLeftChild(): ArrayList<MindMapItem> {
        return leftChildItems
    }

    fun getLeftChildSize(): Int {
        return leftChildItems.size
    }

    fun addRightChild(item: MindMapItem) {
        rightChildItems.add(item)
    }

    fun getRightChild(): ArrayList<MindMapItem> {
        return rightChildItems
    }

    fun getRightChildSize(): Int {
        return rightChildItems.size
    }

    private fun setItemText(text: CharSequence) {
        itemTextView.text = text
        measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)

        when (itemPosition) {
            ItemPosEnum.LEFT -> {
                val prevHeight = leftTotalHeight
                leftTotalHeight = max(leftTotalHeight, measuredHeight)
                if(prevHeight != leftTotalHeight && itemParent != null) {
                    itemParent!!.leftChildHeight += (leftTotalHeight - prevHeight)
                }
            }
            ItemPosEnum.RIGHT -> {
                val prevHeight = rightTotalHeight
                rightTotalHeight = max(rightTotalHeight, measuredHeight)
                if(prevHeight != rightTotalHeight && itemParent != null) {
                    itemParent!!.rightChildHeight += (rightTotalHeight - prevHeight)
                }
            }
            else -> {

            }
        }
    }

    fun getItemText(): String {
        return itemTextView.text.toString()
    }

    fun setItemParent(parent: MindMapItem) {
        itemParent = parent
    }

    fun getItemParent(): MindMapItem? {
        return itemParent
    }

    private fun setBaseStyle() {
        when (itemPosition) {
            ItemPosEnum.PRIMARY -> {
                shape = ContextCompat.getDrawable(
                    context,
                    R.drawable.primary_mind_map_item_shape
                ) as GradientDrawable
                itemTextView.gravity = Gravity.CENTER
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    itemTextView.setTextAppearance(R.style.primary_item_text_style)
                } else {
                    itemTextView.setTextAppearance(context, R.style.primary_item_text_style)
                }
            }
            else -> {
                shape = ContextCompat.getDrawable(
                    context,
                    R.drawable.mind_map_item_shape
                ) as GradientDrawable
                itemTextView.gravity = Gravity.START
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    itemTextView.setTextAppearance(R.style.item_text_style)
                } else {
                    itemTextView.setTextAppearance(context, R.style.item_text_style)
                }
            }
        }
        background = shape
        setPadding(
            resources.getDimension(R.dimen.item_padding_vertical).toInt(),
            resources.getDimension(R.dimen.item_padding_horizontal).toInt(),
            resources.getDimension(R.dimen.item_padding_vertical).toInt(),
            resources.getDimension(R.dimen.item_padding_horizontal).toInt()
        )
    }

    private fun setColorStyle(bgColor: Int, txtColor: Int) {
        shape.setColor(bgColor)
        background = shape
        itemTextView.setTextColor(txtColor)
    }

    fun setOnItemClick(listener: MindMapItemClick) {
        itemClickListener = listener
    }

    fun applyChanges(data: MindMapItemData) {
        itemData = data
        setColorStyle(itemData.backgroundColor, itemData.textColor)
        setItemText(itemData.itemText)
    }

    override fun performClick(): Boolean {
        itemClickListener?.onClick(this)
        return super.performClick()
    }
}