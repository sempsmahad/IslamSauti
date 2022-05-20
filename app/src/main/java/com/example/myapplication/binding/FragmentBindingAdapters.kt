package com.example.myapplication.binding

import androidx.fragment.app.Fragment
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {
//    @BindingAdapter(value = ["imageUrl", "imageRequestListener"], requireAll = false)
//    fun bindImage(imageView: ImageView, url: String?, listener: RequestListener<Drawable?>?) {
//        Glide.with(fragment).load(url).listener(listener).into(imageView)
//    }
}

