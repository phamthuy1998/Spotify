package thuypham.ptithcm.spotify.ui.song


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.databinding.FragmentBottomNowPlayingBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.util.SONG
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

class BottomNowPlayingFragment : Fragment() {

    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var binding: FragmentBottomNowPlayingBinding

    private var song: Song? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nowPlayingViewModel = ViewModelProviders
            .of(
                requireActivity(),
                Injection.provideNowPlayingViewModelFactory(requireActivity().application)
            )
            .get(NowPlayingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        addEvent()
    }

    private fun addEvent() {
        binding.layoutSongPlaying.setOnClickListener {
            showSongFragment()
        }
    }

    private fun showSongFragment() {
        val nowPlayingFragment = NowPlayingFragment()
        val arguments = Bundle()
        arguments.putParcelable(SONG, song)
        nowPlayingFragment.arguments = arguments
        activity.replaceFragment(
            id = R.id.frmMain,
            fragment = nowPlayingFragment,
            tag = "NowPlaying",
            addToBackStack = true
        )
    }


    private fun bindViewModel() {
        nowPlayingViewModel.songPlaying.observe(this, Observer { songInfo ->
            songInfo.let {
                song = songInfo
                binding.song = it
            }
        })
    }

}
