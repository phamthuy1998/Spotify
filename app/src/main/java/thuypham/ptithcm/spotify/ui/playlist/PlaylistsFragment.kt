package thuypham.ptithcm.spotify.ui.playlist


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import thuypham.ptithcm.spotify.R

/**
 * A simple [Fragment] subclass.
 */
class PlaylistsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }


}
