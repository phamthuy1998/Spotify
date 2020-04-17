package thuypham.ptithcm.spotify.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import thuypham.ptithcm.spotify.R
import java.util.*

fun randomPositionSong(min: Int, max: Int): Int {
    val random = Random()
    return random.nextInt((max - min) + 1) + min
}

fun Activity?.replaceFragment(
    @IdRes id: Int = R.id.frmLogin,
    fragment: Fragment,
    tag: String? = null,
    addToBackStack: Boolean = false
) {
    val compatActivity = this as? AppCompatActivity ?: return
    compatActivity.supportFragmentManager.beginTransaction()
        .apply {
            replace(id, fragment, tag)
            if (addToBackStack) {
                addToBackStack(null)
            }
            commit()
        }
}

fun Activity?.removeFragment(tag: String) {
    val compatActivity = this as? AppCompatActivity ?: return
    val fragment = compatActivity.supportFragmentManager.findFragmentByTag(tag)
    if (fragment != null) supportFragmentManager.beginTransaction().remove(fragment).commit()
}

fun Activity?.addFragment(
    @IdRes id: Int = R.id.frmLogin,
    fragment: Fragment,
    tag: String? = null,
    addToBackStack: Boolean = true
) {
    val compatActivity = this as? AppCompatActivity ?: return
    compatActivity.supportFragmentManager.beginTransaction()
        .apply {
            add(id, fragment, tag)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            if (addToBackStack) {
                addToBackStack(null)
            }
            commit()
        }
}

fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Activity.showKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Context.startActivity(activity: Activity) =
    Intent(this, activity::class.java).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }

fun View.show() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}


fun isValidEmail(input: String): Boolean {
    return input.trim().isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(input).matches()
}

fun isValidUsername(input: String): Boolean {
    return input.trim().isNotEmpty() && Patterns.USER_NAME.matcher(input).matches()
}

fun isValidPassword(input: String): Boolean {
    return input.trim().isNotEmpty() && Patterns.PASSWORD.matcher(input).matches()
}

fun isValidPhoneNumber(input: String): Boolean {
    return input.trim().isNotEmpty() && Patterns.PHONE_NUMBER.matcher(input).matches()
}

fun isValidUrl(input: String): Boolean {
    return input.trim().isNotEmpty() && Patterns.URL.matcher(input).matches()
}


fun EditText.getTextTrim(): String {
    return text.trim().toString()
}

