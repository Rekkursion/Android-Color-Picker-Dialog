package com.rekkursion.colorpickerdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import java.util.*
import android.content.Context.CLIPBOARD_SERVICE




class ColorPickerDialog: Dialog {
    private var mContext: Context

    private var mPickAlphaOrNot: Boolean = true

    private var mOnCancelListener: OnCancelListener? = null
    private var mOnSelectListener: OnSelectListener? = null

    private var mCancelButtonString: String = "Cancel"
    private var mSelectButtonString: String = "Select"

    private lateinit var mBtnCancel: Button
    private lateinit var mBtnSelect: Button

    private lateinit var mSkbPickRed: SeekBar
    private lateinit var mSkbPickGreen: SeekBar
    private lateinit var mSkbPickBlue: SeekBar
    private lateinit var mSkbPickAlpha: SeekBar

    private lateinit var mBtnShowPickedRedValue: Button
    private lateinit var mBtnShowPickedGreenValue: Button
    private lateinit var mBtnShowPickedBlueValue: Button
    private lateinit var mBtnShowPickedAlphaValue: Button

    private lateinit var mLlyPickAlpha: LinearLayout

    private lateinit var mBtnOrUsingHexCode: Button

    private lateinit var mImgvPreviewPickedColor: ImageView

    // constructor
    constructor(context: Context, pickAlpha: Boolean = true): super(context, R.style.ColorPickerDialogTheme) {
        mContext = context
        mPickAlphaOrNot = pickAlpha
    }

    // constructor
    constructor(
        context: Context,
        pickAlpha: Boolean = true,
        onColorPickingCancelClickListener: OnCancelListener? = null,
        onColorPickingSelectClickListener: OnSelectListener? = null
    ): super(context, R.style.ColorPickerDialogTheme) {
        mContext = context
        mPickAlphaOrNot = pickAlpha
        mOnCancelListener = onColorPickingCancelClickListener
        mOnSelectListener = onColorPickingSelectClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_color_picker_dialog)

        //按空白处不能取消动画
        setCanceledOnTouchOutside(false)

