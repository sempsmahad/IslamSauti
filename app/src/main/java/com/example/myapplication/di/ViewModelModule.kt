package com.example.myapplication.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ui.SautiViewModelFactory
import com.example.myapplication.ui.summondetail.SummonDetailViewModel
import com.example.myapplication.ui.summons.SummonsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SummonsViewModel::class)
    abstract fun bindSummonsViewModel(summonsViewModel: SummonsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SummonDetailViewModel::class)
    abstract fun bindSummonDetailViewModel(summonDetailViewModel: SummonDetailViewModel): ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: SautiViewModelFactory): ViewModelProvider.Factory
}
