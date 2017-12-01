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

package me.rei_m.hbfavmaterial.viewmodel.activity

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.model.UserModel
import me.rei_m.hbfavmaterial.presentation.widget.adapter.BookmarkPagerAdapter

class BaseDrawerActivityViewModel(userModel: UserModel) : ViewModel() {

    val userId: ObservableField<String> = ObservableField()

    val checkedNavId: ObservableInt = ObservableInt()

    val currentItem: ObservableInt = ObservableInt()

    val isVisiblePager: ObservableBoolean = ObservableBoolean(true)

    private val disposable = CompositeDisposable()

    init {
        disposable.addAll(userModel.user.subscribe {
            userId.set(it.id)
        })
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun onNavigationPageSelected(page: BookmarkPagerAdapter.Page) {
        currentItem.set(page.index)
        checkedNavId.set(page.navId)
    }

    fun onPageSelected(checkedNavId: Int) {
        if (checkedNavId == R.id.nav_explain_app || checkedNavId == R.id.nav_setting) {
            isVisiblePager.set(false)
        }
    }

    class Factory(private val userModel: UserModel) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BaseDrawerActivityViewModel::class.java)) {
                return BaseDrawerActivityViewModel(userModel) as T
            }
            throw IllegalArgumentException("Unknown class name")
        }
    }
}
