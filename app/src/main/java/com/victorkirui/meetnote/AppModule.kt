package com.victorkirui.meetnote

import androidx.room.Room
import com.victorkirui.meetnote.data.repository.MyProfileRepositoryImpl
import com.victorkirui.meetnote.data.UserPreferencesRepository
import com.victorkirui.meetnote.data.util.ZXingQRCodeGeneratorImpl
import com.victorkirui.meetnote.data.local.AppDatabase
import com.victorkirui.meetnote.data.repository.ContactsRepositoryImpl
import com.victorkirui.meetnote.data.repository.EventsRepositoryImpl
import com.victorkirui.meetnote.domain.repository.ContactsRepository
import com.victorkirui.meetnote.domain.usecase.SaveMySocialProfileUseCase
import com.victorkirui.meetnote.domain.usecase.SaveMyWorkProfileUseCase
import com.victorkirui.meetnote.domain.usecase.SaveProfilePictureUseCase
import com.victorkirui.meetnote.domain.repository.EventsRepository
import com.victorkirui.meetnote.domain.repository.MyProfileRepository
import com.victorkirui.meetnote.domain.repository.QRCodeGenerator
import com.victorkirui.meetnote.presentation.contacts.add.AddContactViewModel
import com.victorkirui.meetnote.presentation.contacts.details.ContactDetailsViewModel
import com.victorkirui.meetnote.presentation.contacts.list.ContactListViewModel
import com.victorkirui.meetnote.presentation.account.AccountViewModel
import com.victorkirui.meetnote.presentation.contacts.more_details.ContactMoreDetailsViewModel
import com.victorkirui.meetnote.presentation.events.EventsViewModel
import com.victorkirui.meetnote.presentation.events.add.AddEventViewModel
import com.victorkirui.meetnote.presentation.events.details.EventDetailsViewModel
import com.victorkirui.meetnote.presentation.home.HomeScreenViewModel
import com.victorkirui.meetnote.presentation.profile.ProfileSetupSocialViewModel
import com.victorkirui.meetnote.presentation.profile.ProfileSetupViewModel
import com.victorkirui.meetnote.presentation.profile.QRCodeShareScreenViewModel
import com.victorkirui.meetnote.presentation.scan.ScannedContactViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // DAOs
    single { get<AppDatabase>().myProfile() }
    single { get<AppDatabase>().mySocialLinksDao() }
    single { get<AppDatabase>().eventsDao() }
    single { get<AppDatabase>().contactsDao() }

    // Repositories
    single<MyProfileRepository> { MyProfileRepositoryImpl(get(), get(), get()) }
    single { UserPreferencesRepository(get()) }
    single<EventsRepository> { EventsRepositoryImpl(get()) }
    single<ContactsRepository> { ContactsRepositoryImpl(get(), get()) }
    single<QRCodeGenerator> { ZXingQRCodeGeneratorImpl() }

    // UseCases
    single { SaveProfilePictureUseCase(get()) }
    single { SaveMySocialProfileUseCase(get(), get()) }
    single { SaveMyWorkProfileUseCase(get(), get()) }
    single { com.victorkirui.meetnote.domain.usecase.contact.SaveContactUseCase(get()) }
    single { com.victorkirui.meetnote.domain.usecase.contact.UpdateContactMoreDetailsUseCase(get()) }
    single { com.victorkirui.meetnote.domain.usecase.contact.GetContactSummaryUseCase(get()) }
    single { com.victorkirui.meetnote.domain.usecase.event.GetAllEventsUseCase(get()) }
    single { com.victorkirui.meetnote.domain.usecase.event.SaveEventUseCase(get()) }
    single { com.victorkirui.meetnote.domain.usecase.event.GetEventDetailsUseCase(get()) }
    single { com.victorkirui.meetnote.domain.usecase.event.DeleteEventUseCase(get()) }

    // ViewModels
    viewModelOf(::MainViewModel)
    viewModelOf(::ProfileSetupViewModel)
    viewModelOf(::ProfileSetupSocialViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::ContactListViewModel)
    viewModelOf(::AddContactViewModel)
    viewModelOf(::ContactDetailsViewModel)
    viewModelOf(::ContactMoreDetailsViewModel)
    viewModelOf(::EventsViewModel)
    viewModelOf(::AddEventViewModel)
    viewModelOf(::EventDetailsViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::QRCodeShareScreenViewModel)
    viewModelOf(::ScannedContactViewModel)
}
