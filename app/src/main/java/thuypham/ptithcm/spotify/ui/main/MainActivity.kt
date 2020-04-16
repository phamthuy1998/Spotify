package thuypham.ptithcm.spotify.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity.LEFT
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.databinding.ActivityMainBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.auth.LoginActivity
import thuypham.ptithcm.spotify.ui.browser.BrowseFragment
import thuypham.ptithcm.spotify.ui.song.BottomNowPlayingFragment
import thuypham.ptithcm.spotify.ui.your_music.YourMusicFragment
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import thuypham.ptithcm.spotify.viewmodel.ProfileViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        // show browser fragment
        addFragment(id = R.id.frmMain, fragment = BrowseFragment())
        // Bottom playing fragment
        addFragment(id = R.id.frmNowPlaying, fragment = BottomNowPlayingFragment())
        bindViewModelProfile()
        initViews()
    }

    private fun setUpViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        profileViewModel = ViewModelProviders
            .of(this, Injection.provideProfileViewModelFactory())
            .get(ProfileViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(this, Injection.provideNowPlayingViewModelFactory(application))
            .get(NowPlayingViewModel::class.java)
//        nowPlayingViewModel.getAllSongInDb()
    }

    private fun bindViewModelProfile() {
        profileViewModel.userInfo.observe(this, Observer {
            if (it != null) binding.user = it
        })
        nowPlayingViewModel.getAllSongInDb()?.observe(this, Observer { listSong ->
            initBottomNowPlaying(listSong)
        })

        nowPlayingViewModel.isShowFragmentNowPlaying.observe(this, Observer { isShowNowPlaying ->
            if (isShowNowPlaying) binding.frmNowPlaying.gone()
            else binding.frmNowPlaying.show()
        })
    }

    private fun initBottomNowPlaying(listSong: List<Song>?) {
        if (listSong?.size != 0){
            frmNowPlaying.show()
            nowPlayingViewModel.listSongDb.value = listSong
        }
        else frmNowPlaying.gone()
    }

    private fun initViews() {
        // Lock drawer
        drawerMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun openNav(view: View) {
        drawerMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerMain.openDrawer(LEFT)
    }

    private fun closeNav() {
        drawerMain.closeDrawer(LEFT)
        drawerMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun onSetting(view: View) {
        closeNav()
        replaceFragment(id = R.id.frmMain, fragment = BrowseFragment(), addToBackStack = true)
    }

    fun onShowProfile(view: View) {
        closeNav()
        replaceFragment(id = R.id.frmMain, fragment = BrowseFragment())
    }

    fun onShowBrowse(view: View) {
        closeNav()
        replaceFragment(id = R.id.frmMain, fragment = BrowseFragment())
    }

    fun onShowYourMusic(view: View) {
        closeNav()
        replaceFragment(id = R.id.frmMain, fragment = YourMusicFragment())
    }

    fun logOut(view: View) {
        showDialogConfirmSignOut()
    }

    private fun showDialogConfirmSignOut() {
        val builder = AlertDialog.Builder(this)
        val optionDialog = builder.create()
        with(builder)
        {
            setMessage(getString(R.string.titleDialogSignOut))
            setPositiveButton(getString(R.string.sign_out)) { dialog, id ->
                profileViewModel.signOut()
                startActivity(LoginActivity())
                finish()
            }
            setNegativeButton(getString(android.R.string.cancel)) { dialog, id ->
                optionDialog.dismiss()
            }
            show()
        }
    }

}

