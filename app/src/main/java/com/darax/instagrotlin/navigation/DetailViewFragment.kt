package com.darax.instagrotlin.navigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.darax.instagrotlin.R
import com.darax.instagrotlin.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment: Fragment(){
    var firestore:FirebaseFirestore?=null
    var uid : String? =null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=LayoutInflater.from(activity).inflate(R.layout.fragment_detail,container,false)
        firestore= FirebaseFirestore.getInstance()
        uid =FirebaseAuth.getInstance().currentUser?.uid
        view.detailviewfragmen_reciclerview.adapter= DetailViewReciclerView()
        view.detailviewfragmen_reciclerview.layoutManager =LinearLayoutManager(activity)
        return view
    }
    inner class DetailViewReciclerView: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        private var contentUidList: ArrayList<String> = arrayListOf()
        init {
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, _ ->
                contentDTOs.clear()
                contentUidList.clear()
                //Aveces este query retorna null cuando se cierra sesion
                if(querySnapshot == null ) return@addSnapshotListener
                for(snapshot in querySnapshot.documents){
                    val item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
           val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail,parent,false)
            return CustomViewHolder(view)
        }
        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
        override fun getItemCount(): Int {
            return contentDTOs.size
        }
        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder=(holder as  CustomViewHolder).itemView
            //userid
            viewHolder.detailviewitem_profile_textview.text= contentDTOs[position].userId
            //image
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUrl).into(viewHolder.detailviewitem_imageview_content)
            //explain of content
            viewHolder.detailviewitem_explain_textview.text= contentDTOs[position].explain
            //likes
            viewHolder.detailviewitem_favoritecounter_textview.text="Me gusta "+ contentDTOs[position].favoriteCount

            //Este codigo es cuando el boton es clickeado
            viewHolder.detailviewitem_favorite_imageview.setOnClickListener{
                favoriteEvent(position)
            }
            //Este es para cuando la pagina este cargada
            if(contentDTOs[position].favorites.containsKey(uid)){
                //Este es el estado del Me gusta
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else{
                //este es el estado del No me gusta
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }
            //Este codigo sirve cuando la foto de perfil es clickeada
            viewHolder.detailviewitem_profile_image.setOnClickListener {
                val fragment = UserFragment()
                val bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[position].uid)
                bundle.putString("userId",contentDTOs[position].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
            }
        }
        //Agregar Me Gusta a las fotos
        private fun favoriteEvent(position: Int){
            val tsDoc= firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction{transaction ->
                val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                if(contentDTO!!.favorites.containsKey(uid)){
                    //Cuando el boton es clickeado
                    contentDTO.favoriteCount =contentDTO.favoriteCount -1
                     contentDTO.favorites.remove(uid)
                }else{
                    //Cuando el boton no es clickeado
                    contentDTO.favoriteCount=contentDTO.favoriteCount +1
                    contentDTO.favorites[uid!!] =true
                }
                transaction.set(tsDoc,contentDTO)
            }

        }
    }
}