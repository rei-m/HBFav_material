package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.EntryListAdapter

@Subcomponent(modules = arrayOf(NewEntryFragmentModule::class))
interface NewEntryFragmentComponent : EntryListAdapter.Injector {
    fun inject(fragment: NewEntryFragment)
}
