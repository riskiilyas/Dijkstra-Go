package com.keecoding.dijkstrago

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class PanelView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
/*
      0 = Empty
     -1 = Blocks
      1 = Start
      2 = End
      3 = Visited
      4 = Path
*/

    private var mMatrix = mutableListOf<MutableList<Int>>()

    private var blockStroke: Paint = Paint()
    private var blockBlock: Paint = Paint()
    private var blockStart: Paint = Paint()
    private val blockEnd: Paint = Paint()
    private val blockVisited: Paint = Paint()
    private val blockPath: Paint = Paint()

    private var mCanvas: Canvas? = null

    private var rectStart: Rect? = null
    private var rectEnd: Rect? = null
    private var startX = -1
    private var startY = -1

    private var mode = MODE_BLOCK
    private var hrBlocks = 0
    private var vrBlocks = 0
    private var isProgressing = false
    private var isFinished = false

    init {
        blockStroke.strokeWidth = 2f
        blockStroke.style = Paint.Style.STROKE
        blockStroke.color = Color.BLACK

        blockBlock.color = Color.BLACK
        blockBlock.style = Paint.Style.FILL

        blockStart.color = Color.GREEN
        blockStart.style = Paint.Style.FILL

        blockEnd.color = Color.RED
        blockEnd.style = Paint.Style.FILL

        blockVisited.color = Color.CYAN
        blockVisited.style = Paint.Style.FILL

        blockPath.color = Color.LTGRAY
        blockPath.style = Paint.Style.FILL

        for (i in 0 until 16) {
            mMatrix.add(mutableListOf())
        }
        initMatrix()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mCanvas = canvas
        hrBlocks = measuredWidth / 8
        vrBlocks = measuredHeight / 16

        for (y in 0 until 16) {
            for (x in 0 until 8) {
                val rect = Rect(x * hrBlocks, y * vrBlocks, (x + 1) * hrBlocks, (y + 1) * vrBlocks)
                canvas?.drawRect(rect, blockStroke)
                when (mMatrix[y][x]) {
                    -1 -> canvas?.drawRect(rect, blockBlock)
                    1 -> canvas?.drawRect(rect, blockStart)
                    2 -> canvas?.drawRect(rect, blockEnd)
                    3 -> canvas?.drawRect(rect, blockVisited)
                    4 -> canvas?.drawRect(rect, blockPath)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isProgressing && !isFinished) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> redrawRect(event.x, event.y)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun redrawRect(x: Float, y: Float) {
        for (i in 0 until 16) {
            for (j in 0 until 8) {
                if ((x > j * hrBlocks && x < (j + 1) * hrBlocks) && (y > i * vrBlocks && y < (i + 1) * vrBlocks)) {
                    val rect =
                        Rect(j * hrBlocks, i * vrBlocks, (j + 1) * hrBlocks, (i + 1) * vrBlocks)
                    if (mMatrix[i][j] == 0) {
                        when (mode) {
                            MODE_BLOCK -> mMatrix[i][j] = -1
                            MODE_START -> {
                                startX = j
                                startY = i
                                removeDuplicate(MODE_START)
                                mMatrix[i][j] = 1
                                rectStart = rect
                                mode = MODE_BLOCK
                            }
                            MODE_END -> {
                                removeDuplicate(MODE_END)
                                mMatrix[i][j] = 2
                                rectEnd = rect
                                mode = MODE_BLOCK
                            }
                        }
                        invalidate()
                    }
                }
            }
        }
    }

    private fun removeDuplicate(mode: Int) {
        for (i in 0 until 16) {
            for (j in 0 until 8) {
                if (mMatrix[i][j] == mode) mMatrix[i][j] = 0
            }
        }
    }

    fun clear() {
        isFinished = false
        rectStart = null
        rectEnd = null
        mode = MODE_BLOCK
        for (i in 0 until 16) {
            for (j in 0 until 8) {
                mMatrix[i][j] = 0
            }
        }
        invalidate()
    }

    private fun initMatrix() {
        for (i in 0 until 16) {
            for (j in 0 until 8) {
                mMatrix[i].add(0)
            }
        }
    }

    fun isReady() = rectStart != null && rectEnd != null

    fun play() {
        mode = MODE_BLOCK
        isProgressing = true
    }

    fun finishMove(mMatrix: MutableList<MutableList<Int>>) {
        isFinished = true
        this.mMatrix = mMatrix
        this.mMatrix.forEachIndexed { index1, mutableList ->
            mutableList.forEachIndexed { index2, _ ->
                if (mMatrix[index1][index2] == 3) {
                    mMatrix[index1][index2] = 0
                }
            }
        }
        invalidate()
        isProgressing = false
        mMatrix[startY][startX] = 1
        invalidate()
    }

    fun cantFind() {
        isProgressing = false
        isFinished = true
    }

    fun setMode(mode: Int) {
        this.mode = mode
    }

    fun provideMatrix() = mMatrix

    fun callback(mMatrix: MutableList<MutableList<Int>>) {
        Log.d("DDD", "onCreate: CALLBACK")
        this.mMatrix = mMatrix
        invalidate()
    }

    companion object {
        const val MODE_BLOCK = 0
        const val MODE_START = 1
        const val MODE_END = 2
    }

}