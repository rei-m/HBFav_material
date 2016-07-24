package me.rei_m.hbfavmaterial.service

import rx.Observable

interface UserService {

    fun confirmExistingUserId(id: String): Observable<Boolean>
}
