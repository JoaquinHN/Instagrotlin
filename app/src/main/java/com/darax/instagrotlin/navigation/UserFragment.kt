package com.darax.instagrotlin.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.darax.instagrotlin.LoginActivity
import com.darax.instagrotlin.MainActivity
import com.darax.instagrotlin.R
import com.darax.instagrotlin.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment: Fragment(){
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    private var auth : FirebaseAuth? = null
    private var currentUserUid : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView=LayoutInflater.from(activity).inflate(R.layout.fragment_user,container,false)
        uid=arguments?.getString("destinationUid")
        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        currentUserUid=auth?.currentUser?.uid
        if(uid==currentUserUid){
            //Mi Perfil
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity,LoginActivity::class.java))
            }
        }else{
            //El perfil de otro usuario
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
            val mainActivity =(activity as MainActivity)
            mainActivity.toolbar_username?.text = arguments?.getString("userId")
            mainActivity.toolbar_btn_back?.setOnClickListener {
                mainActivity.bottom_navigation.selectedItemId=R.id.action_home
            }
            mainActivity.toolbar_title_image?.visibility= View.GONE
            mainActivity.toolbar_username?.visibility = View.VISIBLE
            mainActivity.toolbar_btn_back.visibility = View.VISIBLE
        }
        fragmentView?.account_reyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_reyclerview?.layoutManager= GridLayoutManager(activity!!,3)
        return fragmentView
    }
    inner class UserFragmentRecyclerViewAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        private var contentDTO: ArrayList<ContentDTO> = arrayListOf()
        init {
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, _ ->
                //Aveces este query retorna null cuando se cierra sesion
                if(querySnapshot == null ) return@addSnapshotListener
                //Conseguir los datos
                for(snapshot in querySnapshot.documents){
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
                }
                fragmentView?.account_tv_post_count?.text =contentDTO.size.toString()
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
           val with = resources.displayMetrics.widthPixels / 3
            val imageview= ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(with,with)
            return CustomViewHolder(imageview)
        }
        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview)

        override fun getItemCount(): Int {
           return  contentDTO.size
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val imageView = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTO[position].imageUrl).apply(RequestOptions().centerCrop()).into(imageView)
        }

    }
}