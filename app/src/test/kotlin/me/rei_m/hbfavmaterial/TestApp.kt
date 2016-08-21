package me.rei_m.hbfavmaterial

import me.rei_m.hbfavmaterial.di.ApplicationComponent
import me.rei_m.hbfavmaterial.di.ApplicationModule
import me.rei_m.hbfavmaterial.di.DaggerTestApplicationComponent
import me.rei_m.hbfavmaterial.di.InfraLayerModule

open class TestApp : App() {
    override fun createApplicationComponent(): ApplicationComponent {
        return DaggerTestApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .infraLayerModule(InfraLayerModule())
                .build()
    }
}
