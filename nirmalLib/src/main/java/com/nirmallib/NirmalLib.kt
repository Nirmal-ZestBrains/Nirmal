package com.nirmallib

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer
import java.io.Serializable

object NirmalLib {

/*------------------------------------------------------------------------------------------------*/
    /* Ex:
    recyclerView.withSimpleAdapter(dummyData, R.layout.item_recipe) { data ->
        itemView.recipe_img.setImageResource(data.drawable)
        itemView.recipe_name.text = data.name
    } */

    abstract class BaseRecyclerAdapter<Data : Any>(protected var dataList: List<Data>) : RecyclerView.Adapter<BaseViewHolder<Data>>() {
        abstract val layoutItemId: Int
        override fun getItemCount(): Int = dataList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Data> = BaseViewHolder(parent, layoutItemId)
    }

    class BaseViewHolder<in T>(parent: ViewGroup, @LayoutRes layoutId: Int) : RecyclerView.ViewHolder(parent.inflater(layoutId)), LayoutContainer {
        override val containerView: View
            get() = itemView
    }

    fun ViewGroup.inflater(layoutRes: Int): View = LayoutInflater.from(context).inflate(layoutRes, this, false)

    class SimpleRecyclerAdapter<RecyclerData : Any>(data: List<RecyclerData>, @LayoutRes layoutID: Int, private val onBindView: BaseViewHolder<RecyclerData>.(data: RecyclerData) -> Unit) : BaseRecyclerAdapter<RecyclerData>(data) {
        override val layoutItemId: Int = layoutID
        override fun onBindViewHolder(holder: BaseViewHolder<RecyclerData>, position: Int) {
            holder.onBindView(dataList[position])
        }
    }

    fun <T : Any> RecyclerView.withSimpleAdapter(dataList: List<T>, @LayoutRes layoutID: Int, onBindView: BaseViewHolder<T>.(data: T) -> Unit): SimpleRecyclerAdapter<T> {
        val recyclerAdapter = SimpleRecyclerAdapter(dataList, layoutID, onBindView)
        adapter = recyclerAdapter
        return recyclerAdapter
    }
/*------------------------------------------------------------------------------------------------*/

    /* used lazy function */
    
    /* val button: Button by lazy { findViewById<Button>(R.id.button) }   */


/*------------------------------------------------------------------------------------------------*/
/*------------------------------------------------------------------------------------------------*/
/*------------------------------------------------------------------------------------------------*/

    // isFinish-> 0 = No action | 1 = finish current activity | 2= Finish all previous activities
    inline fun <reified T : Activity> Context.start(isFinish: Int = 0, requestCode: Int = 0, vararg params: Pair<String, Any?>) {
        this as Activity
        val intent = Intent(this, T::class.java).apply {
            params.forEach {
                when (val value = it.second) {
                    is Int -> putExtra(it.first, value)
                    is String -> putExtra(it.first, value)
                    is Double -> putExtra(it.first, value)
                    is Float -> putExtra(it.first, value)
                    is Boolean -> putExtra(it.first, value)
                    is Serializable -> putExtra(it.first, value)
                    else -> throw IllegalArgumentException("Wrong param type!")
                }
                return@forEach
            }
        }

        if (requestCode == 0 && isFinish == 2) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        if (requestCode == 0) startActivity(intent) else startActivityForResult(intent, requestCode)

        if (isFinish == 1) finish()
    }

/*------------------------------------------------------------------------------------------------*/

    /* *
     * Alert dialog
     *    showAlertDialog {
     *         setTitle("Greet")
     *         setMessage("Welcome again, want coffee?")
     *         positiveButton("Yes") {}
     *         negativeButton {}
     *    }
     * */
    fun Context.showAlertDialog(dialogBuilder: AlertDialog.Builder.() -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.dialogBuilder()
        val alertDialog = builder.create()

        alertDialog.show()
    }

/*------------------------------------------------------------------------------------------------*/

    /* *
     * Custom Dialog
     *    showDialog(R.layout.dialog_confirmation) {
     *       setWidth(0.8f) // this is not compulsory it will take default 0.8f
     *    }
     * */

