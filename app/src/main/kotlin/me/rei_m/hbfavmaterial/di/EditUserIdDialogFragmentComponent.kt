package me.rei_m.hbfavmaterial.di

import dagger.Subcomponent
import me.rei_m.hbfavmaterial.presentation.fragment.EditUserIdDialogFragment

@Subcomponent(modules = arrayOf(EditUserIdDialogFragmentModule::class))
interface EditUserIdDialogFragmentComponent {
    fun inject(fragment: EditUserIdDialogFragment)
}
