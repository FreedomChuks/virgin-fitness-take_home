package com.virginactive.core.data

import com.virginactive.core.model.Member
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberCache @Inject constructor() {
    @Volatile var member: Member? = null
}
