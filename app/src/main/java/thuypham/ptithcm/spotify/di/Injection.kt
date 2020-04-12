package thuypham.ptithcm.spotify.di

import androidx.lifecycle.ViewModelProvider
import thuypham.ptithcm.spotify.repository.*
import thuypham.ptithcm.spotify.viewmodel.*

object Injection {
    fun provideAccViewModelFactory(): ViewModelProvider.Factory {
        return AuthViewModelFactory(AccRepositoryImpl())
    }

    fun provideArtistViewModelFactory(): ViewModelProvider.Factory {
        return ArtistViewModelFactory(ArtistRepositoryImpl())
    }

    fun provideBrowserViewModelFactory(): ViewModelProvider.Factory {
        return BrowserViewModelFactory(BrowserRepositoryImpl())
    }

    fun provideNowPlayingViewModelFactory(): ViewModelProvider.Factory {
        return NowPlayingViewModelFactory(SongRepositoryImpl())
    }

}