    internal fun Context.showDialog(layoutResourceId: Int, dialogBuilder: Dialog.() -> Unit) {
        Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(layoutResourceId)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWidth(0.8f)
            dialogBuilder()
            show()
        }
    }

    internal fun Dialog.setWidth(width: Float = 0.8f) {
        this.window?.setLayout((Resources.getSystem().displayMetrics.widthPixels * width).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

/*------------------------------------------------------------------------------------------------*/

    /* *
     * To get Color, Resource, String from xml or res folder
     * getStr :  getStr(R.string.your_string)
     * getRes :  getRes(R.drawable.your_drawable)
     * getClr :  getClr(R.color.your_color)
     * getDimen : getDimen(R.dimen.your_dimension)
     * */

    fun Context.getStr(@StringRes id: Int) = resources.getString(id)

    fun Context.getRes(@DrawableRes id: Int) = ResourcesCompat.getDrawable(resources, id, theme)!!

    fun Context.getClr(@ColorRes id: Int): Int = ResourcesCompat.getColor(resources, id, theme)

    fun Context.getClrStateList(@ColorRes id: Int) = ColorStateList.valueOf(getClr(id))

    fun Context.getDimen(@DimenRes id: Int) = resources.getDimension(id)

    fun Context.getFont(id: Int) = ResourcesCompat.getFont(this, id)


/*------------------------------------------------------------------------------------------------*/
    /**
     * To hide keyboard
     * Usage :  hideKeyboard()   or   editText.hideKeyboard()   or    editText.showKeyboard()
     * */

    fun View.showKeyboard() {
        this.requestFocus()
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun Activity.hideKeyboard() {
        val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

/*------------------------------------------------------------------------------------------------*/

    /**
     * To visible and gone view
     * Usage :  to Visible_Gone_Invisible view : ex:- yourView.visible()  same as ex:-  yourView.gone()  or ex:-  yourView.invisible()
     *          to setVisible__setInVisible : yourView.setVisible(true)  same as ex:- yourView.setInVisible(true)
     * */

    internal fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    fun View.setVisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun View.setInVisible(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

/*------------------------------------------------------------------------------------------------*/

    /**
     * To get string from components text directly
     * Usage : yourEditText.asString()
     * */

    fun View.asString(): String {
        return when (this) {
            is AppCompatEditText -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
            is AppCompatTextView -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
            is AutoCompleteTextView -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
            is MaterialButton -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
            is MaterialTextView -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
            else -> ""
        }
    }

/*------------------------------------------------------------------------------------------------*/

    fun Context.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, length).show()
    }

/*------------------------------------------------------------------------------------------------*/
    /* *
     * Load glide images
     * Usage : imageview.loadImage(  url = your_url_or_string, placeDrawable = your_custom_place_holder
     *          storeMemory = if_you_want_to_store_its_cache_then_pass_true_otherwise_don't_need_pass_it   )
     * */

    fun ImageView.loadImage(imageUrl: Any?, placeDrawable: Drawable, storeMemory: Boolean = false) {
        Glide.with(this)
            .applyDefaultRequestOptions(RequestOptions().placeholder(placeDrawable).skipMemoryCache(!storeMemory).diskCacheStrategy(if (storeMemory) DiskCacheStrategy.ALL else DiskCacheStrategy.NONE))
            .load(imageUrl)
            .into(this)
    }

/*------------------------------------------------------------------------------------------------*/

    //showType=>  1 =  internal error sniped | 2= Toast show
    fun Context.giveError(editText: EditText? = null, error: String, showType: Int = 0) {
        if (showType == 1) editText?.error = error else showToast(error)
        editText?.requestFocus()
    }

/*------------------------------------------------------------------------------------------------*/

    /* *
     * To get desired value for given VALUE whether it is null or not
     * Usage : textMsg.toString().trim().ifNotNullOrElse({ it }, { "" }
     * */

    inline fun <T : Any, R> T?.ifNotNullOrElse(ifNotNullPath: (T) -> R, elsePath: () -> R) =
        let { if (it == null) elsePath() else ifNotNullPath(it) }

    inline fun <E : Any, T : Collection<E>> T?.withNotNullNorEmpty(func: T.() -> Unit) {
        if (this != null && this.isNotEmpty()) {
            with(this) { func() }
        }
    }

/*------------------------------------------------------------------------------------------------*/
}