package com.gmail.bodziowaty6978.model

import android.os.Parcel
import android.os.Parcelable

data class Product(val author: String? = "",
                   val name: String? = "",
                   val brand: String? = "",
                   val containerWeight: Double = 0.0,
                   val position: Int = 0,
                   val unit: String? = "",
                   val calories: Int = 0,
                   val carbs: Double = 0.0,
                   val protein: Double = 0.0,
                   val fat: Double = 0.0,
                   val barcode: String? = ""):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(author)
        parcel.writeString(name)
        parcel.writeString(brand)
        parcel.writeDouble(containerWeight)
        parcel.writeInt(position)
        parcel.writeString(unit)
        parcel.writeInt(calories)
        parcel.writeDouble(carbs)
        parcel.writeDouble(protein)
        parcel.writeDouble(fat)
        parcel.writeString(barcode)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}




