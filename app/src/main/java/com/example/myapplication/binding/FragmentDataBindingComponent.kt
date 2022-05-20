package com.example.myapplication.binding

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import com.example.myapplication.binding.FragmentBindingAdapters

/**
 * A Data Binding Component implementation for fragments.
 */
class FragmentDataBindingComponent(fragment: Fragment) : DataBindingComponent {
    private val adapter = FragmentBindingAdapters(fragment)
    fun getFragmentBindingAdapters() = adapter
}
