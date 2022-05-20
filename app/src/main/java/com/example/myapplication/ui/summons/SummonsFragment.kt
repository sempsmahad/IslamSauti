package com.example.myapplication.ui.summons

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.android.example.github.util.autoCleared
import com.example.myapplication.AppExecutors
import com.example.myapplication.binding.FragmentDataBindingComponent
import com.example.myapplication.data.model.Summon
import com.example.myapplication.di.Injectable
import javax.inject.Inject

class SummonsFragment : Fragment() , Injectable {

        @Inject
        lateinit var viewModelFactory: ViewModelProvider.Factory

        val summonsViewModel: SummonsViewModel by viewModels {
            viewModelFactory
        }

        @Inject
        lateinit var appExecutors: AppExecutors

        // mutable for testing
        var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
        var binding by autoCleared<Summon>()

}