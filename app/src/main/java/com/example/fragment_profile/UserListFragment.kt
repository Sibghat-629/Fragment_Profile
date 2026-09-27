package com.example.fragment_profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment_profile.databinding.FragmentUserListBinding
import com.example.fragment_profile.databinding.ItemUserBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeUsers()

        binding.btnAddUser.setOnClickListener {
            viewModel.selectUser(null)
            // Navigate to Form Fragment
            (activity as? MainActivity)?.navigateToTab(R.id.nav_form)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(
            onItemClick = { user ->
                viewModel.selectUser(user)
                (activity as? MainActivity)?.navigateToTab(R.id.nav_form)
            },
            onDeleteClick = { user ->
                viewModel.deleteUser(user) { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = adapter
    }

    private fun observeUsers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allUsers.collectLatest { users ->
                adapter.submitList(users)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class UserAdapter(
        private val onItemClick: (User) -> Unit,
        private val onDeleteClick: (User) -> Unit
    ) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        private var users: List<User> = emptyList()

        fun submitList(newList: List<User>) {
            users = newList
            notifyDataSetChanged()
        }

        class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return UserViewHolder(binding)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = users[position]
            holder.binding.tvName.text = user.name
            holder.binding.tvDetails.text = "Age: ${user.age} | Email: ${user.email}"

            holder.itemView.setOnClickListener { onItemClick(user) }
            holder.binding.btnDelete.setOnClickListener { onDeleteClick(user) }
        }

        override fun getItemCount(): Int = users.size
    }
}
