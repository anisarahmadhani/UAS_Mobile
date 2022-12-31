package com.pertamina.transkripnilai

import android.text.Editable


val String.editable: Editable get() = Editable.Factory.getInstance().newEditable(this)
val Int.editable: Editable get() = Editable.Factory.getInstance().newEditable(this.toString())
val Double.editable: Editable get() = Editable.Factory.getInstance().newEditable(this.toString())