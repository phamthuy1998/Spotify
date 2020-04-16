package thuypham.ptithcm.spotify.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import thuypham.ptithcm.spotify.database.SongDbRepository
import thuypham.ptithcm.spotify.repository.*
import thuypham.ptithcm.spotify.viewmodel.*

object Injection {
    fun provideAccViewModelFactory(): ViewModelProvider.Factory {
        return AuthViewModelFactory(AccRepositoryImpl())
    }

    fun provideArtistViewModelFactory(): ViewModelProvider.Factory {
        return ArtistViewModelFactory(ArtistRepositoryImpl())
    }

    fun provideAlbumViewModelFactory(): ViewModelProvider.Factory {
        return AlbumViewModelFactory(AlbumRepositoryImpl())
    }

    fun provideBrowserViewModelFactory(): ViewModelProvider.Factory {
        return BrowserViewModelFactory(BrowserRepositoryImpl())
    }

    fun provideNowPlayingViewModelFactory(application: Application): ViewModelProvider.Factory {
        return NowPlayingViewModelFactory(SongRepositoryImpl(), SongDbRepository(application) )
    }

    fun provideYourMusicViewModelFactory(): ViewModelProvider.Factory {
        return YourMusicViewModelFactory(YourMusicRepositoryImpl())
    }

    fun provideProfileViewModelFactory(): ViewModelProvider.Factory {
        return ProfileViewModelFactory(ProfileRepositoryImpl())
    }

    fun provideCountryViewModelFactory(): ViewModelProvider.Factory {
        return CountryViewModelFactory(CountryRepositoryImpl())
    }

    fun provideMusicGenreViewModelFactory(): ViewModelProvider.Factory {
        return MusicGenreViewModelFactory(MusicGenreRepositoryImpl())
    }

    fun providePlaylistViewModelFactory(): ViewModelProvider.Factory {
        return PlaylistViewModelFactory(PlaylistRepositoryImpl())
    }

    fun provideSongViewModelFactory(): ViewModelProvider.Factory {
        return SongViewModelFactory(SongsRepositoryImpl())
    }

}