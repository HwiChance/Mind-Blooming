package com.hwichance.android.mindblooming.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.enums.ItemPosEnum
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class FlexibleLayout : RelativeLayout {

    private lateinit var primaryItem: MindMapItem
    private val items = ArrayList<MindMapItem>()

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetectorCompat? = null

    private val horInterval = 180
    val verInterval = 40

    private val edgePaint = Paint()

    private val minZoom = 0.1f
    private val maxZoom = 4.0f
    private var scaleFactor = 1f

    private val mid = PointF()

    private val mMatrix = Matrix()
    private val matrixInverse = Matrix()
    private val savedMatrix = Matrix()
    var isAddedItem = false

    private var touchPoint = FloatArray(2)

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setPaint()

        mScaleGestureDetector = ScaleGestureDetector(context, mScaleGestureListener)
        mGestureDetector = GestureDetectorCompat(context, mGestureListener)
    }

    private fun setPaint() {
        edgePaint.isAntiAlias = true
        edgePaint.style = Paint.Style.STROKE
        edgePaint.color = ContextCompat.getColor(context, R.color.soft_gray)
        edgePaint.strokeWidth = resources.getDimension(R.dimen.edge_width)
    }

    private val mScaleGestureListener: OnScaleGestureListener =
        object : SimpleOnScaleGestureListener() {
            override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
                val dist = scaleGestureDetector.currentSpan
                if (dist > 10f) {
                    savedMatrix.set(mMatrix)
                    mid.set(scaleGestureDetector.focusX, scaleGestureDetector.focusY)
                }
                return true
            }

            override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
                scaleFactor = scaleGestureDetector.scaleFactor

                if (checkScaleBound()) {
                    mMatrix.set(savedMatrix)
                    mMatrix.postScale(scaleFactor, scaleFactor, mid.x, mid.y)
                    mMatrix.invert(matrixInverse)
                    savedMatrix.set(mMatrix)
                    invalidate()
                }

                return true
            }
        }

    private val mGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            mMatrix.set(savedMatrix)
            mMatrix.postTranslate(-distanceX, -distanceY)
            mMatrix.invert(matrixInverse)
            savedMatrix.set(mMatrix)
            invalidate()

            return true
        }
    }

    fun getItemList(): ArrayList<MindMapItem> {
        return items
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = MeasureSpec.getSize(heightMeasureSpec)

        for (item in children) {
            item.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        }

        setMeasuredDimension(maxWidth, maxHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val left = (this.measuredWidth - primaryItem.measuredWidth) / 2
        val top = (this.measuredHeight - primaryItem.measuredHeight) / 2
        val right = left + primaryItem.measuredWidth
        val bottom = top + primaryItem.measuredHeight

        primaryItem.layout(left, top, right, bottom)
        setLeftFamilyPosition(primaryItem)
        setRightFamilyPosition(primaryItem)
        if (isAddedItem) {
            moveToLastInsertedItem()
            isAddedItem = false
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val values = FloatArray(9)
        mMatrix.getValues(values)
        canvas.save()
        canvas.translate(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y])
        canvas.scale(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y])

        findEdges(primaryItem, canvas)

        super.dispatchDraw(canvas)
        canvas.restore()
    }

    private fun findEdges(item: MindMapItem, canvas: Canvas) {
        for (child in item.getLeftChild()) {
            drawEdge(
                canvas,
                item.left.toFloat(),
                item.top + item.measuredHeight.toFloat() / 2,
                child.left + child.measuredWidth.toFloat(),
                child.top + child.measuredHeight.toFloat() / 2,
                true
            )
            findEdges(child, canvas)
        }
        for (child in item.getRightChild()) {
            drawEdge(
                canvas,
                item.left + item.measuredWidth.toFloat(),
                item.top + item.measuredHeight.toFloat() / 2,
                child.left.toFloat(),
                child.top + child.measuredHeight.toFloat() / 2,
                false
            )
            findEdges(child, canvas)
        }
    }

    private fun drawEdge(canvas: Canvas, sX: Float, sY: Float, eX: Float, eY: Float, isL: Boolean) {
        val oval = RectF()

        val verDist = abs(eY - sY)
        val horDist = abs(eX - sX)

        when {
            sY == eY -> {
                canvas.drawLine(sX, sY, eX, eY, edgePaint)
            }
            sY > eY -> {
                if (isL) {
                    oval.set(eX - horDist, eY, sX, sY + verDist)
                    canvas.drawArc(oval, 270f, 90f, false, edgePaint)
                } else {
                    oval.set(sX, eY, eX + horDist, sY + verDist)
                    canvas.drawArc(oval, 180f, 90f, false, edgePaint)
                }
            }
            else -> {
                if (isL) {
                    oval.set(eX - horDist, sY - verDist, sX, eY)
                    canvas.drawArc(oval, 0f, 90f, false, edgePaint)
                } else {
                    oval.set(sX, sY - verDist, eX + horDist, eY)
                    canvas.drawArc(oval, 90f, 90f, false, edgePaint)
                }
            }
        }
    }

    private fun scaledPointsToScreenPoints(point: FloatArray): FloatArray {
        mMatrix.mapPoints(point)
        return point
    }

    private fun screenPointsToScaledPoints(point: FloatArray): FloatArray {
        matrixInverse.mapPoints(point)
        return point
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        touchPoint[0] = ev.x
        touchPoint[1] = ev.y
        touchPoint = screenPointsToScaledPoints(touchPoint)
        ev.setLocation(touchPoint[0], touchPoint[1])
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchPoint[0] = event.x
        touchPoint[1] = event.y
        touchPoint = scaledPointsToScreenPoints(touchPoint)
        event.setLocation(touchPoint[0], touchPoint[1])

        mScaleGestureDetector?.onTouchEvent(event)
        mGestureDetector?.onTouchEvent(event)
        return true
    }

    private fun checkScaleBound(): Boolean {
        val values = FloatArray(9)
        mMatrix.getValues(values)
        val scaleX = values[Matrix.MSCALE_X] * scaleFactor
        val scaleY = values[Matrix.MSCALE_Y] * scaleFactor
        return (scaleX > minZoom && scaleX < maxZoom) && (scaleY > minZoom && scaleY < maxZoom)
    }

    fun addPrimaryItem(item: MindMapItem) {
        addView(item)

        item.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        item.leftTotalHeight = item.measuredHeight
        item.rightTotalHeight = item.measuredHeight

        primaryItem = item
    }

    fun addItem(item: MindMapItem, parentItem: MindMapItem) {
        addView(item)

        item.setItemParent(parentItem)

        item.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        item.leftTotalHeight = item.measuredHeight
        item.rightTotalHeight = item.measuredHeight

        when (item.itemPosition) {
            ItemPosEnum.LEFT -> {
                parentItem.leftChildHeight += item.measuredHeight
                if (parentItem.getLeftChildSize() != 0) {
                    parentItem.leftChildHeight += verInterval
                }
                parentItem.addLeftChild(item)
                changeParentLeftHeight(parentItem)
            }
            ItemPosEnum.RIGHT -> {
                parentItem.rightChildHeight += item.measuredHeight
                if (parentItem.getRightChildSize() != 0) {
                    parentItem.rightChildHeight += verInterval
                }
                parentItem.addRightChild(item)
                changeParentRightHeight(parentItem)
            }
            else -> {

            }
        }
    }

    fun removeChildViews(item: MindMapItem) {
        for (child in item.getLeftChild()) {
            removeChildViews(child)
        }
        for (child in item.getRightChild()) {
            removeChildViews(child)
        }
        removeView(item)
    }

    fun changeParentLeftHeight(parentItem: MindMapItem) {
        if (parentItem.leftTotalHeight != parentItem.leftChildHeight) {
            val prevHeight = parentItem.leftTotalHeight
            parentItem.leftTotalHeight = max(parentItem.leftChildHeight, parentItem.measuredHeight)
            val diff = parentItem.leftTotalHeight - prevHeight
            if (parentItem.getItemParent() != null) {
                parentItem.getItemParent()!!.leftChildHeight += diff
                changeParentLeftHeight(parentItem.getItemParent()!!)
            }
        }
    }

    fun changeParentRightHeight(parentItem: MindMapItem) {
        if (parentItem.rightTotalHeight != parentItem.rightChildHeight) {
            val prevHeight = parentItem.rightTotalHeight
            parentItem.rightTotalHeight =
                max(parentItem.rightChildHeight, parentItem.measuredHeight)
            val diff = parentItem.rightTotalHeight - prevHeight
            if (parentItem.getItemParent() != null) {
                parentItem.getItemParent()!!.rightChildHeight += diff
                changeParentRightHeight(parentItem.getItemParent()!!)
            }
        }
    }

    private fun setLeftFamilyPosition(item: MindMapItem) {
        val centerPos = item.top + item.measuredHeight / 2
        var nextTop = centerPos - item.leftChildHeight / 2
        for (child in item.getLeftChild()) {
            val l = item.left - horInterval - child.measuredWidth
            val t = nextTop + (child.leftTotalHeight - child.measuredHeight) / 2
            val r = l + child.measuredWidth
            val b = t + child.measuredHeight
            child.layout(l, t, r, b)
            nextTop += (child.leftTotalHeight + verInterval)
            setLeftFamilyPosition(child)
        }
    }

    private fun setRightFamilyPosition(item: MindMapItem) {
        val centerPos = item.top + item.measuredHeight / 2
        var nextTop = centerPos - item.rightChildHeight / 2
        for (child in item.getRightChild()) {
            val l = item.right + horInterval
            val t = nextTop + (child.rightTotalHeight - child.measuredHeight) / 2
            val r = l + child.measuredWidth
            val b = t + child.measuredHeight
            child.layout(l, t, r, b)
            nextTop += (child.rightTotalHeight + verInterval)
            setRightFamilyPosition(child)
        }
    }

    private fun moveToLastInsertedItem() {
        val lastItem = children.elementAt(childCount - 1)
        val widthMoveDist = (width - (lastItem.left + lastItem.right)).toFloat() / 2
        val heightMoveDist = (height - (lastItem.top + lastItem.bottom)).toFloat() / 2

        mMatrix.set(savedMatrix)

        val values = FloatArray(9)
        mMatrix.getValues(values)

        mMatrix.reset()
        mMatrix.postTranslate(widthMoveDist, heightMoveDist)
        mMatrix.postScale(
            values[Matrix.MSCALE_X],
            values[Matrix.MSCALE_Y],
            width.toFloat() / 2,
            height.toFloat() / 2
        )
        mMatrix.invert(matrixInverse)
        savedMatrix.set(mMatrix)
        invalidate()
    }

    private fun getWidthAndHeight(): Rect {
        var l = right
        var r = left
        var t = bottom
        var b = top
        for (child in children) {
            l = min(l, child.left)
            r = max(r, child.right)
            t = min(t, child.top)
            b = max(b, child.bottom)
        }
        return Rect(l, t, r, b)
    }

    fun setFullView() {
        val layoutSize = getWidthAndHeight()
        val widthTotal = layoutSize.right - layoutSize.left
        val heightTotal = layoutSize.bottom - layoutSize.top
        val widthMoveDist = (width - (layoutSize.left + layoutSize.right)).toFloat() / 2
        val heightMoveDist = (height - (layoutSize.top + layoutSize.bottom)).toFloat() / 2
        val widthDiff = widthTotal - width
        val heightDiff = heightTotal - height
        var newScaleFactor = if (widthDiff <= 0 && heightDiff <= 0) {
            1.0f
        } else {
            if (widthDiff < heightDiff) {
                height.toFloat() / heightTotal
            } else {
                width.toFloat() / widthTotal
            }
        }
        newScaleFactor = floor(newScaleFactor * 1000) / 1000

        savedMatrix.set(mMatrix)
        mMatrix.reset()
        mMatrix.postTranslate(widthMoveDist, heightMoveDist)
        mMatrix.postScale(newScaleFactor, newScaleFactor, width.toFloat() / 2, height.toFloat() / 2)
        invalidate()
    }

    fun restoreView() {
        mMatrix.set(savedMatrix)
        invalidate()
    }
}