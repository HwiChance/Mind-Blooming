package com.hwichance.android.mindblooming.custom_views.mind_map_item

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.listeners.MindMapItemClick

class MindMapItem(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    var itemPosition = ItemPosEnum.PRIMARY
    var leftTotalHeight = 0
    var rightTotalHeight = 0
    private lateinit var itemTextView: TextView

    private var itemParent: MindMapItem? = null
    private var rightChildItems = ArrayList<MindMapItem>()
    private var leftChildItems = ArrayList<MindMapItem>()

    private var itemClickListener: MindMapItemClick? = null

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, pos: ItemPosEnum, text: String, isDefaultStyle: Boolean) : this(
        context,
        null,
        0
    ) {
        itemPosition = pos

        isClickable = true
        orientation = VERTICAL
        gravity = Gravity.CENTER

        addItemTextView(text)

        if (isDefaultStyle) {
            setDefaultStyle()
        }
    }

    private fun addItemTextView(text: String) {
        itemTextView = TextView(context)
        itemTextView.text = text
        itemTextView.maxWidth = resources.getDimensionPixelSize(R.dimen.item_max_width)

        addView(itemTextView)
    }

    fun getItemTextView(): TextView {
        return itemTextView
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

    fun getLeftChildByIndex(idx: Int): MindMapItem {
        return leftChildItems[idx]
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

    fun getRightChildByIndex(idx: Int): MindMapItem {
        return rightChildItems[idx]
    }

    fun setItemText(text: CharSequence) {
        itemTextView.text = text
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

    private fun setDefaultStyle() {
        val shape = GradientDrawable()
        when (itemPosition) {
            ItemPosEnum.PRIMARY -> {
                shape.setColor(resources.getColor(R.color.blube))
                shape.cornerRadius = resources.getDimension(R.dimen.primary_item_corner_radius)
                itemTextView.gravity = Gravity.CENTER
                itemTextView.setTextAppearance(context, R.style.primary_item_text_style)
            }
            else -> {
                shape.setColor(resources.getColor(R.color.crestor))
                shape.cornerRadius = resources.getDimension(R.dimen.item_corner_radius)
                itemTextView.gravity = Gravity.START
                itemTextView.setTextAppearance(context, R.style.item_text_style)
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

    fun setOnItemClick(listener: MindMapItemClick) {
        itemClickListener = listener
    }

    override fun performClick(): Boolean {
        itemClickListener?.onClick(this)
        return super.performClick()
    }
}