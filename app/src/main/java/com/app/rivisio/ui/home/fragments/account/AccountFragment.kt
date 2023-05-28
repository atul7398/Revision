package com.app.rivisio.ui.home.fragments.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.app.rivisio.databinding.FragmentAccountBinding
import com.app.rivisio.ui.base.BaseFragment
import com.app.rivisio.ui.profile.ProfileActivity
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AccountFragment : BaseFragment() {

    private var _binding: FragmentAccountBinding? = null
    private val accountViewModel: AccountViewModel by viewModels()

    private val binding
        get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUp(view: View) {

        binding.logout.setOnClickListener {
            Identity.getSignInClient(requireActivity())
                .signOut()
                .addOnSuccessListener {
                    Timber.e("Logout successful")
                }
                .addOnFailureListener {
                    Timber.e("Logout failed")
                    showError("Logout failed")
                }

        }
        accountViewModel.userEmail.observe(this, Observer {
            binding.accountEmail.text = it
        })

        accountViewModel.userName.observe(this, Observer {
            binding.accountName.text = it
        })

        binding.profileContainer.setOnClickListener {
            startActivity(ProfileActivity.getStartIntent(requireContext()))
        }

        accountViewModel.getUserDetails()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}