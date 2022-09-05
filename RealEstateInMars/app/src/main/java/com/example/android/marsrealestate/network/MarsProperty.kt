/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.network

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true) // was not there but i found it in the next project that's why i added it
data class MarsProperty(
    @Json(name = "id")
    val id: String,
    @Json(name = "price")
    val price: Double,
    @Json(name = "type")
    val type: String,
    @Json(name = "img_src")
    val imgSrcUrl: String


) : Parcelable {
    val isRental: Boolean
        get() = type == "rent"
}

//{
//    constructor(parcel: Parcel) : this(
//        parcel.readString(),
//        parcel.readDouble(),
//        parcel.readString(),
//        parcel.readString()
//    ) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(id)
//        parcel.writeDouble(price)
//        parcel.writeString(type)
//        parcel.writeString(imgSrcUrl)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<MarsProperty> {
//        override fun createFromParcel(parcel: Parcel): MarsProperty {
//            return MarsProperty(parcel)
//        }
//
//        override fun newArray(size: Int): Array<MarsProperty?> {
//            return arrayOfNulls(size)
//        }
//    }
//}
