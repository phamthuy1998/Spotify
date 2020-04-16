package thuypham.ptithcm.spotify.ui.album

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_all_album.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.album.adapter.AlbumAdapter
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.AlbumViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

class AllAlbumFragment : Fragment() {
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private val albumAdapter by lazy {
        AlbumAdapter(mutableListOf() ,this::itemAlbumClick)
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
        albumViewModel = ViewModelProviders
            .of(this, Injection.provideAlbumViewModelFactory())
            .get(AlbumViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory(requireActivity().application))
            .get(NowPlayingViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumViewModel.getAllListAlbum()
        initRecyclerView()
        addEvents()
        bindViewModel()
    }

    private fun addEvents() {
        swRefreshAllAlbum.setOnRefreshListener {
            albumViewModel.getAllListAlbum()
            swRefreshAllAlbum.isRefreshing = false
        }
        btnBackAllAlbum.setOnClickListener { requireActivity().onBackPressed() }
        edtSearchAlbum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                albumAdapter.search(s.toString(), {
                    llSearchAllAlbumNotFound.show()
                    albumAdapter.removeAllData()
                }, {
                    albumAdapter.addDataSearch(it)
                    llSearchAllAlbumNotFound.gone()
                })
            }
        })
    }

    private fun bindViewModel() {
        albumViewModel.listAlbum.observe(requireActivity(), Observer {
            albumAdapter.addDataAlbum(it)
        })

        albumViewModel.networkStateAlbum.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    progressAllAlbum?.gone()
                    llAlbumEmpty?.show()
                }
                Status.RUNNING -> {
                    progressAllAlbum?.show()
                    llAlbumEmpty?.gone()
                }
                Status.SUCCESS -> {
                    progressAllAlbum?.gone()
                    llAlbumEmpty?.gone()
                }
            }
        })
    }

    private fun initRecyclerView() {
        rvAllAlbum.setItemViewCacheSize(20)
        rvAllAlbum.setHasFixedSize(true)
        rvAllAlbum.adapter = albumAdapter
    }
}
