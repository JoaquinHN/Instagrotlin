package com.darax.instagrotlin.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.darax.instagrotlin.R

class AlarmFragment: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)
        return view
    }
}