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
    private val minZoom = 1.0f
    private val maxZoom = 4.0f
    private var scale = 1f

    // Parameters for zooming.
    private val start = PointF()
    private val mid = PointF()
    private var oldDist = 1f
    private var distanceX = 0f
    private var distanceY = 0f

    private var contentSize: RectF? = null

    // Matrices used to move and zoom image.
    private val mMatrix = Matrix()
    private val matrixInverse: Matrix = Matrix()
    private val savedMatrix: Matrix = Matrix()

    private val containerRect = Rect()
    private val childRect = Rect()

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setWillNotDraw(false)
        mScaleGestureDetector = ScaleGestureDetector(context, mScaleGestureListener)
        mGestureDetector = GestureDetectorCompat(context, mGestureListener)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val values = FloatArray(9)
        mMatrix.getValues(values)
        canvas.save()
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        //mDispatchTouchEventWorkingArray[0] = ev.getX();
        //mDispatchTouchEventWorkingArray[1] = ev.getY();
        //mDispatchTouchEventWorkingArray = screenPointsToScaledPoints(mDispatchTouchEventWorkingArray);
        //ev.setLocation(mDispatchTouchEventWorkingArray[0], mDispatchTouchEventWorkingArray[1]);
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mMatrix.set(savedMatrix)
        var gestureDetected = mGestureDetector!!.onTouchEvent(event)
        if (event.pointerCount > 1) {
            gestureDetected = mScaleGestureDetector!!.onTouchEvent(event) or gestureDetected
            if (checkScaleBounds()) {
                mMatrix.postScale(scale, scale, mid.x, mid.y)
            }
        }
        mMatrix.invert(matrixInverse)
        savedMatrix.set(mMatrix)
        invalidate()
        return gestureDetected
    }

    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private val mScaleGestureListener: OnScaleGestureListener =
        object : SimpleOnScaleGestureListener() {
            /**
             * This is the active focal point in terms of the viewport. Could be a local
             * variable but kept here to minimize per-frame allocations.
             */
            override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
                oldDist = scaleGestureDetector.currentSpan
                if (oldDist > 10f) {
                    savedMatrix.set(mMatrix)
                    mid[scaleGestureDetector.focusX] = scaleGestureDetector.focusY
                }
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {}
            override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
                scale = scaleGestureDetector.scaleFactor
                return true
            }
        }

    /**
     * The gesture listener, used for handling simple gestures such as double touches, scrolls,
     * and flings.
     */
    private val mGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            savedMatrix.set(mMatrix)
            start[event.x] = event.y
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {
            setupTranslation(dX, dY)
            mMatrix.postTranslate(distanceX, distanceY)
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            //fling((int) -velocityX, (int) -velocityY);
            return true
        }
    }

    private fun checkScaleBounds(): Boolean {
        val values = FloatArray(9)
        mMatrix.getValues(values)
        val sx = values[Matrix.MSCALE_X] * scale
        val sy = values[Matrix.MSCALE_Y] * scale
        return sx > minZoom && sx < maxZoom && sy > minZoom && sy < maxZoom
    }

    private fun setupTranslation(dX: Float, dY: Float) {
        distanceX = -1 * dX
        distanceY = -1 * dY
        if (contentSize != null) {
            val values = FloatArray(9)
            mMatrix.getValues(values)
            val totX = values[Matrix.MTRANS_X] + distanceX
            val totY = values[Matrix.MTRANS_Y] + distanceY
            val sx = values[Matrix.MSCALE_X]
            val viewableRect = Rect()
            this@FlexibleLayout.getDrawingRect(viewableRect)
            val offscreenWidth: Float =
                contentSize!!.width() - (viewableRect.right - viewableRect.left)
            val offscreenHeight: Float =
                contentSize!!.height() - (viewableRect.bottom - viewableRect.top)
            val maxDx = (contentSize!!.width() - contentSize!!.width() / sx) * sx
            val maxDy = (contentSize!!.height() - contentSize!!.height() / sx) * sx
            if (totX > 0 && distanceX > 0) {
                distanceX = 0f
            }
            if (totY > 0 && distanceY > 0) {
                distanceY = 0f
            }
            if (totX * -1 > offscreenWidth + maxDx && distanceX < 0) {
                distanceX = 0f
            }
            if (totY * -1 > offscreenHeight + maxDy && distanceY < 0) {
                distanceY = 0f
            }
        }
    }

    fun setContentSize(width: Float, height: Float) {
        contentSize = RectF(0f, 0f, width, height)
    }

    fun addPrimaryItem(item: MindMapItem) {
        item.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        item.gravity = CENTER_IN_PARENT
        this.gravity = Gravity.CENTER

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
        Log.v("flex", "${parentItem.getItemText()} : ${parentItem.rightTotalHeight}")
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