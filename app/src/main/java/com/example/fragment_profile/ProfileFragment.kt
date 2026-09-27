package com.example.fragment_profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.fragment_profile.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        observeUserCount()

        binding.btnTestBroadcastProfile.setOnClickListener {
            val intent = Intent("com.example.fragment_profile.TEST_BROADCAST")
            requireActivity().sendBroadcast(intent)
        }

        // Display current status from activity if available
        val activityStatus = (activity as? MainActivity)?.getStatusLogs() ?: "No logs"
        binding.tvBroadcastStatus.text = activityStatus

        return binding.root
    }

    private fun observeUserCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allUsers.collectLatest { users ->
                binding.tvUserCount.text = users.size.toString()
            }
        }
    }

    fun appendStatusLog(log: String) {
        if (_binding != null) {
            val current = binding.tvBroadcastStatus.text.toString()
            binding.tvBroadcastStatus.text = "$log\n$current"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
