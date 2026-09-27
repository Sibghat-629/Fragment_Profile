package com.example.fragment_profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).userDao()

    val allUsers: Flow<List<User>> = dao.getAllUsers()

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    fun selectUser(user: User?) {
        _selectedUser.value = user
    }

    fun insertUser(name: String, age: Int, email: String, onResult: (Boolean, String) -> Unit) {
        if (name.isBlank() || email.isBlank()) {
            onResult(false, "Name and Email cannot be empty")
            return
        }
        viewModelScope.launch {
            try {
                dao.insertUser(User(name = name.trim(), age = age, email = email.trim()))
                onResult(true, "User added successfully")
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            }
        }
    }

    fun updateUser(id: Long, name: String, age: Int, email: String, onResult: (Boolean, String) -> Unit) {
        if (name.isBlank() || email.isBlank()) {
            onResult(false, "Name and Email cannot be empty")
            return
        }
        viewModelScope.launch {
            try {
                dao.updateUser(User(id = id, name = name.trim(), age = age, email = email.trim()))
                _selectedUser.value = null
                onResult(true, "User updated successfully")
            } catch (e: Exception) {
                onResult(false, "Error: ${e.localizedMessage}")
            }
        }
    }

    fun deleteUser(user: User, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                dao.deleteUser(user)
                if (_selectedUser.value?.id == user.id) {
                    _selectedUser.value = null
                }
                onResult("User deleted")
            } catch (e: Exception) {
                onResult("Error deleting user: ${e.localizedMessage}")
            }
        }
    }
}
