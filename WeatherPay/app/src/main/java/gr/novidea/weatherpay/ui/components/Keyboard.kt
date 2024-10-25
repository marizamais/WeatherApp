package gr.novidea.weatherpay.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import gr.novidea.weatherpay.R

/**
 * TODO: document your custom view class.
 */
class Keyboard : ConstraintLayout, View.OnClickListener {

    private var _keyFontSize: Float = 0f // TODO: use a default from R.dimen...

    /**
     * In the example view, this dimension is the font size.
     */
    private var keyFontSize: Float
        get() = _keyFontSize
        set(value) {
            _keyFontSize = value
            updateKeys()
        }

    interface OnKeyClickedListener {
        fun onKeyClicked(key: String)
    }

    private var listener: OnKeyClickedListener? = null

    fun setOnKeyClickedListener(listener: OnKeyClickedListener) {
        this.listener = listener
    }

    private var keys: Array<View>? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.Keyboard, defStyle, 0
        )

        _keyFontSize = a.getDimension(
            R.styleable.Keyboard_keyFontSize, keyFontSize
        )

        a.recycle()

        inflate(context, R.layout.keyboard, this)

        keys = arrayOf(
            findViewById(R.id.key_0),
            findViewById(R.id.key_1),
            findViewById(R.id.key_2),
            findViewById(R.id.key_3),
            findViewById(R.id.key_4),
            findViewById(R.id.key_5),
            findViewById(R.id.key_6),
            findViewById(R.id.key_7),
            findViewById(R.id.key_8),
            findViewById(R.id.key_9),
            findViewById(R.id.key_clear),
            findViewById(R.id.key_ok)
        )

        keys!!.forEach { it.setOnClickListener(this) }
    }

    private fun updateKeys() {
        keys!!.forEach {
            (it as TextView).textSize = keyFontSize
        }
    }

    override fun onClick(v: View?) {
        if (v !is View) return

        when (v.id) {

            R.id.key_clear -> {
                listener?.onKeyClicked("clear")
            }

            R.id.key_ok -> {
                listener?.onKeyClicked("ok")
            }

            else -> {
                listener?.onKeyClicked((v as TextView).getText().toString())
            }
        }
    }


}