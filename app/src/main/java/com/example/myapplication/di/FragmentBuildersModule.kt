package com.example.myapplication.di

import com.example.myapplication.ui.summons.SummonsFragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSummonFragment(): SummonsFragment

}
