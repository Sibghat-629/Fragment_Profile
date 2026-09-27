package com.example.fragment_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.fragment_profile.databinding.FragmentUserFormBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserFormFragment : Fragment() {

    private var _binding: FragmentUserFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFormBinding.inflate(inflater, container, false)

        observeSelectedUser()
        setupSaveButton()

        return binding.root
    }

    private fun observeSelectedUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedUser.collectLatest { user ->
                if (user != null) {
                    binding.tvFormTitle.text = "Update User"
                    binding.etName.setText(user.name)
                    binding.etAge.setText(user.age.toString())
                    binding.etEmail.setText(user.email)
                    binding.btnSave.text = "Update User"
                } else {
                    binding.tvFormTitle.text = "Add New User"
                    binding.etName.text.clear()
                    binding.etAge.text.clear()
                    binding.etEmail.text.clear()
                    binding.btnSave.text = "Save User"
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val age = binding.etAge.text.toString().toIntOrNull() ?: 0
            val email = binding.etEmail.text.toString().trim()
            val currentUser = viewModel.selectedUser.value

            if (currentUser == null) {
                viewModel.insertUser(name, age, email) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        clearForm()
                        (activity as? MainActivity)?.navigateToTab(R.id.nav_users)
                    }
                }
            } else {
                viewModel.updateUser(currentUser.id, name, age, email) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        clearForm()
                        (activity as? MainActivity)?.navigateToTab(R.id.nav_users)
                    }
                }
            }
        }
    }

    private fun clearForm() {
        binding.etName.text.clear()
        binding.etAge.text.clear()
        binding.etEmail.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
