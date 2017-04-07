package me.rei_m.hbfavmaterial.presentation.fragment.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.EditBookmarkDialogFragment

@Subcomponent(modules = arrayOf(EditBookmarkDialogFragmentModule::class))
interface EditBookmarkDialogFragmentComponent {
    fun inject(fragment: EditBookmarkDialogFragment)
}
