package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.HotEntryFragment
import me.rei_m.hbfavmaterial.presentation.widget.adapter.EntryListAdapter

@Subcomponent(modules = arrayOf(HotEntryFragmentModule::class))
interface HotEntryFragmentComponent : EntryListAdapter.Injector {
    fun inject(fragment: HotEntryFragment)
}
