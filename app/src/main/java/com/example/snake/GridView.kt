package com.example.snake

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View

abstract class GridView : View {
    private val LOG_TAG = GridView::class.java.name
    private val TILE_DEFAULT_SIZE = 12
    private val TILE_EMPTY = -1

    private val mPaint: Paint
    private var mTileList: Array<Bitmap?>? = null
    private var mGrid: Array<IntArray>? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {

        this.mPaint = Paint()

        val styledAttributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.GridView
        )

        mTileSize = styledAttributes.getDimensionPixelSize(
            R.styleable.GridView_tileSize,
            TILE_DEFAULT_SIZE
        )

        Log.d(LOG_TAG, "Tile size: $mTileSize")

        styledAttributes.recycle()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        this.mPaint = Paint()

        val styledAttributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.GridView
        )

        mTileSize = styledAttributes.getDimensionPixelSize(
            R.styleable.GridView_tileSize,
            TILE_DEFAULT_SIZE
        )

        Log.d(LOG_TAG, "Tile size: $mTileSize")

        styledAttributes.recycle()
    }


    protected fun resetTileList(nbTiles: Int) {
        mTileList = arrayOfNulls(nbTiles)
    }

    fun loadTile(key: Int, tile: Drawable) {
        val bitmap = Bitmap.createBitmap(
            mTileSize, mTileSize,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        tile.setBounds(0, 0, mTileSize, mTileSize)
        tile.draw(canvas)

        mTileList?.set(key,bitmap)
    }

    public override fun onDraw(canvas: Canvas) {
        Log.d(LOG_TAG, "Drawing...")

        super.onDraw(canvas)

        for (x in 0 until nbTileX) {
            for (y in 0 until nbTileY) {
                if (mGrid!![x][y] > TILE_EMPTY) {
                    canvas.drawBitmap(
                        mTileList!![mGrid!![x][y]],
                        (mOffsetX + x * mTileSize).toFloat(),
                        (mOffsetY + y * mTileSize).toFloat(),
                        mPaint
                    )
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        nbTileX = Math.floor((w / mTileSize).toDouble()).toInt()
        nbTileY = Math.floor((h / mTileSize).toDouble()).toInt()

        Log.d(LOG_TAG, "Nb tile - X: $nbTileX")
        Log.d(LOG_TAG, "Nb tile - Y: $nbTileY")

        mOffsetX = (w - mTileSize * nbTileX) / 2
        mOffsetY = (h - mTileSize * nbTileY) / 2

        mGrid = Array(nbTileX) { IntArray(nbTileY) }

        clearTiles()
        this.invalidate()
    }

    fun clearTiles() {
        for (x in 0 until nbTileX) {
            for (y in 0 until nbTileY) {
                setTile(TILE_EMPTY, x, y)
            }
        }

        this.updateTiles()
    }

    protected abstract fun updateTiles()

    fun setTile(value: Int, x: Int, y: Int) {
        mGrid!![x][y] = value
    }

    companion object {

        private var mTileSize: Int = 0
        private var mOffsetX: Int = 0
        private var mOffsetY: Int = 0

        var nbTileX: Int = 0
            protected set
        var nbTileY: Int = 0
            protected set

        fun getTileX(x: Float): Double {
            return Math.floor(((x - mOffsetX) / mTileSize).toDouble())
        }

        fun getTileY(y: Float): Double {
            return Math.floor(((y - mOffsetY) / mTileSize).toDouble())
        }
    }
}