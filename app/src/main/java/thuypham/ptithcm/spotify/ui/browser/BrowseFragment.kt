package thuypham.ptithcm.spotify.ui.browser


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import thuypham.ptithcm.spotify.R

class BrowseFragment : Fragment() {

    companion object {
        private var instance: BrowseFragment? = null
        fun getInstance() = instance ?: BrowseFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false)
    }


}
