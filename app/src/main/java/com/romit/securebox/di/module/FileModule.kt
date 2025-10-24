package com.romit.securebox.di.module

import com.romit.securebox.data.repository.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@InstallIn(SingletonComponent::class)
//@Module
//object FileModule {
//    @Singleton
//    @Provides
//    fun providesRepository(): FileRepository {
//        return FileRepository()
//    }
//}