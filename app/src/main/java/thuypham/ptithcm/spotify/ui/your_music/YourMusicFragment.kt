package thuypham.ptithcm.spotify.ui.your_music


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_your_music.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.base.MyFragmentPagerAdapter
import thuypham.ptithcm.spotify.ui.album.FavoriteAlbumFragment
import thuypham.ptithcm.spotify.ui.artist.ArtistsFollowedFragment
import thuypham.ptithcm.spotify.ui.playlist.FavoritePlaylistsFragment
import thuypham.ptithcm.spotify.ui.song.FavoriteSongsFragment

class YourMusicFragment : Fragment(), ViewPager.OnPageChangeListener {
    private val artistFragment by lazy {
        ArtistsFollowedFragment()
    }
    private val albumFragment by lazy {
        FavoriteAlbumFragment()
    }
    private val songFragment by lazy {
        FavoriteSongsFragment()
    }
    private val playlistFragment by lazy {
        FavoritePlaylistsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_your_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        addButtonEventWithViewPager()
    }

    private fun addButtonEventWithViewPager() {
        btnArtist.setOnClickListener {
            viewPagerYourMusic.currentItem = 0
            btnArtist.isSelected = true
            btnAlbum.isSelected = false
            btnSongs.isSelected = false
            btnPlaylist.isSelected = false
        }
        btnAlbum.setOnClickListener {
            viewPagerYourMusic.currentItem = 1
            btnArtist.isSelected = false
            btnAlbum.isSelected = true
            btnSongs.isSelected = false
            btnPlaylist.isSelected = false
        }
        btnSongs.setOnClickListener {
            viewPagerYourMusic.currentItem = 2
            btnArtist.isSelected = false
            btnAlbum.isSelected = false
            btnSongs.isSelected = true
            btnPlaylist.isSelected = false
        }
        btnPlaylist.setOnClickListener {
            viewPagerYourMusic.currentItem = 3
            btnArtist.isSelected = false
            btnAlbum.isSelected = false
            btnSongs.isSelected = false
            btnPlaylist.isSelected = true
        }
    }

    private fun initViewPager() {
        val viewPagerAdapter = MyFragmentPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragment(artistFragment, "Artists fragment")
        viewPagerAdapter.addFragment(albumFragment, "Albums fragment")
        viewPagerAdapter.addFragment(songFragment, "Songs fragment")
        viewPagerAdapter.addFragment(playlistFragment, "Playlists fragment")
        viewPagerYourMusic.adapter = viewPagerAdapter
        viewPagerYourMusic.currentItem = 0
        btnArtist.isSelected = true
        viewPagerYourMusic.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                btnArtist.isSelected = true
                btnAlbum.isSelected = false
                btnSongs.isSelected = false
                btnPlaylist.isSelected = false
            }
            1 -> {
                btnArtist.isSelected = false
                btnAlbum.isSelected = true
                btnSongs.isSelected = false
                btnPlaylist.isSelected = false
            }
            2 -> {
                btnArtist.isSelected = false
                btnAlbum.isSelected = false
                btnSongs.isSelected = true
                btnPlaylist.isSelected = false
            }
            3 -> {
                btnArtist.isSelected = false
                btnAlbum.isSelected = false
                btnSongs.isSelected = false
                btnPlaylist.isSelected = true
            }
        }
    }
}
