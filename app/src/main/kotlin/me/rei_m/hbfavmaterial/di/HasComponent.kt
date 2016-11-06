package me.rei_m.hbfavmaterial.di

interface HasComponent<out C> {
    fun getComponent(): C
}
