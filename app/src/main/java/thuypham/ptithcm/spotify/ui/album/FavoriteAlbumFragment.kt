package thuypham.ptithcm.spotify.ui.album


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_albums.*
import kotlinx.android.synthetic.main.fragment_artists.*
import kotlinx.android.synthetic.main.list_empty.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.album.adapter.AlbumAdapter
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.YourMusicViewModel

class FavoriteAlbumFragment : Fragment() {

    private lateinit var viewModel: YourMusicViewModel
    private val albumAdapter by lazy {
        AlbumAdapter(mutableListOf(), this::itemAlbumClick)
    }

    private fun itemAlbumClick(id: String?) {
        val albumFragment = AlbumDetailFragment()
        val arguments = Bundle()
        arguments.putString("albumID", id)
        albumFragment.arguments = arguments
        requireActivity()
            .replaceFragment(id = R.id.frmMain, fragment = albumFragment, addToBackStack = true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders
            .of(this, Injection.provideYourMusicViewModelFactory())
            .get(YourMusicViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getListAlbum()
        bindViewModel()
        initRecyclerView()
        addEvents()
    }

    private fun addEvents() {
        swRefreshAlbum.setOnRefreshListener {
            viewModel.getListAlbum()
            swRefreshAlbum.isRefreshing = false
        }
        btnFind.setOnClickListener { showAlbumsFragment() }
    }

    private fun showAlbumsFragment() {
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = AllAlbumFragment(),
            addToBackStack = true
        )
    }

    private fun initRecyclerView() {
        rvAlbums.adapter = albumAdapter
        rvAlbums.hasFixedSize()
        rvAlbums.setItemViewCacheSize(20)
    }

    private fun bindViewModel() {
        viewModel.listAlbum.observe(requireActivity(), Observer {
            albumAdapter.addDataAlbum(it)
        })

        viewModel.networkStateAlbum.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    progressAlbum?.gone()
                    llAlbumEmpty?.show()
                }
                Status.RUNNING -> {
                    progressArtist?.show()
                    llAlbumEmpty?.gone()
                }
                Status.SUCCESS -> {
                    progressArtist?.gone()
                    llAlbumEmpty?.gone()
                }
            }
        })
    }
}
