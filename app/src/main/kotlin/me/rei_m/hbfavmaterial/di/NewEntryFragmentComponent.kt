package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.NewEntryFragment
import me.rei_m.hbfavmaterial.presentation.view.adapter.EntryListAdapter

@Subcomponent(modules = arrayOf(NewEntryFragmentModule::class))
interface NewEntryFragmentComponent : EntryListAdapter.Injector {
    fun inject(fragment: NewEntryFragment)
}
