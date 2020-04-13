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
import thuypham.ptithcm.spotify.databinding.FragmentLoginBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.main.MainActivity
import thuypham.ptithcm.spotify.util.invisible
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.startActivity
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.AuthViewModel

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: AuthViewModel by lazy {
        ViewModelProviders
            .of(requireActivity(), Injection.provideAccViewModelFactory())
            .get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this@SignInFragment
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        addEvents()
    }

    private fun addEvents() {
        binding.btnBackSignIn.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnForgotPassword.setOnClickListener {
            requireActivity().replaceFragment(
                id = R.id.frmLogin,
                fragment = ForgotPasswordFragment(),
                addToBackStack = true
            )
        }
    }

    private fun bindViewModel() {
        viewModel.logInStatus?.observe(requireActivity(), Observer {
            if (it == true) {
                requireActivity().startActivity(MainActivity())
                requireActivity().finish()
            }
        })
        viewModel.networkLogin.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> {
                    binding.progressbarSignIn.invisible()
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                    binding.btnSignIn.isEnabled = true
                    binding.btnSignIn.backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), R.color.colorOrange)
                }
                Status.RUNNING -> {
                    binding.progressbarSignIn.show()
                    binding.btnSignIn.isEnabled = false
                    binding.btnSignIn.backgroundTintList =
                        ContextCompat.getColorStateList(
                            requireContext(),
                            R.color.colorGrayBtnEnable
                        )
                }
                Status.SUCCESS -> {
                    binding.progressbarSignIn.invisible()

                }
            }
        })
    }

}
