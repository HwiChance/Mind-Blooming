package com.hwichance.android.mindblooming.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.RelativeLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapItem

class FlexibleLayout : RelativeLayout {

    private lateinit var primaryItem: MindMapItem

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetectorCompat? = null

    val horMargin = 200
    val verMargin = 20

    // TODO: Calculate minZoom
    private val minZoom = 0.5f
    private val maxZoom = 4.0f
    private var scaleFactor = 1f

    private val mid = PointF()

    private val mMatrix = Matrix()
    private val matrixInverse = Matrix()
    private val savedMatrix = Matrix()

    private var touchPoint = FloatArray(2)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        mScaleGestureDetector = ScaleGestureDetector(context, mScaleGestureListener)
        mGestureDetector = GestureDetectorCompat(context, mGestureListener)
    }

    private val mScaleGestureListener: OnScaleGestureListener =
        object : SimpleOnScaleGestureListener() {
            override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
                val dist = scaleGestureDetector.currentSpan
                if (dist > 10f) {
                    savedMatrix.set(mMatrix)
                    mid[scaleGestureDetector.focusX] = scaleGestureDetector.focusY
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
                child.left + child.measuredWidth.toFloat(),
                child.top + child.measuredHeight.toFloat() / 2,
                item.left.toFloat(),
                item.top + item.measuredHeight.toFloat() / 2
            )
            findEdges(child, canvas)
        }
        for (child in item.getRightChild()) {
            drawEdge(
                canvas,
                item.left + item.measuredWidth.toFloat(),
                item.top + item.measuredHeight.toFloat() / 2,
                child.left.toFloat(),
                child.top + child.measuredHeight.toFloat() / 2
            )
            findEdges(child, canvas)
        }
    }

    private fun drawEdge(canvas: Canvas, startX: Float, startY: Float, stopX: Float, stopY: Float) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = resources.getColor(R.color.white_gray)
        paint.strokeWidth = resources.getDimension(R.dimen.edge_width)

        canvas.drawLine(startX, startY, stopX, stopY, paint)
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
                var heightIncrease = 0
                if (parentItem.getLeftChildSize() > 0) {
                    heightIncrease = item.measuredHeight + verMargin
                } else {
                    if (item.measuredHeight > parentItem.measuredHeight) {
                        parentItem.leftTotalHeight = parentItem.measuredHeight
                        heightIncrease = item.measuredHeight - parentItem.measuredHeight
                    } else {
                        parentItem.leftTotalHeight = item.measuredHeight
                    }
                }

                parentItem.addLeftChild(item)

                if (heightIncrease > 0) {
                    changeParentLeftHeight(parentItem, heightIncrease)
                }
            }
            ItemPosEnum.RIGHT -> {
                var heightIncrease = 0
                if (parentItem.getRightChildSize() > 0) {
                    heightIncrease = item.measuredHeight + verMargin
                } else {
                    if (item.measuredHeight > parentItem.measuredHeight) {
                        parentItem.rightTotalHeight = parentItem.measuredHeight
                        heightIncrease = item.measuredHeight - parentItem.measuredHeight
                    } else {
                        parentItem.rightTotalHeight = item.measuredHeight
                    }
                }

                parentItem.addRightChild(item)

                if (heightIncrease > 0) {
                    changeParentRightHeight(parentItem, heightIncrease)
                }
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

    fun changeParentLeftHeight(parentItem: MindMapItem, heightIncrease: Int) {
        parentItem.leftTotalHeight += heightIncrease
        if (parentItem.getItemParent() != null) {
            changeParentLeftHeight(parentItem.getItemParent() as MindMapItem, heightIncrease)
        }
    }

    fun changeParentRightHeight(parentItem: MindMapItem, heightIncrease: Int) {
        parentItem.rightTotalHeight += heightIncrease
        if (parentItem.getItemParent() != null) {
            changeParentRightHeight(parentItem.getItemParent() as MindMapItem, heightIncrease)
        }
    }

    private fun setLeftFamilyPosition(item: MindMapItem) {
        val centerPos = item.top + item.measuredHeight / 2
        var nextTop = centerPos - item.leftTotalHeight / 2
        for (child in item.getLeftChild()) {
            val l = item.left - horMargin - child.measuredWidth
            val t = nextTop + (child.leftTotalHeight - child.measuredHeight) / 2
            val r = l + child.measuredWidth
            val b = t + child.measuredHeight
            child.layout(l, t, r, b)
            nextTop += (child.leftTotalHeight + verMargin)
            setLeftFamilyPosition(child)
        }
    }

    private fun setRightFamilyPosition(item: MindMapItem) {
        val centerPos = item.top + item.measuredHeight / 2
        var nextTop = centerPos - item.rightTotalHeight / 2
        for (child in item.getRightChild()) {
            val l = item.right + horMargin
            val t = nextTop + (child.rightTotalHeight - child.measuredHeight) / 2
            val r = l + child.measuredWidth
            val b = t + child.measuredHeight
            child.layout(l, t, r, b)
            nextTop += (child.rightTotalHeight + verMargin)
            setRightFamilyPosition(child)
        }
    }
}