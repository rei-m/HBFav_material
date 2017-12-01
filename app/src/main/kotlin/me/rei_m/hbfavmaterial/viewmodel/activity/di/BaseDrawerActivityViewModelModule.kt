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

package me.rei_m.hbfavmaterial.viewmodel.activity.di

import dagger.Module
import dagger.Provides
import me.rei_m.hbfavmaterial.di.ForActivity
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.viewmodel.activity.BaseDrawerActivityViewModel

@Module
class BaseDrawerActivityViewModelModule {
    @Provides
    @ForActivity
    internal fun provideViewModelFactory(userModel: UserModel): BaseDrawerActivityViewModel.Factory =
            BaseDrawerActivityViewModel.Factory(userModel)
}
