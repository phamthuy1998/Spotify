package thuypham.ptithcm.spotify.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import thuypham.ptithcm.spotify.R

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("blurImageUrl")
fun bindImageBlur(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(BlurTransformation(70))
            .into(view)
    }
}

@BindingAdapter("imageFromUrlForAvt")
fun bindImageFromUrlAvatar(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty())
        Glide.with(view.context)
            .load(imageUrl)
            .error(R.drawable.ic_account)
            .into(view)
}
@BindingAdapter("imageInCircle")
fun bindImageBotPlaying(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty())
        Glide.with(view.context)
            .load(imageUrl)
            .error(R.color.colorOrange)
            .into(view)
}

@BindingAdapter("imageRoundedFromUrl")
fun bindImageRoundedFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(7)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("imageState")
fun setButtonState(view: ImageView, res: Int) {
    view.setImageResource(res)
}
@BindingAdapter("imagePlayState")
fun setImageBotPlay(view: ImageView, isPlaying: Boolean) {
    if(isPlaying) view.setImageResource(R.drawable.ic_bot_pause)
    else  view.setImageResource(R.drawable.ic_bot_play)
}