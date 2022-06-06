package com.canerture.videocallingapp.presentation.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.canerture.videocallingapp.R
import com.canerture.videocallingapp.databinding.FragmentLoginBinding
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.AGConnectAuthCredential
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AGConnectAuth.getInstance().currentUser?.let {
            findNavController().navigate(R.id.loginToMain)
        }

        lifecycleScope.launch {

            binding.imgLogo.apply {
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(1500)
                    .setListener(null)
            }

            delay(2000)

            binding.tvLogin.apply {
                alpha = 1f
                visibility = View.VISIBLE
                animate()
                    .alpha(0f)
                    .setDuration(500)
                    .setListener(null)
            }

            binding.btnLogin.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setListener(null)
                setOnClickListener {
                    AGConnectAuth.getInstance()
                        .signIn(requireActivity(), AGConnectAuthCredential.HMS_Provider)
                        .addOnSuccessListener {
                            findNavController().navigate(R.id.loginToMain)
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}