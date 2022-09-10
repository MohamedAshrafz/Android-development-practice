package com.udacity.asteroidradar

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.AsteroidRecyclerViewAdaptor

@BindingAdapter("asteroidsList")
// take special care of the nullable list here
fun bindAsteroidsList(recyclerView: RecyclerView, list: List<Asteroid>?) {

    val adapter = recyclerView.adapter as AsteroidRecyclerViewAdaptor
    adapter.submitList(list)
}

@BindingAdapter("setDayPicture")
fun bindPictureOfDay(imageView: ImageView, pictureOfDay: PictureOfDay?) {
    val imageUrl = pictureOfDay?.url
    imageUrl?.let {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()
        Picasso.with(imageView.context)
            .load(imgUri)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .error(R.drawable.asteroid_hazardous)
            .into(imageView)
    }
    if (pictureOfDay != null) {
        imageView.contentDescription =
            imageView.context.getString(R.string.image_of_the_day) + " " + pictureOfDay.title
    } else {
        imageView.contentDescription =
            imageView.context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
    }
}

@BindingAdapter("setPictureTitle")
fun bindPictureTitle(textView: TextView, title: String?) {
    if (title != null) {
        textView.text = title
        textView.contentDescription = title
    } else {
        textView.text = textView.context.getString(R.string.image_of_the_day)
        textView.contentDescription = textView.context.getString(R.string.image_of_the_day)
    }
}

@BindingAdapter("statusIcon")
fun ImageView.bindAsteroidStatusImage(isHazardous: Boolean) {
    if (isHazardous) {
        setImageResource(R.drawable.ic_status_potentially_hazardous)
        contentDescription = context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        setImageResource(R.drawable.ic_status_normal)
        contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("asteroidStatusImage")
fun ImageView.bindDetailsStatusImage(isHazardous: Boolean) {
    contentDescription = if (isHazardous) {
        setImageResource(R.drawable.asteroid_hazardous)
        context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        setImageResource(R.drawable.asteroid_safe)
        context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun TextView.bindTextViewToAstronomicalUnit(number: Double) {
    text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun TextView.bindTextViewToKmUnit(number: Double) {
    text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun TextView.bindTextViewToDisplayVelocity(number: Double) {
    text = String.format(context.getString(R.string.km_s_unit_format), number)
}
