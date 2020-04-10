package thuypham.ptithcm.spotify.di

import androidx.lifecycle.ViewModelProvider
import thuypham.ptithcm.spotify.repository.AccRepositoryImpl
import thuypham.ptithcm.spotify.viewmodel.AuthViewModelFactory

object Injection {
    fun provideAccViewModelFactory(): ViewModelProvider.Factory {
        return AuthViewModelFactory(AccRepositoryImpl())
    }
}