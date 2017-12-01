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

package me.rei_m.hbfavmaterial.presentation.widget.fragment

import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.databinding.FragmentInitializeBinding
import me.rei_m.hbfavmaterial.di.ForFragment
import me.rei_m.hbfavmaterial.presentation.helper.Navigator
import me.rei_m.hbfavmaterial.presentation.helper.SnackbarFactory
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.InitializeFragmentViewModel
import me.rei_m.hbfavmaterial.viewmodel.widget.fragment.di.InitializeFragmentViewModelModule
import javax.inject.Inject

/**
 * アプリの初期処理を行うFragment.
 */
class InitializeFragment : DaggerFragment() {

    companion object {

        private const val KEY_USER_ID = "KEY_USER_ID"

        fun newInstance() = InitializeFragment()
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var progressDialog: ProgressDialog

    @Inject
    lateinit var viewModelFactory: InitializeFragmentViewModel.Factory

    private lateinit var binding: FragmentInitializeBinding

    private lateinit var viewModel: InitializeFragmentViewModel

    private var disposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            viewModelFactory.userId = savedInstanceState.getString(KEY_USER_ID)
        }
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(InitializeFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentInitializeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        disposable = CompositeDisposable()
        disposable?.addAll(viewModel.completeSetUpEvent.subscribe {
            navigator.navigateToMain()
            activity?.finish()
        }, viewModel.isLoading.subscribe {
            if (it) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }, viewModel.isRaisedError.subscribe {
            SnackbarFactory(binding.root).create(R.string.message_error_network).show()
        })
    }

    override fun onPause() {
        disposable?.dispose()
        disposable = null
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_USER_ID, viewModel.userId.get())
    }

    @dagger.Module
    abstract inner class Module {
        @ForFragment
        @ContributesAndroidInjector(modules = arrayOf(InitializeFragmentViewModelModule::class))
        internal abstract fun contributeInjector(): InitializeFragment
    }
}
