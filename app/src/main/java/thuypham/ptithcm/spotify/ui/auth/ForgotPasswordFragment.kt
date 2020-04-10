package thuypham.ptithcm.spotify.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentForgotPasswordBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.util.invisible
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.visible
import thuypham.ptithcm.spotify.viewmodel.AuthViewModel


class ForgotPasswordFragment : Fragment() {

    private val userViewModel: AuthViewModel by lazy {
        ViewModelProviders
            .of(requireActivity(), Injection.provideAccViewModelFactory())
            .get(AuthViewModel::class.java)
    }


    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@ForgotPasswordFragment
        binding.userViewModel = userViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addEvents()
        bindViewModel()
    }

    private fun bindViewModel() {
        userViewModel.senMailStatus.observe(requireActivity(), Observer {
            if (it == true) requireActivity().replaceFragment(id = R.id.frmLogin, fragment =  SignInFragment())
        })
        userViewModel.networkSendMail.observe(this, Observer {
            when (it.status) {
                Status.FAILED -> {
                    binding.progressbarForgotPw.invisible()
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                    binding.btnSendMailPw.isEnabled = true
                    binding.btnSendMailPw.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.colorOrange)
                }
                Status.RUNNING -> {
                    binding.progressbarForgotPw.visible()
                    binding.btnSendMailPw.isEnabled = false
                    binding.btnSendMailPw.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.colorGrayBtnEnable
                        )
                }
                Status.SUCCESS -> {
                    binding.progressbarForgotPw.invisible()
                }
            }
        })
    }

    private fun addEvents() {
        binding.btnBackForgotPw.setOnClickListener { requireActivity().onBackPressed() }
    }

}