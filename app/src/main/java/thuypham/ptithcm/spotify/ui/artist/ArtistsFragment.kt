package thuypham.ptithcm.spotify.ui.artist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_artists2.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.artist.adapter.ArtistAdapter
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.hideKeyboard
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.ArtistViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

class ArtistsFragment : Fragment() {

    private lateinit var artistViewModel: ArtistViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private val artistAdapter by lazy {
        ArtistAdapter(mutableListOf(),this::onItemArtistClick)
    }

    private fun onItemArtistClick(artistId: String?) {
        requireActivity().hideKeyboard()
        val artistFragment = ArtistDetailFragment()
        val arguments = Bundle()
        arguments.putString("artistID", artistId)
        artistFragment.arguments = arguments
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = artistFragment,
            addToBackStack = true
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artistViewModel = ViewModelProviders
            .of(this, Injection.provideArtistViewModelFactory())
            .get(ArtistViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory())
            .get(NowPlayingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artists2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        artistViewModel.getAllArtist()
        initRecyclerView()
        addEvents()
        bindViewModel()
    }

    private fun addEvents() {
        swRefreshAllArtist.setOnRefreshListener {
            requireActivity().hideKeyboard()
            artistViewModel.getAllArtist()
            swRefreshAllArtist.isRefreshing = false
        }
        btnBackAllArtist.setOnClickListener {
            requireActivity().hideKeyboard()
            requireActivity().onBackPressed()
        }
        edtSearchArtist.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                artistAdapter.search(s.toString(), {
                    llSearchArtistNotFound.show()
                    artistAdapter.removeAllData()
                }, {
                    artistAdapter.addDataSearch(it)
                    llSearchArtistNotFound.gone()
                })
            }
        })
    }

    private fun bindViewModel() {
        artistViewModel.listAllArtist.observe(requireActivity(), Observer {
            artistAdapter.addDataArtist(it)
        })

        artistViewModel.statusListAllArtist.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    progressArtist?.gone()
                    llArtistEmpty?.show()
                }
                Status.RUNNING -> {
                    progressArtist?.show()
                    llArtistEmpty?.gone()
                }
                Status.SUCCESS -> {
                    progressArtist?.gone()
                    llArtistEmpty?.gone()
                }
            }
        })
    }

    private fun initRecyclerView() {
        rvAllArtist.setItemViewCacheSize(20)
        rvAllArtist.setHasFixedSize(true)
        rvAllArtist.adapter = artistAdapter
    }

}
