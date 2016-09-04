package me.rei_m.hbfavmaterial.usecase

interface UpdateUserUsecase {

    fun updateUserId(userId: String)

    fun updateIsCheckedPostBookmarkOpen(isChecked: Boolean)

    fun updateIsCheckedPostBookmarkReadAfter(isChecked: Boolean)
}
