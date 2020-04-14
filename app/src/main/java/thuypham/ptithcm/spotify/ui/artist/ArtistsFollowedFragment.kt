package thuypham.ptithcm.spotify.ui.artist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_artists.*
import kotlinx.android.synthetic.main.list_empty.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.artist.adapter.ArtistAdapter
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.YourMusicViewModel

class ArtistsFollowedFragment : Fragment() {

    private lateinit var viewModel: YourMusicViewModel
    private val artistAdapter by lazy {
        ArtistAdapter(mutableListOf(),this::itemArtistClick)
    }

    private fun itemArtistClick(id: String?) {
        val artistFragment = ArtistDetailFragment()
        val arguments = Bundle()
        arguments.putString("artistID", id)
        artistFragment.arguments = arguments
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = artistFragment,
            addToBackStack = true
        )
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
        return inflater.inflate(R.layout.fragment_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getListArtist()
        bindViewModel()
        initRecyclerView()
        addEvents()
    }

    private fun addEvents() {
        swRefreshArtist.setOnRefreshListener {
            viewModel.getListArtist()
            swRefreshArtist.isRefreshing = false
        }
        btnFind.setOnClickListener {showArtistsFragment() }

    }

    private fun showArtistsFragment() {
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = ArtistsFragment(),
            addToBackStack = true
        )
    }

    private fun initRecyclerView() {
        rvArtist.adapter = artistAdapter
        rvArtist.hasFixedSize()
        rvArtist.setItemViewCacheSize(20)
    }

    private fun bindViewModel() {
        viewModel.listArtist.observe(requireActivity(), Observer {
            artistAdapter.addDataArtist(it)
        })

        viewModel.networkStateArtist.observe(requireActivity(), Observer {
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
}
