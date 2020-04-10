package thuypham.ptithcm.spotify.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.ui.auth.LoginActivity
import thuypham.ptithcm.spotify.ui.main.MainActivity

class SplashActivity : AppCompatActivity(),Animation.AnimationListener  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ivLogo.apply {
            val animation = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.anim_splash)
            startAnimation(animation)
            animation?.setAnimationListener(this@SplashActivity)
        }

    }

    override fun onAnimationRepeat(animation: Animation?) {

    }

    override fun onAnimationEnd(animation: Animation?) {
        val intent =
            if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser == null)
                Intent(this, LoginActivity::class.java)
            else
                Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onAnimationStart(animation: Animation?) {
    }
}
