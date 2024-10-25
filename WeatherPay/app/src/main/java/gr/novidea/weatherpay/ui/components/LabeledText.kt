package gr.novidea.weatherpay.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import gr.novidea.weatherpay.R

/**
 * TODO: document your custom view class.
 */
class LabeledText : ConstraintLayout {

    private lateinit var labelView: TextView
    private lateinit var valueView: TextView

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
            attrs, R.styleable.LabeledText, defStyle, 0
        )

        inflate(context, R.layout.labeled_text, this)

        labelView = findViewById(R.id.label)
        valueView = findViewById(R.id.value)

        try {

            setLabel(a.getString(R.styleable.LabeledText_label).toString())
            setValue(a.getString(R.styleable.LabeledText_value).toString())

        } finally {
            a.recycle()
        }
    }

    fun setValue(value: String) {
        valueView.text = value
    }

    fun setLabel(label: String) {
        labelView.text = label
    }
}