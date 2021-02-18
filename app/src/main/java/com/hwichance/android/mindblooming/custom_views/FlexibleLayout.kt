package com.hwichance.android.mindblooming.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.RelativeLayout
import androidx.core.view.GestureDetectorCompat
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapEdge
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapItem

class FlexibleLayout : RelativeLayout {

    private lateinit var primaryItem: MindMapItem
    private val leftItemEdges = ArrayList<MindMapEdge>()
    private val rightItemEdges = ArrayList<MindMapEdge>()

    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetectorCompat? = null

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

    override fun dispatchDraw(canvas: Canvas) {
        val values = FloatArray(9)
        mMatrix.getValues(values)
        canvas.save()
        Log.v("flexible", "value: ${values[0]} and ${values[1]} and ${values[2]}")
        canvas.translate(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y])
        canvas.scale(values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y])
        for (edges in leftItemEdges) {
            drawEdge(
                canvas,
                edges.item.x + edges.item.measuredWidth,
                edges.item.y + edges.item.measuredHeight / 2,
                edges.parentItem.x,
                edges.parentItem.y + edges.parentItem.measuredHeight / 2
            )
        }

        for (edges in rightItemEdges) {
            drawEdge(
                canvas,
                edges.parentItem.x + edges.parentItem.measuredWidth,
                edges.parentItem.y + edges.parentItem.measuredHeight / 2,
                edges.item.x,
                edges.item.y + edges.item.measuredHeight / 2
            )
        }
        super.dispatchDraw(canvas)
        canvas.restore()
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
        Log.v("flexible", "here5 : ${primaryItem.x} and ${primaryItem.y}")
        touchPoint[0] = ev.x
        touchPoint[1] = ev.y
        touchPoint = screenPointsToScaledPoints(touchPoint)
        ev.setLocation(touchPoint[0], touchPoint[1])
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        Log.v("flexible", "here4 : ${primaryItem.x} and ${primaryItem.y}")
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
        item.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        item.gravity = CENTER_IN_PARENT

        addView(item)

        primaryItem = item
    }

    fun addItem(item: MindMapItem, parentItem: MindMapItem, horMargin: Int, verMargin: Int) {
        addView(item)

        item.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        item.leftTotalHeight = item.measuredHeight
        item.rightTotalHeight = item.measuredHeight

        item.setItemParent(parentItem)

        item.y = parentItem.y + (parentItem.measuredHeight - item.measuredHeight).toFloat() / 2

        when (item.itemPosition) {
            ItemPosEnum.LEFT -> {
                var heightIncrease = 0
                val childNum = parentItem.getLeftChildSize()

                item.x = parentItem.x - (item.measuredWidth + horMargin)

                if (childNum > 0) {
                    for (child in parentItem.getLeftChild()) {
                        moveLeftItems(child, (item.measuredHeight.toFloat() + verMargin) / 2)
                    }
                    val lastChild = parentItem.getLeftChildByIndex(childNum - 1)
                    item.y = lastChild.y + lastChild.measuredHeight + verMargin
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
                    moveLeftFamily(primaryItem, verMargin)
                }

                leftItemEdges.add(MindMapEdge(item, parentItem))
            }
            ItemPosEnum.RIGHT -> {
                var heightIncrease = 0
                val childNum = parentItem.getRightChildSize()

                item.x = parentItem.x + (parentItem.measuredWidth + horMargin)

                if (childNum > 0) {
                    for (child in parentItem.getRightChild()) {
                        moveRightItems(child, (item.measuredHeight.toFloat() + verMargin) / 2)
                    }
                    val lastChild = parentItem.getRightChildByIndex(childNum - 1)
                    item.y = lastChild.y + lastChild.measuredHeight + verMargin
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
                    moveRightFamily(primaryItem, verMargin)
                }

                rightItemEdges.add(MindMapEdge(item, parentItem))
            }
            else -> {

            }
        }
    }


    private fun moveLeftItems(item: MindMapItem, distance: Float) {
        item.y -= distance
        for (child in item.getLeftChild()) {
            moveLeftItems(child, distance)
        }
    }

    private fun moveRightItems(item: MindMapItem, distance: Float) {
        item.y -= distance
        for (child in item.getRightChild()) {
            moveRightItems(child, distance)
        }
    }

    private fun changeParentLeftHeight(parentItem: MindMapItem, heightIncrease: Int) {
        parentItem.leftTotalHeight += heightIncrease
        if (parentItem.getItemParent() != null) {
            changeParentLeftHeight(parentItem.getItemParent() as MindMapItem, heightIncrease)
        }
    }

    private fun changeParentRightHeight(parentItem: MindMapItem, heightIncrease: Int) {
        parentItem.rightTotalHeight += heightIncrease
        if (parentItem.getItemParent() != null) {
            changeParentRightHeight(parentItem.getItemParent() as MindMapItem, heightIncrease)
        }
    }

    private fun moveLeftFamily(item: MindMapItem, verMargin: Int) {
        val centerPos = item.y + item.measuredHeight.toFloat() / 2
        var nextTop = centerPos - item.leftTotalHeight.toFloat() / 2
        for (child in item.getLeftChild()) {
            child.y = nextTop + (child.leftTotalHeight - child.measuredHeight).toFloat() / 2
            nextTop += (child.leftTotalHeight + verMargin)
            moveLeftFamily(child, verMargin)
        }
    }

    private fun moveRightFamily(item: MindMapItem, verMargin: Int) {
        val centerPos = item.y + item.measuredHeight.toFloat() / 2
        var nextTop = centerPos - item.rightTotalHeight.toFloat() / 2
        for (child in item.getRightChild()) {
            child.y = nextTop + (child.rightTotalHeight - child.measuredHeight).toFloat() / 2
            nextTop += (child.rightTotalHeight + verMargin)
            moveRightFamily(child, verMargin)
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
}