        init()
    }

    override fun onStart() {
        mBtnCancel.text = mCancelButtonString
        mBtnSelect.text = mSelectButtonString
        super.onStart()
    }

    private fun init() {
        initViews()
        initData()
        initEvents()
    }

    private fun initViews() {
        mBtnCancel = findViewById(R.id.btn_color_picker_dialog_cancel)
        mBtnSelect = findViewById(R.id.btn_color_picker_dialog_select)

        mSkbPickRed = findViewById(R.id.skb_color_picker_dialog_pick_red)
        mSkbPickGreen = findViewById(R.id.skb_color_picker_dialog_pick_green)
        mSkbPickBlue = findViewById(R.id.skb_color_picker_dialog_pick_blue)
        mSkbPickAlpha = findViewById(R.id.skb_color_picker_dialog_pick_alpha)

        mSkbPickRed.progressDrawable = ContextCompat.getDrawable(mContext, R.drawable.drawable_seek_bar_pick_red)
        mSkbPickRed.thumb = ContextCompat.getDrawable(mContext, R.drawable.ic_block_black_24dp)
        mSkbPickGreen.progressDrawable = ContextCompat.getDrawable(mContext, R.drawable.drawable_seek_bar_pick_green)
        mSkbPickGreen.thumb = ContextCompat.getDrawable(mContext, R.drawable.ic_block_black_24dp)
        mSkbPickBlue.progressDrawable = ContextCompat.getDrawable(mContext, R.drawable.drawable_seek_bar_pick_blue)
        mSkbPickBlue.thumb = ContextCompat.getDrawable(mContext, R.drawable.ic_block_black_24dp)
        mSkbPickAlpha.progressDrawable = ContextCompat.getDrawable(mContext, R.drawable.drawable_seek_bar_pick_alpha)
        mSkbPickAlpha.thumb = ContextCompat.getDrawable(mContext, R.drawable.ic_block_black_24dp)

        mBtnShowPickedRedValue = findViewById(R.id.btn_color_picker_dialog_show_red_value)
        mBtnShowPickedGreenValue = findViewById(R.id.btn_color_picker_dialog_show_green_value)
        mBtnShowPickedBlueValue = findViewById(R.id.btn_color_picker_dialog_show_blue_value)
        mBtnShowPickedAlphaValue = findViewById(R.id.btn_color_picker_dialog_show_alpha_value)

        mLlyPickAlpha = findViewById(R.id.lly_pick_alpha)

        mBtnOrUsingHexCode = findViewById(R.id.btn_color_picker_dialog_or_using_hex_code)

        mImgvPreviewPickedColor = findViewById(R.id.imgv_color_picker_dialog_preview_color)

        // no need to pick alpha
        if (!mPickAlphaOrNot)
            mLlyPickAlpha.visibility = View.GONE
    }

    private fun initData() {
        mSkbPickRed.max = 255
        mSkbPickGreen.max = 255
        mSkbPickBlue.max = 255
        mSkbPickAlpha.max = 255

        mSkbPickRed.progress = 0
        mSkbPickGreen.progress = 0
        mSkbPickBlue.progress = 0
        mSkbPickAlpha.progress = 255

        mBtnShowPickedRedValue.text = mContext.getString(R.string.str_000)
        mBtnShowPickedGreenValue.text = mContext.getString(R.string.str_000)
        mBtnShowPickedBlueValue.text = mContext.getString(R.string.str_000)
        mBtnShowPickedAlphaValue.text = mContext.getString(R.string.str_255)

        setPreviewColorAtImageView()
    }

    private fun initEvents() {
        // btn-cancel click
        mBtnCancel.setOnClickListener {
            if (mOnCancelListener != null)
                mOnCancelListener!!.onCancelClick(
                    if (mPickAlphaOrNot) mSkbPickAlpha.progress else null,
                    mSkbPickRed.progress,
                    mSkbPickGreen.progress,
                    mSkbPickBlue.progress
                )
            this.dismiss()
        }

        // btn-select click
        mBtnSelect.setOnClickListener {
            if (mOnSelectListener != null)
                mOnSelectListener!!.onSelectClick(
                    if (mPickAlphaOrNot) mSkbPickAlpha.progress else null,
                    mSkbPickRed.progress,
                    mSkbPickGreen.progress,
                    mSkbPickBlue.progress
                )
            this.dismiss()
        }

        // rgb value seek-bar change
        val onSeekBarChangeListener = object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, isUser: Boolean) {
                // change the corresponding text-view to show the value
                when (seekBar?.id) {
                    R.id.skb_color_picker_dialog_pick_red ->
                        setPickedValueText(mBtnShowPickedRedValue, progress)

                    R.id.skb_color_picker_dialog_pick_green ->
                        setPickedValueText(mBtnShowPickedGreenValue, progress)

                    R.id.skb_color_picker_dialog_pick_blue ->
                        setPickedValueText(mBtnShowPickedBlueValue, progress)

                    R.id.skb_color_picker_dialog_pick_alpha ->
                        setPickedValueText(mBtnShowPickedAlphaValue, progress)
                }

                // set the color at image-view for previewing
                setPreviewColorAtImageView()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
        }
        mSkbPickRed.setOnSeekBarChangeListener(onSeekBarChangeListener)
        mSkbPickGreen.setOnSeekBarChangeListener(onSeekBarChangeListener)
        mSkbPickBlue.setOnSeekBarChangeListener(onSeekBarChangeListener)
        mSkbPickAlpha.setOnSeekBarChangeListener(onSeekBarChangeListener)

        // color value text-views click
        val onShowingColorValueButtonClickListener = { view: View? ->
            val colorStr: String = when (view?.id) {
                R.id.btn_color_picker_dialog_show_red_value -> "Red"
                R.id.btn_color_picker_dialog_show_green_value -> "Green"
                R.id.btn_color_picker_dialog_show_blue_value -> "Blue"
                R.id.btn_color_picker_dialog_show_alpha_value -> "Alpha"
                else -> "Joseph Joestar"
            }

            // create a new edit-text
            val edtInputColorValue = EditText(mContext)
            edtInputColorValue.hint = "Range: [0, 255]"
            edtInputColorValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            edtInputColorValue.transformationMethod = object: PasswordTransformationMethod() {
                override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
                    return source!!
                }
            }

            // region build a new alert-dialog for input the value of a/r/g/b
            AlertDialog.Builder(mContext)
                .setTitle("$colorStr, current value: ${(view as TextView).text}")
                .setView(edtInputColorValue)
                .setPositiveButton("OK") { _, _ ->
                    if (edtInputColorValue.text.isEmpty())
                        return@setPositiveButton

                    val inputValue: Int = Integer.valueOf(edtInputColorValue.text.toString())
                    when (view.id) {
                        R.id.btn_color_picker_dialog_show_red_value -> mSkbPickRed.progress = inputValue
                        R.id.btn_color_picker_dialog_show_green_value -> mSkbPickGreen.progress = inputValue
                        R.id.btn_color_picker_dialog_show_blue_value -> mSkbPickBlue.progress = inputValue
                        R.id.btn_color_picker_dialog_show_alpha_value -> mSkbPickAlpha.progress = inputValue
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
            // endregion
        }
        mBtnShowPickedRedValue.setOnClickListener(onShowingColorValueButtonClickListener)
        mBtnShowPickedGreenValue.setOnClickListener(onShowingColorValueButtonClickListener)
        mBtnShowPickedBlueValue.setOnClickListener(onShowingColorValueButtonClickListener)
        mBtnShowPickedAlphaValue.setOnClickListener(onShowingColorValueButtonClickListener)

        // or-using-hex-code button
        mBtnOrUsingHexCode.setOnClickListener {
            val currentHexStringRed = String.format("%02X", Integer.valueOf(mBtnShowPickedRedValue.text.toString()))
            val currentHexStringGreen = String.format("%02X", Integer.valueOf(mBtnShowPickedGreenValue.text.toString()))
            val currentHexStringBlue = String.format("%02X", Integer.valueOf(mBtnShowPickedBlueValue.text.toString()))
            val currentHexStringAlpha = String.format("%02X", Integer.valueOf(mBtnShowPickedAlphaValue.text.toString()))

            // pick-alpha -> #ARGB, else -> #RGB
            val currentHexCode =
                if (mPickAlphaOrNot)
                    "#$currentHexStringAlpha$currentHexStringRed$currentHexStringGreen$currentHexStringBlue"
                else
                    "#$currentHexStringRed$currentHexStringGreen$currentHexStringBlue"

            // edit-text to let user enter a hex code
            val edtHexCode = EditText(mContext)
            edtHexCode.setText(currentHexCode)
            edtHexCode.setSelection(1, currentHexCode.length)

            // hex code dialog title string
            val titleStringForHexCodeDialog =
                if (mPickAlphaOrNot)
                    "Enter a hex code by the order of ARGB or RGB"
                else
                    "Enter a hex code by the order of RGB"

            // automatically show the keyboard
            val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

            // region build a new alert-dialog for hex code
            val hexCodeDialog = AlertDialog.Builder(mContext)
                .setTitle(titleStringForHexCodeDialog)
                .setView(edtHexCode)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Paste", null)
                .create()

            hexCodeDialog.setOnShowListener {
                // ok button
                val positiveButton = hexCodeDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener {
                    // get the string of edit-text
                    val str = edtHexCode.text.toString().substring(edtHexCode.text.toString().lastIndexOf("#") + 1)

                    val redHexString: String
                    val greenHexString: String
                    val blueHexString: String
                    val alphaHexString: String

                    // region deal with the hex code based on its length
                    when (str.length) {
                        // ARGB: shortened format of hex code, e.g. #F3EA, equals to #FF33EEAA
                        4 -> {
                            if (!mPickAlphaOrNot) {
                                Toast.makeText(mContext, "Wrong hex code format.", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            redHexString = str.substring(1, 2).repeat(2).toUpperCase()
                            greenHexString = str.substring(2, 3).repeat(2).toUpperCase()
                            blueHexString = str.substring(3, 4).repeat(2).toUpperCase()
                            alphaHexString = str.substring(0, 1).repeat(2).toUpperCase()
                        }

                        // ARGB: normal format of hex code
                        8 -> {
                            if (!mPickAlphaOrNot) {
                                Toast.makeText(mContext, "Wrong hex code format.", Toast.LENGTH_SHORT).show()
                                return@setOnClickListener
                            }

                            redHexString = str.substring(2, 4).toUpperCase()
                            greenHexString = str.substring(4, 6).toUpperCase()
                            blueHexString = str.substring(6, 8).toUpperCase()
                            alphaHexString = str.substring(0, 2).toUpperCase()
                        }

                        // RGB: shortened format
                        3 -> {
                            redHexString = str.substring(0, 1).repeat(2).toUpperCase()
                            greenHexString = str.substring(1, 2).repeat(2).toUpperCase()
                            blueHexString = str.substring(2, 3).repeat(2).toUpperCase()
                            alphaHexString = currentHexStringAlpha
                        }

                        // RGB: normal format
                        6 -> {
                            redHexString = str.substring(0, 2).toUpperCase()
                            greenHexString = str.substring(2, 4).toUpperCase()
                            blueHexString = str.substring(4, 6).toUpperCase()
                            alphaHexString = currentHexStringAlpha
                        }

                        // wrong format
                        else -> {
                            Toast.makeText(mContext, "Wrong hex code format.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    // endregion

                    // invalid characters check
                    val regex = "[^0-9a-fA-F]".toRegex()
                    if (redHexString.contains(regex) || greenHexString.contains(regex) || blueHexString.contains(regex) || alphaHexString.contains(regex)) {
                        Toast.makeText(mContext, "Wrong hex code format.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // convert hex codes into integer values
                    val redValue = redHexString.toInt(radix = 16)
                    val greenValue = greenHexString.toInt(radix = 16)
                    val blueValue = blueHexString.toInt(radix = 16)
                    val alphaValue = alphaHexString.toInt(radix = 16)

                    // assign them to seek-bars
                    mSkbPickRed.progress = redValue
                    mSkbPickGreen.progress = greenValue
                    mSkbPickBlue.progress = blueValue
                    mSkbPickAlpha.progress = alphaValue

                    // hide the keyboard
                    imm?.hideSoftInputFromWindow(edtHexCode.windowToken, 0)
                    hexCodeDialog.dismiss()
                }

                // cancel button
                val negativeButton = hexCodeDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setOnClickListener {
                    imm?.hideSoftInputFromWindow(edtHexCode.windowToken, 0)
                    hexCodeDialog.dismiss()
                }

                // neutral button (paste the copied hex code)
                val neutralButton = hexCodeDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                neutralButton.setOnClickListener {
                    @SuppressWarnings("deprecation")
                    val clipboardManager = mContext.getSystemService(CLIPBOARD_SERVICE) as android.text.ClipboardManager?
                    if (clipboardManager == null)
                        return@setOnClickListener

                    val copiedString = clipboardManager.text.toString().trimStart('#')
                    edtHexCode.setText("#$copiedString")
                }
            }

            hexCodeDialog.show()
            // endregion
        }
    }

    private fun setPreviewColorAtImageView() {
        val currentColorInt: Int = Color.argb(
            mSkbPickAlpha.progress,
            mSkbPickRed.progress,
            mSkbPickGreen.progress,
            mSkbPickBlue.progress
        )
        mImgvPreviewPickedColor.setImageDrawable(ColorDrawable(currentColorInt))
    }

    private fun setPickedValueText(btn: Button?, value: Int) {
        btn?.text = String.format(Locale.CANADA, "%03d", value)
    }

    // set on-cancel-listener
    fun setOnCancelListener(cancelBtnStr: String?, onCancelListener: ColorPickerDialog.OnCancelListener?): ColorPickerDialog {
        mCancelButtonString = cancelBtnStr ?: "NULL"
        mOnCancelListener = onCancelListener
        return this
    }

    // set on-select-listener
    fun setOnSelectListener(selectBtnStr: String?, onSelectListener: ColorPickerDialog.OnSelectListener?): ColorPickerDialog {
        mSelectButtonString = selectBtnStr ?: "NULL"
        mOnSelectListener = onSelectListener
        return this
    }

    // set pick-alpha-or-not
    fun setPickAlpha(pickAlpha: Boolean): ColorPickerDialog {
        mPickAlphaOrNot = pickAlpha
        return this
    }

    // interface for cancel button
    interface OnCancelListener {
        fun onCancelClick(a: Int?, r: Int, g: Int, b: Int)
    }

    // interface for select button
    interface OnSelectListener {
        fun onSelectClick(a: Int?, r: Int, g: Int, b: Int)
    }
}