package thuypham.ptithcm.spotify.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.ui.browser.BrowseFragment
import thuypham.ptithcm.spotify.util.replaceFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(id = R.id.frmMain, fragment = BrowseFragment())
    }

    fun openNav(view: View) = drawerMain.openDrawer(Gravity.LEFT)
    fun closeNav(view: View) = drawerMain.closeDrawer(Gravity.LEFT)

}