package thuypham.ptithcm.spotify.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.util.hideKeyboard
import thuypham.ptithcm.spotify.util.replaceFragment

class LoginActivity : AppCompatActivity() {

    private val landingFragment: LaunchFragment by lazy {
        LaunchFragment.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        replaceFragment(id = R.id.frmLogin, fragment = landingFragment)
    }

    fun btnCreateAccClick(view: View) =
        replaceFragment(id = R.id.frmLogin, fragment = SignUpFragment(), addToBackStack = true)

    fun btnLoginClick(view: View) =
        replaceFragment(id = R.id.frmLogin, fragment = SignInFragment(), addToBackStack = true)

    fun hideKeyBoard(view: View) = hideKeyboard()
}
