/*
 * Copyright (c) 2017. Rei Matsushita
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package me.rei_m.hbfavmaterial.viewmodel.widget.dialog.di

import android.content.Context
import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.viewmodel.widget.dialog.EditUserIdDialogFragmentViewModel

@Module
class EditUserIdDialogFragmentViewModelModule {
    @Provides
    @ForFragment
    internal fun provideViewModelFactory(context: Context,
                                         userModel: UserModel): EditUserIdDialogFragmentViewModel.Factory =
            EditUserIdDialogFragmentViewModel.Factory(userModel, context.getString(R.string.message_error_input_user_id))
}
